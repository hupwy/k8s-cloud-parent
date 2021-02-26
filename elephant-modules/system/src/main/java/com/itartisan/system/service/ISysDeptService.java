package com.itartisan.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itartisan.system.beans.domain.SysDept;
import com.itartisan.system.domain.vo.TreeSelect;

import java.util.List;

/**
 * 组织机构 服务层
 */
public interface ISysDeptService extends IService<SysDept> {
    /**
     * 查询组织机构管理数据
     *
     * @param dept 组织机构信息
     * @return 组织机构信息集合
     */
    List<SysDept> selectDeptList(SysDept dept);

    /**
     * 构建前端所需要下拉树结构
     *
     * @param depts 组织机构列表
     * @return 下拉树结构列表
     */
    List<TreeSelect> buildDeptTreeSelect(List<SysDept> depts);

    /**
     * 构建前端所需要树结构
     *
     * @param depts 组织机构列表
     * @return 树结构列表
     */
    List<SysDept> buildDeptTree(List<SysDept> depts);

    /**
     * 校验组织机构名称是否唯一
     *
     * @param dept 组织机构信息
     * @return 结果
     */
    String checkDeptNameUnique(SysDept dept);

    /**
     * 新增保存组织机构信息
     *
     * @param dept 组织机构信息
     * @return 结果
     */
    int insertDept(SysDept dept);

    /**
     * 根据ID查询所有子组织机构（正常状态）
     *
     * @param deptId 组织机构ID
     * @return 子组织机构数
     */
    int selectNormalChildrenDeptById(Long deptId);

    /**
     * 修改保存组织机构信息
     *
     * @param dept 组织机构信息
     * @return 结果
     */
    int updateDept(SysDept dept);

    /**
     * 是否存在子节点
     *
     * @param deptId 组织机构ID
     * @return 结果
     */
    boolean hasChildByDeptId(Long deptId);

    /**
     * 查询组织机构是否存在用户
     *
     * @param deptId 组织机构ID
     * @return 结果 true 存在 false 不存在
     */
    boolean checkDeptExistUser(Long deptId);
}
