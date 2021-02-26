package com.itartisan.api.beans.lab.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.itartisan.api.beans.lab.domain.LabAtomEnvPod;
import com.itartisan.api.beans.lab.domain.LabAtomEnvService;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TypeModel implements Serializable {
    /**
     * 组件类型
     **/
    private ResourceType type;
    /**
     * Pod组件
     **/
    private LabAtomEnvPod pod;
    /**
     * Service组件
     **/
    private LabAtomEnvService service;
    /**
     * 通用环境变量
     **/
    protected String environment;

    public ResourceType getType() {
        return type;
    }

    public void setType(ResourceType type) {
        this.type = type;
    }

    public LabAtomEnvPod getPod() {
        return pod;
    }

    public void setPod(LabAtomEnvPod pod) {
        this.pod = pod;
    }

    public LabAtomEnvService getService() {
        return service;
    }

    public void setService(LabAtomEnvService service) {
        this.service = service;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }
}
