package com.itartisan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itartisan.api.beans.system.domain.SysDept;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysDeptMapper extends BaseMapper<SysDept> {
    /**
     * 查询组织机构管理数据
     *
     * @param dept 组织机构信息
     * @return 组织机构信息集合
     */
    List<SysDept> selectDeptList(SysDept dept);

    /**
     * 根据ID查询所有子组织机构（正常状态）
     *
     * @param deptId 组织机构ID
     * @return 组织机构信息集合
     */
    List<SysDept> selectNormalChildrenDeptById(Long deptId);

    /**
     * 查询组织机构是否存在用户
     *
     * @param deptId 组织机构ID
     * @return 结果
     */
    int checkDeptExistUser(Long deptId);

    /**
     * 修改组织机构信息
     *
     * @param dept 组织机构信息
     * @return 结果
     */
    int updateDept(SysDept dept);

    /**
     * 根据ID查询所有子组织机构
     *
     * @param deptId 组织机构ID
     * @return 组织机构列表
     */
    List<SysDept> selectChildrenDeptById(Long deptId);

    /**
     * 是否存在子节点
     *
     * @param deptId 组织机构ID
     * @return 结果
     */
    int hasChildByDeptId(Long deptId);

    /**
     * 修改子元素关系
     *
     * @param depts 子元素
     * @return 结果
     */
    int updateDeptChildren(@Param("depts") List<SysDept> depts);

    /**
     * 修改所在组织机构的父级组织机构状态
     *
     * @param dept 组织机构
     */
    void updateDeptStatus(SysDept dept);
}
