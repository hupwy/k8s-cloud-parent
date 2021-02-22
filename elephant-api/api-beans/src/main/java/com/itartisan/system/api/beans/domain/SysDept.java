package com.itartisan.system.api.beans.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.itartisan.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * 组织机构表 sys_dept
 */
@TableName("sys_dept")
@ApiModel
public class SysDept extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 组织机构ID
     */
    @TableId(type = IdType.AUTO)
    private Long deptId;

    /**
     * 父组织机构ID
     */
    private Long parentId;

    /**
     * 祖级列表
     */
    @ApiModelProperty(hidden = true)
    private String ancestors;

    /**
     * 组织机构名称
     */
    private String deptName;

    /**
     * 显示顺序
     */
    private String orderNum;

    /**
     * 组织机构状态:0正常,1停用
     */
    private String status;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @ApiModelProperty(hidden = true)
    private String delFlag;

    /**
     * 父组织机构名称
     */
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String parentName;

    /**
     * 子组织机构
     */
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private List<SysDept> children = new ArrayList<>();

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getAncestors() {
        return ancestors;
    }

    public void setAncestors(String ancestors) {
        this.ancestors = ancestors;
    }

    @NotBlank(message = "组织机构名称不能为空")
    @Size(max = 30, message = "组织机构名称长度不能超过30个字符")
    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    @NotBlank(message = "显示顺序不能为空")
    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public List<SysDept> getChildren() {
        return children;
    }

    public void setChildren(List<SysDept> children) {
        this.children = children;
    }
}
