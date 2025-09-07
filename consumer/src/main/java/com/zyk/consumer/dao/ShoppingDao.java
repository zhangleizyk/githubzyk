package com.zyk.consumer.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zyk.consumer.bo.ShoppingBo;
import com.zyk.consumer.dao.entity.ShoppingEntity;
import com.zyk.consumer.dao.mapper.ShoppingMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ShoppingDao {

    @Resource
    private ShoppingMapper shoppingMapper;

    public List<ShoppingBo> getShoppingList(ShoppingBo shoppingBo){
        List<ShoppingBo> shoppingBos = new ArrayList<>();

        LambdaQueryWrapper<ShoppingEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(StringUtils.isNotEmpty(shoppingBo.getId()), ShoppingEntity::getId, shoppingBo.getId())
                .eq(StringUtils.isNotEmpty(shoppingBo.getKindNo()), ShoppingEntity::getKind_no, shoppingBo.getKindNo());
        List<ShoppingEntity> list = shoppingMapper.selectList(queryWrapper);

        if (null != list && !list.isEmpty()) {
            for (ShoppingEntity entity : list) {
                ShoppingBo bo = new ShoppingBo();
                bo.setId(entity.getId());
                bo.setKindNo(entity.getKind_no());
                shoppingBos.add(bo);
            }
        }
        return shoppingBos;
    };

    public Integer addShopping(ShoppingBo shoppingBo){
        ShoppingEntity shoppingEntity = new ShoppingEntity();
        shoppingEntity.setId(shoppingBo.getId());
        shoppingEntity.setKind_no(shoppingBo.getKindNo());
        return shoppingMapper.insert(shoppingEntity);
    }
}
