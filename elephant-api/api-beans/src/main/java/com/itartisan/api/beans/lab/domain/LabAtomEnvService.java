package com.itartisan.api.beans.lab.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.itartisan.common.core.domain.BaseEntity;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class LabAtomEnvService extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    @TableField(exist = false)
    private String instanceName;
    @TableField(exist = false)
    private String podInstanceName;
    private Long envId;
    private String podName;
    private String type;
    private String namespace;
    @TableField(exist = false)
    private List<LabAtomEnvServicePorts> ports;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getPodInstanceName() {
        return podInstanceName;
    }

    public void setPodInstanceName(String podInstanceName) {
        this.podInstanceName = podInstanceName;
    }

    public Long getEnvId() {
        return envId;
    }

    public void setEnvId(Long envId) {
        this.envId = envId;
    }

    public String getPodName() {
        return podName;
    }

    public void setPodName(String podName) {
        this.podName = podName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public List<LabAtomEnvServicePorts> getPorts() {
        return ports;
    }

    public void setPorts(List<LabAtomEnvServicePorts> ports) {
        this.ports = ports;
    }
}
