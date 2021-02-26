package com.itartisan.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itartisan.system.domain.SysDictData;

/**
 * 字典 业务层处理
 */
public interface ISysDictDataService extends IService<SysDictData> {
    /**
     * 新增保存字典数据信息
     *
     * @param dictData 字典数据信息
     * @return 结果
     */
    int insertDictData(SysDictData dictData);

    /**
     * 修改保存字典数据信息
     *
     * @param dictData 字典数据信息
     * @return 结果
     */
    int updateDictData(SysDictData dictData);

    /**
     * 批量删除字典数据信息
     *
     * @param dictCodes 需要删除的字典数据ID
     * @return 结果
     */
    int deleteDictDataByIds(Long[] dictCodes);
}
