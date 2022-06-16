# 安装手册

## elasticsearch:7.8.1安装

1. 从阿里云上拉取elasticSearch7.8.1镜像

   ```shell
   sudo docker pull registry.cn-hangzhou.aliyuncs.com/elastic-stack-repo/elasticsearch:7.8.1
   ```

2. 重命名镜像

   ```shell
   sudo docker tag registry.cn-hangzhou.aliyuncs.com/elastic-stack-repo/elasticsearch:7.8.1 elasticsearch:7.8.1
   ```

3. 删除镜像

   ````shell 
   sudo docker rmi -f registry.cn-hangzhou.aliyuncs.com/elastic-stack-repo/elasticsearch:7.8.1
   ````

4. 以单例模式创建elasticsearch容器

   ```shell
   docker run -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" elasticsearch:7.8.1
   ```

   

## kibana:7.8.1安装   

1. 从阿里云上拉取kibana7.8.1镜像

   ```shell
   sudo docker pull registry.cn-hangzhou.aliyuncs.com/elastic-stack-repo/kibana:7.8.1
   ```

2. 重命名镜像

   ```shell
   sudo docker tag registry.cn-hangzhou.aliyuncs.com/elastic-stack-repo/kibana:7.8.1 kibana:7.8.1
   ```

3. 删除镜像

   ```shell
   sudo docker rmi -f registry.cn-hangzhou.aliyuncs.com/elastic-stack-repo/kibana:7.8.1
   ```

4. 创建kibana容器

   ```shell
   sudo docker run --link 9ef08846aa82:elasticsearch -p 5601:5601 kibana:7.8.1
   ```

## metricbeat:7.8.1安装

1. 从阿里云上拉取kibana7.8.1镜像

   ```shell
   sudo docker pull registry.cn-hangzhou.aliyuncs.com/elastic-stack-repo/metricbeat:7.8.1
   ```

2. 下载配置文件

   ```shell
   curl -L -O https://raw.githubusercontent.com/elastic/beats/7.8/deploy/docker/metricbeat.docker.yml
   ```

   修改配置

   ```shell
   reload.enabled: true
     setup.kibana:
       host: "http://172.17.3.111:5601"
   ```
   
3. 重删除镜像

   ```shell
   sudo docker rmi -f registry.cn-hangzhou.aliyuncs.com/elastic-stack-repo/metricbeat:7.8.1
   ```

3. 创建metricbeat容器

   ```shell
   docker run -d \
     --name=metricbeat \
     --user=root \
     --volume="$(pwd)/metricbeat.docker.yml:/usr/share/metricbeat/metricbeat.yml:ro" \
     --volume="/var/run/docker.sock:/var/run/docker.sock:ro" \
     --volume="/sys/fs/cgroup:/hostfs/sys/fs/cgroup:ro" \
     --volume="/proc:/hostfs/proc:ro" \
     --volume="/:/hostfs:ro" \
     metricbeat:7.8.1 metricbeat -e \
     -E output.elasticsearch.hosts=["172.17.3.111:9200"]
   ```
   
5. 交互式进入容器

   ```shell
   sudo docker exec -it metricbeat bash
   ```

6. 启动system和容器监控modules:

   ```shell
metricbeat modules enable system
   metricbeat modules enable docker
   ```

7. 导入dashboards

   ```shell
   metricbeat setup --dashboards system
   ```
   
8. 初始化配置

   ```shell
   metricbeat -e \
    -E output.elasticsearch.hosts=["172.17.68.1:9200"] \
    -E setup.kibana.host=172.17.68.1:5601 \
    -E output.elasticsearch.username=elastic \
    -E output.elasticsearch.password=changeme
   ```
