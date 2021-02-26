package com.itartisan.system.api;

import com.itartisan.api.beans.system.domain.SysUser;
import com.itartisan.api.beans.system.model.LoginUser;
import com.itartisan.common.core.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * 用户服务
 */
@FeignClient(contextId = "remoteSysUserService", value = "system", url = "${local.feign.server.system.url:}")
public interface RemoteSysUserService {
    /**
     * 通过用户名查询用户信息
     *
     * @param username 用户名
     * @return 结果
     */
    @GetMapping(value = "/user/info/{username}")
    R<LoginUser> getUserInfo(@PathVariable("username") String username);

    /**
     * 取某个班级的所有学生
     *
     * @param classId
     * @return
     */
    @GetMapping("/user/class/{classId}")
    R<List<SysUser>> getStudentByClassId(@PathVariable("classId") Long classId);
}
