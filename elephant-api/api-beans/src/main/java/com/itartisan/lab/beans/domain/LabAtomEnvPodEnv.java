package com.itartisan.lab.beans.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.itartisan.common.core.domain.BaseEntity;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class LabAtomEnvPodEnv extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long podId;
    private Long envId;
    private String envName;
    private String envValue;

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

    public String getEnvName() {
        return envName;
    }

    public void setEnvName(String envName) {
        this.envName = envName;
    }

    public String getEnvValue() {
        return envValue;
    }

    public void setEnvValue(String envValue) {
        this.envValue = envValue;
    }
}
