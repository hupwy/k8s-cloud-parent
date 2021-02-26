package com.itartisan.api.beans.lab.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ProcessModel implements Serializable {

    /**
     * 容器部分 start
     **/
    private Long envId;                           // 实验模板ID
    private String envName;                       // 实验模板名称
    private String envTech;                       // 环境实现技术(0:容器 1:虚拟机)
    private List<TypeModel> models;               // 实例节点(集群节点，例:pod)
    private String environment;                   // 通用的环境变量

    /**
     * 容器部分 end
     **/

    public Long getEnvId() {
        return envId;
    }

    public void setEnvId(Long envId) {
        this.envId = envId;
    }

    public String getEnvName() {
        return envName;
    }

    public void setEnvName(String envName) {
        this.envName = envName;
    }

    public String getEnvTech() {
        return envTech;
    }

    public void setEnvTech(String envTech) {
        this.envTech = envTech;
    }

    public List<TypeModel> getModels() {
        return models;
    }

    public void setModels(List<TypeModel> models) {
        this.models = models;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }
}
