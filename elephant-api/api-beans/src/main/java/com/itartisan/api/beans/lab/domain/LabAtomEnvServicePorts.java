package com.itartisan.api.beans.lab.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.itartisan.common.core.domain.BaseEntity;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class LabAtomEnvServicePorts extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long serviceId;
    private Long envId;
    private Long port;
    private String name;
    private Long targetPort;
    private Long nodePort;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public Long getEnvId() {
        return envId;
    }

    public void setEnvId(Long envId) {
        this.envId = envId;
    }

    public Long getPort() {
        return port;
    }

    public void setPort(Long port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTargetPort() {
        return targetPort;
    }

    public void setTargetPort(Long targetPort) {
        this.targetPort = targetPort;
    }

    public Long getNodePort() {
        return nodePort;
    }

    public void setNodePort(Long nodePort) {
        this.nodePort = nodePort;
    }
}
