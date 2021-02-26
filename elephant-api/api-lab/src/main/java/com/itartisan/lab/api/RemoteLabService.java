package com.itartisan.lab.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itartisan.api.beans.lab.domain.LabAtomEnv;
import com.itartisan.api.beans.lab.model.ProcessModel;
import com.itartisan.common.core.domain.R;
import io.fabric8.kubernetes.api.model.PodStatus;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 实验环境服务
 */
@FeignClient(contextId = "remoteLabService", value = "lab", url = "${local.feign.server.lab.url:}")
public interface RemoteLabService {

    /**
     * 启动集群
     *
     * @param envId 模板环境ID
     * @return 结果
     */
    @PostMapping(value = "/lab/apply")
    R<ProcessModel> apply(@RequestParam("envId") Long envId);

    /**
     * 停止集群
     *
     * @param podInstanceNames     pod实例名称
     * @param serviceInstanceNames service实例名称
     * @return 结果
     */
    @PostMapping(value = "/lab/stop")
    R<Boolean> stop(@RequestParam("podInstanceNames") String[] podInstanceNames
            , @RequestParam("serviceInstanceNames") String[] serviceInstanceNames);

    /**
     * 查询模板
     *
     * @param page 分页信息
     * @param envName 模板名称
     * @return 结果
     */
    @GetMapping(value = "/lab/pageAtomEnv")
    R<IPage<LabAtomEnv>> pageAtomEnv(Page<LabAtomEnv> page, @RequestParam("envName") String envName);

    /**
     * 查询Pod状态
     *
     * @param podName Pod名称
     *
     * @return 结果
     */
    @GetMapping(value = "/lab/isPodReady")
    R<PodStatus> isPodReady(@RequestParam(value = "podName") String podName);

}
