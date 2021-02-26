package com.itartisan.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.base.Strings;
import com.itartisan.common.core.constant.UserConstants;
import com.itartisan.common.core.domain.AjaxResult;
import com.itartisan.common.core.web.PageSolver;
import com.itartisan.common.core.web.controller.BaseController;
import com.itartisan.common.security.annotation.PreAuthorize;
import com.itartisan.common.security.utils.SecurityUtils;
import com.itartisan.system.domain.SysDictType;
import com.itartisan.system.service.ISysDictTypeService;
import com.itartisan.system.util.DictUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * 数据字典信息
 */
@Api(value = "/dict/type", description = "数据字典类型信息")
@RestController
@RequestMapping("/dict/type")
public class SysDictTypeController extends BaseController {

    @Autowired
    private ISysDictTypeService sysDictTypeService;


    @ApiOperation("字典管理分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前页", dataTypeClass = Integer.class, paramType = "query", required = true),
            @ApiImplicitParam(name = "pageSize", value = "单页容量", dataTypeClass = Integer.class, paramType = "query", required = true),
            @ApiImplicitParam(name = "dictName", value = "字典名称", dataTypeClass = String.class, paramType = "query"),
            @ApiImplicitParam(name = "dicType", value = "字典类型", dataTypeClass = String.class, paramType = "query"),
            @ApiImplicitParam(name = "status", value = "状态（0正常 1停用）", allowableValues = "0,1", dataTypeClass = String.class, paramType = "query")
    })
    @PreAuthorize(hasPermi = "system:dict:list")
    @GetMapping("/list")
    public Page<SysDictType> list(@ApiIgnore @PageSolver Page<SysDictType> page,
                                  @RequestParam(value = "dictType", required = false) String dictType,
                                  @RequestParam(value = "dictName", required = false) String dictName,
                                  @RequestParam(value = "status", required = false) String status) {
        LambdaQueryWrapper<SysDictType> queryWrapper = Wrappers.lambdaQuery(SysDictType.class)
                .eq(!Strings.isNullOrEmpty(dictName), SysDictType::getDictName, dictName)
                .eq(!Strings.isNullOrEmpty(status), SysDictType::getStatus, status)
                .like(!Strings.isNullOrEmpty(dictType), SysDictType::getDictType, dictType);
        return sysDictTypeService.page(page, queryWrapper);
    }

    @ApiOperation("查询字典类型详细")
    @PreAuthorize(hasPermi = "system:dict:query")
    @GetMapping(value = "/{dictId}")
    public AjaxResult getInfo(@PathVariable Long dictId) {
        return AjaxResult.success(sysDictTypeService.getById(dictId));
    }

    @ApiOperation("新增字典类型")
    @PreAuthorize(hasPermi = "system:dict:add")
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysDictType dict) {
        if (UserConstants.NOT_UNIQUE.equals(sysDictTypeService.checkDictTypeUnique(dict))) {
            return AjaxResult.error("新增字典'" + dict.getDictName() + "'失败，字典类型已存在");
        }
        dict.setCreateBy(SecurityUtils.getUsername());
        return toAjax(sysDictTypeService.insertDictType(dict));
    }

    @ApiOperation(value = "修改字典类型", notes = "dictName(字典名称)、dictType(字典类型)、status(状态（0正常 1停用）必填")
    @PreAuthorize(hasPermi = "system:dict:edit")
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysDictType dict) {
        if (UserConstants.NOT_UNIQUE.equals(sysDictTypeService.checkDictTypeUnique(dict))) {
            return AjaxResult.error("修改字典'" + dict.getDictName() + "'失败，字典类型已存在");
        }
        dict.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(sysDictTypeService.updateDictType(dict));
    }

    @ApiOperation("删除字典类型")
    @PreAuthorize(hasPermi = "system:dict:remove")
    @DeleteMapping("/{dictIds}")
    public AjaxResult remove(@PathVariable Long[] dictIds) {
        return toAjax(sysDictTypeService.deleteDictTypeByIds(dictIds));
    }

    @ApiOperation("清空缓存")
    @PreAuthorize(hasPermi = "system:dict:remove")
    @DeleteMapping("/clearCache")
    public AjaxResult clearCache() {
        DictUtils.clearDictCache();
        return AjaxResult.success();
    }

    @ApiOperation("获取字典选择框列表")
    @GetMapping("/optionselect")
    public AjaxResult optionselect() {
        List<SysDictType> dictTypes = sysDictTypeService.list();
        return AjaxResult.success(dictTypes);
    }
}
