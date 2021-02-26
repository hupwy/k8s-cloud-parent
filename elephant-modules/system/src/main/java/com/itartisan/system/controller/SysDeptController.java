package com.itartisan.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.itartisan.common.core.constant.Constants;
import com.itartisan.common.core.constant.UserConstants;
import com.itartisan.common.core.domain.AjaxResult;
import com.itartisan.common.core.web.controller.BaseController;
import com.itartisan.common.security.annotation.PreAuthorize;
import com.itartisan.common.security.utils.SecurityUtils;
import com.itartisan.system.beans.domain.SysDept;
import com.itartisan.system.service.ISysDeptService;
import io.swagger.annotations.*;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@Api(value = "/dept", description = "组织机构信息")
@RestController
@RequestMapping("/dept")
public class SysDeptController extends BaseController {
    @Autowired
    private ISysDeptService deptService;

    @ApiOperation("获取组织机构列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "parentId", value = "父组织机构ID", dataTypeClass = Long.class, paramType = "query"),
            @ApiImplicitParam(name = "deptName", value = "组织机构名称", dataTypeClass = String.class, paramType = "query"),
            @ApiImplicitParam(name = "status", value = "组织机构状态:0正常,1停用", allowableValues = "0,1", dataTypeClass = String.class, paramType = "query")
    })
    @PreAuthorize(hasPermi = "system:dept:list")
    @GetMapping("/list")
    public AjaxResult list(@ApiIgnore SysDept dept) {
        List<SysDept> depts = deptService.selectDeptList(dept);
        return AjaxResult.success(deptService.buildDeptTree(depts));
    }

    @ApiOperation("查询组织机构列表（排除节点）")
    @PreAuthorize(hasPermi = "system:dept:list")
    @GetMapping("/list/exclude/{deptId}")
    public AjaxResult excludeChild(@ApiParam("组织机构id") @PathVariable(value = "deptId", required = false) Long deptId) {
        List<SysDept> depts = deptService.selectDeptList(new SysDept());
        depts.removeIf(d -> d.getDeptId().intValue() == deptId
                || ArrayUtils.contains(StringUtils.split(d.getAncestors(), ","), String.valueOf(deptId)));
        return AjaxResult.success(depts);
    }

    @ApiOperation("根据组织机构编号获取详细信息")
    @PreAuthorize(hasPermi = "system:dept:query")
    @GetMapping(value = "/{deptId}")
    public AjaxResult getInfo(@ApiParam("组织机构id") @PathVariable Long deptId) {
        return AjaxResult.success(deptService.getById(deptId));
    }

    @ApiOperation("获取组织机构下拉树列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "parentId", value = "父组织机构ID", dataTypeClass = Long.class, paramType = "query"),
            @ApiImplicitParam(name = "deptName", value = "组织机构名称", dataTypeClass = String.class, paramType = "query"),
            @ApiImplicitParam(name = "status", value = "组织机构状态:0正常,1停用", allowableValues = "0,1", dataTypeClass = String.class, paramType = "query")
    })
    @GetMapping("/treeselect")
    public AjaxResult treeselect(@ApiIgnore SysDept dept) {
        List<SysDept> depts = deptService.selectDeptList(dept);
        return AjaxResult.success(deptService.buildDeptTreeSelect(depts));
    }

    @ApiOperation("获取教师可教授的班级信息")
    @GetMapping("/classSelect")
    public AjaxResult classSelect() {
        Long deptId = SecurityUtils.getDeptId();
        LambdaQueryWrapper<SysDept> queryWrapper = Wrappers.lambdaQuery(SysDept.class);
        queryWrapper.select(SysDept::getDeptId, SysDept::getDeptName)
                .eq(SysDept::getParentId, deptId)
                .eq(SysDept::getDelFlag, Constants.NOT_DELETED_FLAG)
                .eq(SysDept::getStatus, "0");
        return AjaxResult.success(deptService.list(queryWrapper));
    }

    @ApiOperation("新增组织机构")
    @PreAuthorize(hasPermi = "system:dept:add")
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysDept dept) {
        if (UserConstants.NOT_UNIQUE.equals(deptService.checkDeptNameUnique(dept))) {
            return AjaxResult.error("新增组织机构'" + dept.getDeptName() + "'失败，组织机构名称已存在");
        }
        dept.setCreateBy(SecurityUtils.getUsername());
        return toAjax(deptService.insertDept(dept));
    }

    @ApiOperation("修改组织机构")
    @PreAuthorize(hasPermi = "system:dept:edit")
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysDept dept) {
        if (UserConstants.NOT_UNIQUE.equals(deptService.checkDeptNameUnique(dept))) {
            return AjaxResult.error("修改组织机构'" + dept.getDeptName() + "'失败，组织机构名称已存在");
        } else if (dept.getParentId().equals(dept.getDeptId())) {
            return AjaxResult.error("修改组织机构'" + dept.getDeptName() + "'失败，上级组织机构不能是自己");
        } else if (StringUtils.equals(UserConstants.DEPT_DISABLE, dept.getStatus())
                && deptService.selectNormalChildrenDeptById(dept.getDeptId()) > 0) {
            return AjaxResult.error("该组织机构包含未停用的子组织机构！");
        }
        dept.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(deptService.updateDept(dept));
    }

    @ApiOperation("删除组织机构")
    @PreAuthorize(hasPermi = "system:dept:remove")
    @DeleteMapping("/{deptId}")
    public AjaxResult remove(@PathVariable Long deptId) {
        if (deptService.hasChildByDeptId(deptId)) {
            return AjaxResult.error("存在下级组织机构,不允许删除");
        }
        if (deptService.checkDeptExistUser(deptId)) {
            return AjaxResult.error("组织机构存在用户,不允许删除");
        }
        if (deptService.removeById(deptId)) {
            return AjaxResult.success();
        } else {
            return AjaxResult.error();
        }
    }
}
