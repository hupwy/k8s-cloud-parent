package com.itartisan.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itartisan.common.core.constant.UserConstants;
import com.itartisan.common.core.domain.AjaxResult;
import com.itartisan.common.core.domain.R;
import com.itartisan.common.core.utils.poi.ExcelUtil;
import com.itartisan.common.core.web.PageSolver;
import com.itartisan.common.core.web.controller.BaseController;
import com.itartisan.common.security.annotation.PreAuthorize;
import com.itartisan.common.security.utils.SecurityUtils;
import com.itartisan.system.beans.domain.SysRole;
import com.itartisan.system.beans.domain.SysUser;
import com.itartisan.system.beans.model.LoginUser;
import com.itartisan.system.service.ISysPermissionService;
import com.itartisan.system.service.ISysRoleService;
import com.itartisan.system.service.ISysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户信息
 */
@Api(value = "/user", description = "用户信息")
@RestController
@RequestMapping("/user")
public class SysUserController extends BaseController {
    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysPermissionService permissionService;

    /**
     * 获取用户列表
     */
    @ApiOperation("获取用户列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userName", value = "用户账号", dataTypeClass = String.class, paramType = "query"),
            @ApiImplicitParam(name = "status", value = "帐号状态（0正常 1停用）", allowableValues = "0,1", dataTypeClass = String.class, paramType = "query"),
            @ApiImplicitParam(name = "deptId", value = "组织机构ID", dataTypeClass = String.class, paramType = "query")
    })
    @PreAuthorize(hasPermi = "system:user:list")
    @GetMapping("/list")
    public IPage<SysUser> list(@ApiIgnore @PageSolver Page<SysUser> page, SysUser user) {
        return userService.selectUserPageList(page, user);
    }

    /**
     * 导出用户
     */
    @ApiOperation("导出用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userName", value = "用户账号", dataTypeClass = String.class, paramType = "query"),
            @ApiImplicitParam(name = "status", value = "帐号状态（0正常 1停用）", allowableValues = "0,1", dataTypeClass = String.class, paramType = "query"),
            @ApiImplicitParam(name = "deptId", value = "组织机构ID", dataTypeClass = String.class, paramType = "query")
    })
    @PreAuthorize(hasPermi = "system:user:export")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysUser user) throws IOException {
        List<SysUser> list = userService.selectUserList(user);
        ExcelUtil<SysUser> util = new ExcelUtil<>(SysUser.class);
        util.exportExcel(response, list, "用户数据");
    }

    /**
     * 导入用户
     */
    @ApiOperation("导入用户")
    @PreAuthorize(hasPermi = "system:user:import")
    @PostMapping("/importData")
    public AjaxResult importData(@RequestParam("file") MultipartFile file, @RequestParam("updateSupport") boolean updateSupport) throws Exception {
        ExcelUtil<SysUser> util = new ExcelUtil<>(SysUser.class);
        List<SysUser> userList = util.importExcel(file.getInputStream());
        String operName = SecurityUtils.getUsername();
        String message = userService.importUser(userList, updateSupport, operName);
        return AjaxResult.success(message);
    }

    /**
     * 下载导入模板
     */
    @ApiOperation("下载导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<SysUser> util = new ExcelUtil<>(SysUser.class);
        util.importTemplateExcel(response, "用户数据");
    }

    /**
     * 取某个班级的所有学生
     * @param classId
     * @return
     */
    @GetMapping("/class/{classId}")
    public R<List<SysUser>> getStudentByClassId(@PathVariable("classId") Long classId){
        return R.ok(userService.getStudentByClassId(classId));
    }

    /**
     * 获取当前用户信息
     *
     * @return
     */
    @ApiOperation("获取当前用户信息")
    @ApiImplicitParam(name = "username", value = "用户账号", dataTypeClass = String.class, paramType = "query")
    @GetMapping("/info/{username}")
    public R<LoginUser> info(@PathVariable("username") String username) {
        SysUser sysUser = userService.selectUserByUserName(username);
        if (sysUser == null) {
            return R.fail("用户名或密码错误");
        }
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(sysUser.getUserId());
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(sysUser.getUserId());
        LoginUser sysUserVo = new LoginUser();
        sysUserVo.setSysUser(sysUser);
        sysUserVo.setRoles(roles);
        sysUserVo.setPermissions(permissions);
        return R.ok(sysUserVo);
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @ApiOperation("获取用户信息")
    @GetMapping("getInfo")
    public AjaxResult getInfo() {
        Long userId = SecurityUtils.getUserId();
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(userId);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(userId);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("user", userService.selectUserById(userId));
        ajax.put("roles", roles);
        ajax.put("permissions", permissions);
        return ajax;
    }

    /**
     * 根据用户编号获取详细信息
     */
    @ApiOperation("根据用户编号获取详细信息")
    @ApiImplicitParam(name = "userId", value = "用户ID", dataTypeClass = Integer.class, paramType = "query")
    @PreAuthorize(hasPermi = "system:user:query")
    @GetMapping(value = {"/", "/{userId}"})
    public AjaxResult getInfo(@PathVariable(value = "userId", required = false) Long userId) {
        AjaxResult ajax = AjaxResult.success();
        List<SysRole> roles = roleService.selectRoleAll();
        ajax.put("roles", SysUser.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
        if (userId != null) {
            ajax.put(AjaxResult.DATA_TAG, userService.selectUserById(userId));
            ajax.put("roleIds", roleService.selectRoleListByUserId(userId));
        }
        return ajax;
    }

    /**
     * 新增用户
     */
    @ApiOperation("新增用户")
    @PreAuthorize(hasPermi = "system:user:add")
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysUser user) {
        if (UserConstants.NOT_UNIQUE.equals(userService.checkUserNameUnique(user.getUserName()))) {
            return AjaxResult.error("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
        } else if (UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(user))) {
            return AjaxResult.error("新增用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setCreateBy(SecurityUtils.getUsername());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        return toAjax(userService.insertUser(user));
    }

    /**
     * 修改用户
     */
    @ApiOperation("修改用户")
    @PreAuthorize(hasPermi = "system:user:edit")
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysUser user) {
        userService.checkUserAllowed(user);
        if (UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(user))) {
            return AjaxResult.error("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(userService.updateUser(user));
    }

    /**
     * 删除用户
     */
    @ApiOperation("删除用户")
    @PreAuthorize(hasPermi = "system:user:remove")
    @DeleteMapping("/{userIds}")
    public AjaxResult remove(@PathVariable Long[] userIds) {
        return toAjax(userService.deleteUserByIds(userIds));
    }

    /**
     * 重置密码
     */
    @ApiOperation("重置密码")
    @PreAuthorize(hasPermi = "system:user:edit")
    @PutMapping("/resetPwd")
    public AjaxResult resetPwd(@RequestBody SysUser user) {
        userService.checkUserAllowed(user);
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        user.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(userService.resetPwd(user));
    }

    /**
     * 状态修改
     */
    @ApiOperation("状态修改")
    @PreAuthorize(hasPermi = "system:user:edit")
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysUser user) {
        userService.checkUserAllowed(user);
        user.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(userService.updateUserStatus(user));
    }

}
