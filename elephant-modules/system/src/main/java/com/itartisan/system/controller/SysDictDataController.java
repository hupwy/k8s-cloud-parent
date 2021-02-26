package com.itartisan.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.base.Strings;
import com.itartisan.common.core.domain.AjaxResult;
import com.itartisan.common.core.web.PageSolver;
import com.itartisan.common.core.web.controller.BaseController;
import com.itartisan.common.security.annotation.PreAuthorize;
import com.itartisan.common.security.utils.SecurityUtils;
import com.itartisan.system.domain.SysDictData;
import com.itartisan.system.service.ISysDictDataService;
import com.itartisan.system.service.ISysDictTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Api(value = "/dict/data", description = "数据字典数据信息")
@RestController
@RequestMapping("/dict/data")
public class SysDictDataController extends BaseController {
    @Autowired
    private ISysDictDataService dictDataService;

    @Autowired
    private ISysDictTypeService dictTypeService;

    @ApiOperation("字典项数据分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前页", dataTypeClass = Integer.class, paramType = "query", required = true),
            @ApiImplicitParam(name = "pageSize", value = "单页容量", dataTypeClass = Integer.class, paramType = "query", required = true),
            @ApiImplicitParam(name = "dicType", value = "字典类型", dataTypeClass = String.class, paramType = "query"),
            @ApiImplicitParam(name = "dictLabel", value = "字典标签", dataTypeClass = String.class, paramType = "query"),
            @ApiImplicitParam(name = "status", value = "状态（0正常 1停用）", allowableValues = "0,1", dataTypeClass = String.class, paramType = "query")
    })
    @PreAuthorize(hasPermi = "system:dict:list")
    @GetMapping("/list")
    public Page<SysDictData> list(@ApiIgnore @PageSolver Page<SysDictData> page,
                                  @RequestParam(value = "dictType", required = false) String dictType,
                                  @RequestParam(value = "dictLabel", required = false) String dictLabel,
                                  @RequestParam(value = "status", required = false) String status) {
        LambdaQueryWrapper<SysDictData> queryWrapper = Wrappers.lambdaQuery(SysDictData.class)
                .eq(!Strings.isNullOrEmpty(dictType), SysDictData::getDictType, dictType)
                .like(!Strings.isNullOrEmpty(dictLabel), SysDictData::getDictLabel, dictLabel)
                .eq(!Strings.isNullOrEmpty(status), SysDictData::getStatus, status)
                .orderByAsc(SysDictData::getDictSort);
        return dictDataService.page(page, queryWrapper);
    }

    @ApiOperation("查询字典数据详细")
    @PreAuthorize(hasPermi = "system:dict:query")
    @GetMapping(value = "/{dictCode}")
    public AjaxResult getInfo(@PathVariable Long dictCode) {
        return AjaxResult.success(dictDataService.getById(dictCode));
    }

    @ApiOperation("根据字典类型查询字典数据信息")
    @GetMapping(value = "/type/{dictType}")
    public AjaxResult dictType(@PathVariable String dictType) {
        return AjaxResult.success(dictTypeService.selectDictDataByType(dictType));
    }

    @ApiOperation("新增字典类型")
    @PreAuthorize(hasPermi = "system:dict:add")
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysDictData dict) {
        dict.setCreateBy(SecurityUtils.getUsername());
        return toAjax(dictDataService.insertDictData(dict));
    }

    @ApiOperation("修改保存字典类型")
    @PreAuthorize(hasPermi = "system:dict:edit")
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysDictData dict) {
        dict.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(dictDataService.updateDictData(dict));
    }

    @ApiOperation("删除字典类型")
    @PreAuthorize(hasPermi = "system:dict:remove")
    @DeleteMapping("/{dictCodes}")
    public AjaxResult remove(@PathVariable Long[] dictCodes) {
        return toAjax(dictDataService.deleteDictDataByIds(dictCodes));
    }
}
