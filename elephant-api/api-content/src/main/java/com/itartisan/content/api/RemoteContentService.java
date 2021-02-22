package com.itartisan.content.api;

import com.itartisan.common.core.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 用户服务
 */
@FeignClient(contextId = "remoteContentService", value = "content", url = "${local.feign.server.content.url:}")
public interface RemoteContentService {

    /**
     * 删除文件
     *
     * @param fileIds 文件ID
     * @return 结果
     */
    @DeleteMapping(value = "/content/delete")
    R delete(@RequestParam("fileIds") String... fileIds);

    /**
     * 删除文件夹
     *
     * @param path 路径
     * @return 结果
     */
    @DeleteMapping(value = "/content/deleteFolder")
    R deleteFolder(@RequestParam("path") String path);

    /**
     * 删除文件和文件夹
     *
     * @param path 路径
     * @return 结果
     */
    @DeleteMapping(value = "/content/delFolder")
    R delFolder(@RequestParam("path") String path);
}
