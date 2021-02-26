package com.itartisan.lab.beans.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.itartisan.common.core.domain.BaseEntity;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class LabAtomEnvPod extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long podId;
    private String podName;
    @TableField(exist = false)
    private String instanceName;
    private String serviceName;
    @TableField(exist = false)
    private String serviceInstanceName;
    private Long envId;
    private String imageName;
    private String namespace;
    private String protocol;
    private Integer replicas;
    private Integer cpu;
    private Integer cpuRequests;
    private Integer gpu;
    private Long memory;
    private Long memoryRequests;
    @TableField(exist = false)
    private List<LabAtomEnvPodEnv> env;
    @TableField(exist = false)
    private List<LabAtomEnvPodPorts> ports;
    @TableField(exist = false)
    private List<LabAtomEnvPodVolumes> volumes;

    public Long getPodId() {
        return podId;
    }

    public void setPodId(Long podId) {
        this.podId = podId;
    }

    public String getPodName() {
        return podName;
    }

    public void setPodName(String podName) {
        this.podName = podName;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceInstanceName() {
        return serviceInstanceName;
    }

    public void setServiceInstanceName(String serviceInstanceName) {
        this.serviceInstanceName = serviceInstanceName;
    }

    public Long getEnvId() {
        return envId;
    }

    public void setEnvId(Long envId) {
        this.envId = envId;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Integer getReplicas() {
        return replicas;
    }

    public void setReplicas(Integer replicas) {
        this.replicas = replicas;
    }

    public Integer getCpu() {
        return cpu;
    }

    public void setCpu(Integer cpu) {
        this.cpu = cpu;
    }

    public Integer getCpuRequests() {
        return cpuRequests;
    }

    public void setCpuRequests(Integer cpuRequests) {
        this.cpuRequests = cpuRequests;
    }

    public Integer getGpu() {
        return gpu;
    }

    public void setGpu(Integer gpu) {
        this.gpu = gpu;
    }

    public Long getMemory() {
        return memory;
    }

    public void setMemory(Long memory) {
        this.memory = memory;
    }

    public Long getMemoryRequests() {
        return memoryRequests;
    }

    public void setMemoryRequests(Long memoryRequests) {
        this.memoryRequests = memoryRequests;
    }

    public List<LabAtomEnvPodEnv> getEnv() {
        return env;
    }

    public void setEnv(List<LabAtomEnvPodEnv> env) {
        this.env = env;
    }

    public List<LabAtomEnvPodPorts> getPorts() {
        return ports;
    }

    public void setPorts(List<LabAtomEnvPodPorts> ports) {
        this.ports = ports;
    }

    public List<LabAtomEnvPodVolumes> getVolumes() {
        return volumes;
    }

    public void setVolumes(List<LabAtomEnvPodVolumes> volumes) {
        this.volumes = volumes;
    }
}
