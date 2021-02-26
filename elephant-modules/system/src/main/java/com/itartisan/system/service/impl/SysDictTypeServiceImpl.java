package com.itartisan.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itartisan.common.core.constant.UserConstants;
import com.itartisan.common.core.exception.CustomException;
import com.itartisan.system.domain.SysDictData;
import com.itartisan.system.domain.SysDictType;
import com.itartisan.system.mapper.SysDictDataMapper;
import com.itartisan.system.mapper.SysDictTypeMapper;
import com.itartisan.system.service.ISysDictTypeService;
import com.itartisan.system.util.DictUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

/**
 * 字典 业务层处理
 */
@Service
public class SysDictTypeServiceImpl extends ServiceImpl<SysDictTypeMapper, SysDictType> implements ISysDictTypeService {

    @Autowired
    private SysDictTypeMapper sysDictTypeMapper;

    @Autowired
    private SysDictDataMapper sysDictDataMapper;

    /**
     * 项目启动时，初始化字典到缓存
     */
    @PostConstruct
    public void init() {
        List<SysDictType> dictTypeList = sysDictTypeMapper.selectList(null);
        for (SysDictType dictType : dictTypeList) {
            List<SysDictData> dictDatas = sysDictDataMapper.selectDictDataByType(dictType.getDictType());
            DictUtils.setDictCache(dictType.getDictType(), dictDatas);
        }
    }

    /**
     * 校验字典类型称是否唯一
     *
     * @param dict 字典类型
     * @return 结果
     */
    @Override
    public String checkDictTypeUnique(SysDictType dict) {
        Long dictId = dict.getDictId() == null ? -1L : dict.getDictId();
        LambdaQueryWrapper<SysDictType> query = Wrappers.lambdaQuery(SysDictType.class);
        query.eq(SysDictType::getDictType, dict.getDictType());
        SysDictType dictType = sysDictTypeMapper.selectOne(query);
        if (dictType != null && dictType.getDictId().longValue() != dictId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 新增保存字典类型信息
     *
     * @param dictType 字典类型信息
     * @return 结果
     */
    @Override
    public int insertDictType(SysDictType dictType) {
        int row = sysDictTypeMapper.insert(dictType);
        if (row > 0) {
            DictUtils.clearDictCache();
        }
        return row;
    }

    /**
     * 修改保存字典类型信息
     *
     * @param dictType 字典类型信息
     * @return 结果
     */
    @Override
    @Transactional
    public int updateDictType(SysDictType dictType) {
        SysDictType oldDict = sysDictTypeMapper.selectById(dictType.getDictId());
        sysDictDataMapper.updateDictDataType(oldDict.getDictType(), dictType.getDictType());
        int row = sysDictTypeMapper.updateDictType(dictType);
        if (row > 0) {
            DictUtils.clearDictCache();
        }
        return row;
    }

    /**
     * 批量删除字典信息
     *
     * @param dictIds 需要删除的字典ID
     * @return 结果
     */
    @Override
    public int deleteDictTypeByIds(Long[] dictIds) {
        for (Long dictId : dictIds) {
            SysDictType dictType = sysDictTypeMapper.selectById(dictId);
            if (sysDictDataMapper.selectCount(Wrappers.lambdaQuery(SysDictData.class).eq(SysDictData::getDictType, dictType.getDictType())) > 0) {
                throw new CustomException(String.format("%1$s已分配,不能删除", dictType.getDictName()));
            }
        }
        int count = sysDictTypeMapper.deleteBatchIds(Arrays.asList(dictIds));
        if (count > 0) {
            DictUtils.clearDictCache();
        }
        return count;
    }

    /**
     * 根据字典类型查询字典数据
     *
     * @param dictType 字典类型
     * @return 字典数据集合信息
     */
    @Override
    public List<SysDictData> selectDictDataByType(String dictType) {
        List<SysDictData> dictDatas = DictUtils.getDictCache(dictType);
        if (!CollectionUtils.isEmpty(dictDatas)) {
            return dictDatas;
        }
        dictDatas = sysDictDataMapper.selectDictDataByType(dictType);
        if (!CollectionUtils.isEmpty(dictDatas)) {
            DictUtils.setDictCache(dictType, dictDatas);
            return dictDatas;
        }
        return null;
    }
}
