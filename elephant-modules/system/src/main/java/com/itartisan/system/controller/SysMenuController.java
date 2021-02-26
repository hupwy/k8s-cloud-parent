package com.itartisan.system.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.itartisan.common.core.constant.Constants;
import com.itartisan.common.core.constant.UserConstants;
import com.itartisan.common.core.domain.AjaxResult;
import com.itartisan.common.core.web.controller.BaseController;
import com.itartisan.common.security.annotation.PreAuthorize;
import com.itartisan.common.security.utils.SecurityUtils;
import com.itartisan.system.domain.SysMenu;
import com.itartisan.system.service.ISysMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单信息
 */
@Api(value = "/menu", description = "菜单信息")
@RestController
@RequestMapping("/menu")
public class SysMenuController extends BaseController {
    @Autowired
    private ISysMenuService menuService;

    /**
     * 获取菜单列表
     */
    @ApiOperation("获取菜单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "menuName", value = "菜单名称", dataTypeClass = String.class, paramType = "query"),
            @ApiImplicitParam(name = "visible", value = "菜单状态（0显示 1隐藏）", allowableValues = "0,1", dataTypeClass = String.class, paramType = "query"),
            @ApiImplicitParam(name = "status", value = "菜单状态（0正常 1停用）", allowableValues = "0,1", dataTypeClass = String.class, paramType = "query"),
            @ApiImplicitParam(name = "params", value = "参数", dataTypeClass = String.class, defaultValue = "", paramType = "query")
    })
    @PreAuthorize(hasPermi = "system:menu:list")
    @GetMapping("/list")
    public AjaxResult list(@ModelAttribute SysMenu menu) {
        Long userId = SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuList(menu, userId);
        return AjaxResult.success(menuService.buildMenuTree(menus));
    }

    /**
     * 根据菜单编号获取详细信息
     */
    @ApiOperation("根据菜单编号获取详细信息")
    @ApiImplicitParam(name = "menuId", value = "菜单ID", dataTypeClass = Long.class, paramType = "query")
    @PreAuthorize(hasPermi = "system:menu:query")
    @GetMapping(value = "/{menuId}")
    public AjaxResult getInfo(@PathVariable Long menuId) {
        return AjaxResult.success(menuService.getOne(Wrappers.lambdaQuery(SysMenu.class).eq(SysMenu::getMenuId, menuId)));
//        return AjaxResult.success(menuService.selectMenuById(menuId));
    }

    /**
     * 获取菜单下拉树列表
     */
    @ApiOperation("获取菜单下拉树列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "menuName", value = "菜单名称", dataTypeClass = String.class, paramType = "query"),
            @ApiImplicitParam(name = "visible", value = "菜单状态（0显示 1隐藏）", allowableValues = "0,1", dataTypeClass = String.class, paramType = "query"),
            @ApiImplicitParam(name = "status", value = "菜单状态（0正常 1停用）", allowableValues = "0,1", dataTypeClass = String.class, paramType = "query")
    })
    @GetMapping("/treeselect")
    public AjaxResult treeselect(SysMenu menu) {
        Long userId = SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuList(menu, userId);
        return AjaxResult.success(menuService.buildMenuTreeSelect(menus));
    }

    /**
     * 加载对应角色菜单列表树
     */
    @ApiOperation("加载对应角色菜单列表树")
    @ApiImplicitParam(name = "roleId", value = "角色ID", dataTypeClass = Long.class, paramType = "query")
    @GetMapping(value = "/roleMenuTreeselect/{roleId}")
    public AjaxResult roleMenuTreeselect(@PathVariable("roleId") Long roleId) {
        Long userId = SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuList(userId);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("checkedKeys", menuService.selectMenuListByRoleId(roleId));
        ajax.put("menus", menuService.buildMenuTreeSelect(menus));
        return ajax;
    }

    /**
     * 新增菜单
     */
    @ApiOperation("新增菜单")
    @PreAuthorize(hasPermi = "system:menu:add")
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysMenu menu) {
        if (UserConstants.NOT_UNIQUE.equals(menuService.checkMenuNameUnique(menu))) {
            return AjaxResult.error("新增菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        } else if (UserConstants.YES_FRAME.equals(menu.getIsFrame())
                && (!StringUtils.startsWithIgnoreCase(menu.getPath(), Constants.HTTP)
                && !StringUtils.startsWithIgnoreCase(menu.getPath(), Constants.HTTPS))) {
            return AjaxResult.error("新增菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
        }
        menu.setCreateBy(SecurityUtils.getUsername());
        return toAjax(menuService.insertMenu(menu));
    }

    /**
     * 修改菜单
     */
    @ApiOperation("修改菜单")
    @PreAuthorize(hasPermi = "system:menu:edit")
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysMenu menu) {
        if (UserConstants.NOT_UNIQUE.equals(menuService.checkMenuNameUnique(menu))) {
            return AjaxResult.error("修改菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        } else if (UserConstants.YES_FRAME.equals(menu.getIsFrame())
                && (!StringUtils.startsWithIgnoreCase(menu.getPath(), Constants.HTTP)
                && !StringUtils.startsWithIgnoreCase(menu.getPath(), Constants.HTTPS))) {
            return AjaxResult.error("新增菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
        } else if (menu.getMenuId().equals(menu.getParentId())) {
            return AjaxResult.error("新增菜单'" + menu.getMenuName() + "'失败，上级菜单不能选择自己");
        }
        menu.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(menuService.updateMenu(menu));
    }

    /**
     * 删除菜单
     */
    @ApiOperation("删除菜单")
    @ApiImplicitParam(name = "menuId", value = "菜单ID", dataTypeClass = Long.class, paramType = "query")
    @PreAuthorize(hasPermi = "system:menu:remove")
    @DeleteMapping("/{menuId}")
    public AjaxResult remove(@PathVariable("menuId") Long menuId) {
        if (menuService.hasChildByMenuId(menuId)) {
            return AjaxResult.error("存在子菜单,不允许删除");
        }
        if (menuService.checkMenuExistRole(menuId)) {
            return AjaxResult.error("菜单已分配,不允许删除");
        }
        return toAjax(menuService.deleteMenuById(menuId));
    }

    /**
     * 获取路由信息
     *
     * @return 路由信息
     */
    @ApiOperation("获取路由信息")
    @GetMapping("getRouters")
    public AjaxResult getRouters() {
        Long userId = SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId);
        return AjaxResult.success(menuService.buildMenus(menus));
    }
}
