package com.itartisan.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itartisan.system.domain.SysDictData;
import com.itartisan.system.mapper.SysDictDataMapper;
import com.itartisan.system.service.ISysDictDataService;
import com.itartisan.system.util.DictUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * 字典 业务层处理
 */
@Service
public class SysDictDataServiceImpl extends ServiceImpl<SysDictDataMapper, SysDictData> implements ISysDictDataService {

    @Autowired
    private SysDictDataMapper dictDataMapper;

    /**
     * 新增保存字典数据信息
     *
     * @param dictData 字典数据信息
     * @return 结果
     */
    @Override
    public int insertDictData(SysDictData dictData) {
        int row = dictDataMapper.insert(dictData);
        if (row > 0) {
            DictUtils.clearDictCache();
        }
        return row;
    }

    /**
     * 修改保存字典数据信息
     *
     * @param dictData 字典数据信息
     * @return 结果
     */
    @Override
    public int updateDictData(SysDictData dictData) {
        int row = dictDataMapper.updateDictData(dictData);
        if (row > 0) {
            DictUtils.clearDictCache();
        }
        return row;
    }

    /**
     * 批量删除字典数据信息
     *
     * @param dictCodes 需要删除的字典数据ID
     * @return 结果
     */
    @Override
    public int deleteDictDataByIds(Long[] dictCodes) {
        int row = dictDataMapper.deleteBatchIds(Arrays.asList(dictCodes));
        if (row > 0) {
            DictUtils.clearDictCache();
        }
        return row;
    }
}
