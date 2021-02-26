package com.itartisan.api.beans.lab.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.itartisan.common.core.domain.BaseEntity;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class LabAtomEnvPodPorts extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long podId;
    private Long envId;
    private Long podPort;
    private String portName;
    private String protocol;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPodId() {
        return podId;
    }

    public void setPodId(Long podId) {
        this.podId = podId;
    }

    public Long getEnvId() {
        return envId;
    }

    public void setEnvId(Long envId) {
        this.envId = envId;
    }

    public Long getPodPort() {
        return podPort;
    }

    public void setPodPort(Long podPort) {
        this.podPort = podPort;
    }

    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
}
