# GPU共享扩展

## 问题背景

> Kubernetes服务都提供了Nvidia GPU容器调度能力，但是通常都是将一个GPU卡分配给一个容器。这可以实现比较好的隔离性，确保使用GPU的应用不会被其他应用影响；对于深度学习模型训练的场景非常适合，但是如果对于模型开发和模型预测的场景就会比较浪费。
>
> Kubernetes 集群中，GPU资源作为一种外部资源([extended resources](https://kubernetes.io/docs/concepts/configuration/manage-resources-containers/#extended-resources))，部署Nvidia官方提供的插件([k8s-device-plugin](https://kubernetes.io/zh/docs/concepts/extend-kubernetes/compute-storage-net/device-plugins/))后，GPU资源在节点上是以个数暴露给kubernetes集群来进行调度的，也就是说如果有两个后端应用pod需要使用到GPU资源，但集群节点上只有一张GPU物理卡的情况下，会导致两个后端应用容器中仅有一个可以正常运行，另一个pod则会处于pending状态。
>
> 详细请参考https://kubernetes.io/zh/docs/tasks/manage-gpus/scheduling-gpus/

想要在Kubenentes集群中实现GPUshare，可以采用阿里云于2019年开源的[gpushare-device-plugin](https://github.com/AliyunContainerService/gpushare-device-plugin)：

> Nvidia GPU sharing device plugin是一个Daemonset，允许你自动:
> - 将节点上的GPU资源以显存及显卡数的形式暴露给k8s
> - 集群中的容器可以通过声明显存来共享GPU资源

## 目标

将节点上的GPU资源以GPU显存或者GPU个数的形式暴露给Kubernetes，实现多容器共享GPU资源。



## 部署

[官方安装向导](https://github.com/AliyunContainerService/gpushare-scheduler-extender/blob/master/docs/install.md)

### 0.准备 GPU Node

安装 gpushare-device-plugin 之前，确保在GPU节点上已经安装Nvidia-Driver以及Nvidia-Docker2，同时已将docker的默认运行时设置为nvidia，配置文件如下：/etc/docker/daemon.json

````json
{
    "default-runtime": "nvidia",
    "runtimes": {
        "nvidia": {
            "path": "/usr/bin/nvidia-container-runtime",
            "runtimeArgs": []
        }
    }
}
````

### 1. 部署 GPU share scheduler extender

```shell
cd /etc/kubernetes/
curl -O https://raw.githubusercontent.com/AliyunContainerService/gpushare-scheduler-extender/master/config/scheduler-policy-config.json
```

`scheduler-policy-config.json`文件如下

```json
{
  "kind": "Policy",
  "apiVersion": "v1",
  "extenders": [
    {
      "urlPrefix": "http://127.0.0.1:32766/gpushare-scheduler",
      "filterVerb": "filter",
      "bindVerb":   "bind",
      "enableHttps": false,
      "nodeCacheCapable": true,
      "managedResources": [
        {
          "name": "aliyun.com/gpu-mem",
          "ignoredByScheduler": false
        }
      ],
      "ignorable": false
    }
  ]
}
```

```shell
cd /tmp/
curl -O https://raw.githubusercontent.com/AliyunContainerService/gpushare-scheduler-extender/master/config/gpushare-schd-extender.yaml
kubectl create -f gpushare-schd-extender.yaml
```

`gpushare-schd-extender.yaml`文件如下

````yaml
# rbac.yaml
---
kind: ClusterRole
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: gpushare-schd-extender
rules:
- apiGroups:
  - ""
  resources:
  - nodes
  verbs:
  - get
  - list
  - watch
- apiGroups:
  - ""
  resources:
  - events
  verbs:
  - create
  - patch
- apiGroups:
  - ""
  resources:
  - pods
  verbs:
  - update
  - patch
  - get
  - list
  - watch
- apiGroups:
  - ""
  resources:
  - bindings
  - pods/binding
  verbs:
  - create
- apiGroups:
  - ""
  resources:
  - configmaps
  verbs:
  - get
  - list
  - watch
---
apiVersion: v1
kind: ServiceAccount
metadata:
  name: gpushare-schd-extender
  namespace: kube-system
---
kind: ClusterRoleBinding
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: gpushare-schd-extender
  namespace: kube-system
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: gpushare-schd-extender
subjects:
- kind: ServiceAccount
  name: gpushare-schd-extender
  namespace: kube-system

# deployment yaml
---
kind: Deployment
apiVersion: apps/v1
metadata:
  name: gpushare-schd-extender
  namespace: kube-system
spec:
  replicas: 1
  strategy:
    type: Recreate
  selector:
    matchLabels:
        app: gpushare
        component: gpushare-schd-extender
  template:
    metadata:
      labels:
        app: gpushare
        component: gpushare-schd-extender
      annotations:
        scheduler.alpha.kubernetes.io/critical-pod: ''
    spec:
      hostNetwork: true
      tolerations:
      - effect: NoSchedule
        operator: Exists
        key: node-role.kubernetes.io/master
      - effect: NoSchedule
        operator: Exists
        key: node.cloudprovider.kubernetes.io/uninitialized
      nodeSelector:
         node-role.kubernetes.io/master: ""
      serviceAccount: gpushare-schd-extender
      containers:
        - name: gpushare-schd-extender
          image: registry.cn-hangzhou.aliyuncs.com/acs/k8s-gpushare-schd-extender:1.11-d170d8a
          env:
          - name: LOG_LEVEL
            value: debug
          - name: PORT
            value: "12345"

# service.yaml            
---
apiVersion: v1
kind: Service
metadata:
  name: gpushare-schd-extender
  namespace: kube-system
  labels:
    app: gpushare
    component: gpushare-schd-extender
spec:
  type: NodePort
  ports:
  - port: 12345
    name: http
    targetPort: 12345
    nodePort: 32766
  selector:
    # select app=ingress-nginx pods
    app: gpushare
    component: gpushare-schd-extender   
````

### 2.修改 scheduler 配置文件

将 `/etc/kubernetes/scheduler-policy-config.json` 加到 scheduler 配置文件中 (`/etc/kubernetes/manifests/kube-scheduler.yaml`). 

这是一个成功添加配置的例子 [kube-scheduler.yaml](https://github.com/AliyunContainerService/gpushare-scheduler-extender/blob/master/config/kube-scheduler.yaml)

> 注意: 如果当前Kubernetes的默认scheduler是以静态pod的方式部署的, 不要在这个路径下编辑配置文件/etc/kubernetes/manifest. 应该在该路径外编辑 yaml文件后将其复制到/etc/kubernetes/manifest/路径下, 然后Kubernetes会自动更新scheduler.

#### 2.1.在 scheduler 参数中加入 Policy config file 参数

```yaml
- --policy-config-file=/etc/kubernetes/scheduler-policy-config.json
```

#### 2.2.在Pod Spec中加入volume mount

```yaml
- mountPath: /etc/kubernetes/scheduler-policy-config.json
  name: scheduler-policy-config
  readOnly: true
```

````yaml
- hostPath:
      path: /etc/kubernetes/scheduler-policy-config.json
      type: FileOrCreate
  name: scheduler-policy-config
````

### 3.部署Device Plugin

````shell
wget https://raw.githubusercontent.com/AliyunContainerService/gpushare-device-plugin/master/device-plugin-rbac.yaml
kubectl create -f device-plugin-rbac.yaml
````

`device-plugin-rbac.yaml`文件如下

```yaml
# rbac.yaml
---
kind: ClusterRole
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: gpushare-device-plugin
rules:
- apiGroups:
  - ""
  resources:
  - nodes
  verbs:
  - get
  - list
  - watch
- apiGroups:
  - ""
  resources:
  - events
  verbs:
  - create
  - patch
- apiGroups:
  - ""
  resources:
  - pods
  verbs:
  - update
  - patch
  - get
  - list
  - watch
- apiGroups:
  - ""
  resources:
  - nodes/status
  verbs:
  - patch
  - update
---
apiVersion: v1
kind: ServiceAccount
metadata:
  name: gpushare-device-plugin
  namespace: kube-system
---
kind: ClusterRoleBinding
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: gpushare-device-plugin
  namespace: kube-system
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: gpushare-device-plugin
subjects:
- kind: ServiceAccount
  name: gpushare-device-plugin
  namespace: kube-system
```



```shell
wget https://raw.githubusercontent.com/AliyunContainerService/gpushare-device-plugin/master/device-plugin-ds.yaml
kubectl create -f device-plugin-ds.yaml
```

`device-plugin-ds.yaml`文件如下

```yaml
apiVersion: apps/v1
kind: DaemonSet
metadata:
  name: gpushare-device-plugin-ds
  namespace: kube-system
spec:
  selector:
    matchLabels:
        component: gpushare-device-plugin
        app: gpushare
        name: gpushare-device-plugin-ds
  template:
    metadata:
      annotations:
        scheduler.alpha.kubernetes.io/critical-pod: ""
      labels:
        component: gpushare-device-plugin
        app: gpushare
        name: gpushare-device-plugin-ds
    spec:
      serviceAccount: gpushare-device-plugin
      hostNetwork: true
      nodeSelector:
        gpushare: "true"
      containers:
      - image: registry.cn-hangzhou.aliyuncs.com/acs/k8s-gpushare-plugin:v2-1.11-aff8a23
        name: gpushare
        # Make this pod as Guaranteed pod which will never be evicted because of node's resource consumption.
        command:
          - gpushare-device-plugin-v2
          - -logtostderr
          - --v=5
          - --memory-unit=GiB
        resources:
          limits:
            memory: "300Mi"
            cpu: "1"
          requests:
            memory: "300Mi"
            cpu: "1"
        env:
        - name: KUBECONFIG
          value: /etc/kubernetes/kubelet.conf
        - name: NODE_NAME
          valueFrom:
            fieldRef:
              fieldPath: spec.nodeName
        securityContext:
          allowPrivilegeEscalation: false
          capabilities:
            drop: ["ALL"]
        volumeMounts:
          - name: device-plugin
            mountPath: /var/lib/kubelet/device-plugins
      volumes:
        - name: device-plugin
          hostPath:
            path: /var/lib/kubelet/device-plugins
```

>  可以通过修改DaemonSet中的参数来指定显存的单位是MiB/GiB
> 注意: 请移除 GPU device plugin, 例如 [nvidia-device-plugin](https://github.com/NVIDIA/k8s-device-plugin/blob/v1.11/nvidia-device-plugin.yml), 可以通过命令 `kubectl delete ds -n kube-system nvidia-device-plugin-daemonset` 来删除.

### 4.给 gpushare 节点打标签

```shell
kubectl label node <target_node> gpushare=true
```

For example:

````shell
kubectl label node mynode gpushare=true
````

### 5.安装Kubectl 扩展

#### 5.1 需要已安装 kubectl 1.12+

> 如果以满足此条件，请忽略

````shell
curl -LO https://storage.googleapis.com/kubernetes-release/release/v1.12.1/bin/linux/amd64/kubectl
chmod +x ./kubectl
sudo mv ./kubectl /usr/bin/kubectl
````

#### 5.2下载并安装 kubectl 扩展功能

```shell
cd /usr/bin/
wget https://github.com/AliyunContainerService/gpushare-device-plugin/releases/download/v0.3.0/kubectl-inspect-gpushare
chmod u+x /usr/bin/kubectl-inspect-gpushare
```