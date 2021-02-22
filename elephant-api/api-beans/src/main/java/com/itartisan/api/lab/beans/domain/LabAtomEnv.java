package com.itartisan.api.lab.beans.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.itartisan.common.core.web.domain.BaseEntity;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class LabAtomEnv extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long envId;
    private String envName;
    private String envTech;
    private String envKind;
    private String envParams;
    private String notes;

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

    public String getEnvKind() {
        return envKind;
    }

    public void setEnvKind(String envKind) {
        this.envKind = envKind;
    }

    public String getEnvParams() {
        return envParams;
    }

    public void setEnvParams(String envParams) {
        this.envParams = envParams;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
