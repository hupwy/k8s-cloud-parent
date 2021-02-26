package com.itartisan.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itartisan.api.beans.system.domain.SysDept;
import com.itartisan.common.core.constant.Constants;
import com.itartisan.common.core.constant.UserConstants;
import com.itartisan.common.core.exception.CustomException;
import com.itartisan.system.domain.vo.TreeSelect;
import com.itartisan.system.mapper.SysDeptMapper;
import com.itartisan.system.service.ISysDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements ISysDeptService {
    @Autowired
    private SysDeptMapper sysDeptMapper;

    /**
     * 查询组织机构管理数据
     *
     * @param dept 组织机构信息
     * @return 组织机构信息集合
     */
    @Override
    public List<SysDept> selectDeptList(SysDept dept) {
        return sysDeptMapper.selectDeptList(dept);
    }

    /**
     * 构建前端所需要下拉树结构
     *
     * @param depts 组织机构列表
     * @return 下拉树结构列表
     */
    @Override
    public List<TreeSelect> buildDeptTreeSelect(List<SysDept> depts) {
        List<SysDept> deptTrees = buildDeptTree(depts);
        return deptTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    /**
     * 构建前端所需要树结构
     *
     * @param depts 组织机构列表
     * @return 树结构列表
     */
    @Override
    public List<SysDept> buildDeptTree(List<SysDept> depts) {
        List<SysDept> returnList = new ArrayList<>();
        List<Long> tempList = new ArrayList<>();
        for (SysDept dept : depts) {
            tempList.add(dept.getDeptId());
        }
        for (SysDept dept : depts) {
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(dept.getParentId())) {
                recursionFn(depts, dept);
                returnList.add(dept);
            }
        }
        if (returnList.isEmpty()) {
            returnList = depts;
        }
        return returnList;
    }

    /**
     * 校验组织机构名称是否唯一
     *
     * @param dept 组织机构信息
     * @return 结果
     */
    @Override
    public String checkDeptNameUnique(SysDept dept) {
        long deptId = Objects.isNull(dept.getDeptId()) ? -1L : dept.getDeptId();
        LambdaQueryWrapper<SysDept> query = Wrappers.lambdaQuery(SysDept.class);
        query.eq(SysDept::getDeptName, dept.getDeptName())
                .eq(SysDept::getParentId, dept.getParentId())
                .eq(SysDept::getDelFlag,Constants.NOT_DELETED_FLAG);
        SysDept info = sysDeptMapper.selectOne(query);
        if (Objects.nonNull(info) && info.getDeptId() != deptId) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 新增保存组织机构信息
     *
     * @param dept 组织机构信息
     * @return 结果
     */
    @Override
    public int insertDept(SysDept dept) {
        SysDept info = sysDeptMapper.selectById(dept.getParentId());
        // 如果父节点不为正常状态,则不允许新增子节点
        if (!UserConstants.DEPT_NORMAL.equals(info.getStatus())) {
            throw new CustomException("组织机构停用，不允许新增");
        }
        dept.setAncestors(info.getAncestors() + "," + dept.getParentId());
        return sysDeptMapper.insert(dept);
    }

    /**
     * 根据ID查询所有子组织机构（正常状态）
     *
     * @param deptId 组织机构ID
     * @return 子组织机构数
     */
    @Override
    public int selectNormalChildrenDeptById(Long deptId) {
        return sysDeptMapper.selectNormalChildrenDeptById(deptId).size();
    }

    /**
     * 修改保存组织机构信息
     *
     * @param dept 组织机构信息
     * @return 结果
     */
    @Override
    public int updateDept(SysDept dept) {
        SysDept newParentDept = sysDeptMapper.selectById(dept.getParentId());
        SysDept oldDept = sysDeptMapper.selectById(dept.getParentId());
        if (Objects.nonNull(newParentDept) && Objects.nonNull(oldDept)) {
            String newAncestors = newParentDept.getAncestors() + "," + newParentDept.getDeptId();
            String oldAncestors = oldDept.getAncestors();
            dept.setAncestors(newAncestors);
            updateDeptChildren(dept.getDeptId(), newAncestors, oldAncestors);
        }
        int result = sysDeptMapper.updateDept(dept);
        if (UserConstants.DEPT_NORMAL.equals(dept.getStatus())) {
            // 如果该组织机构是启用状态，则启用该组织机构的所有上级组织机构
            updateParentDeptStatus(dept);
        }
        return result;
    }

    /**
     * 是否存在子节点
     *
     * @param deptId 组织机构ID
     * @return 结果
     */
    @Override
    public boolean hasChildByDeptId(Long deptId) {
        int result = sysDeptMapper.hasChildByDeptId(deptId);
        return result > 0;
    }

    /**
     * 查询组织机构是否存在用户
     *
     * @param deptId 组织机构ID
     * @return 结果 true 存在 false 不存在
     */
    @Override
    public boolean checkDeptExistUser(Long deptId) {
        int result = sysDeptMapper.checkDeptExistUser(deptId);
        return result > 0;
    }

    /**
     * 修改子元素关系
     *
     * @param deptId       被修改的组织机构ID
     * @param newAncestors 新的父ID集合
     * @param oldAncestors 旧的父ID集合
     */
    private void updateDeptChildren(Long deptId, String newAncestors, String oldAncestors) {
        List<SysDept> children = sysDeptMapper.selectChildrenDeptById(deptId);
        for (SysDept child : children) {
            child.setAncestors(child.getAncestors().replace(oldAncestors, newAncestors));
        }
        if (children.size() > 0) {
            sysDeptMapper.updateDeptChildren(children);
        }
    }

    /**
     * 修改该组织机构的父级组织机构状态
     *
     * @param dept 当前组织机构
     */
    private void updateParentDeptStatus(SysDept dept) {
        String updateBy = dept.getUpdateBy();
        dept = sysDeptMapper.selectById(dept.getDeptId());
        dept.setUpdateBy(updateBy);
        sysDeptMapper.updateDeptStatus(dept);
    }

    /**
     * 递归列表
     */
    private void recursionFn(List<SysDept> list, SysDept t) {
        // 得到子节点列表
        List<SysDept> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysDept tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<SysDept> getChildList(List<SysDept> list, SysDept t) {
        List<SysDept> tlist = new ArrayList<>();
        for (SysDept n : list) {
            if (Objects.nonNull(n.getParentId()) && n.getParentId().longValue() == t.getDeptId().longValue()) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<SysDept> list, SysDept t) {
        return getChildList(list, t).size() > 0;
    }
}
