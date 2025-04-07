package com.hmdp.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.dto.Result;
import com.hmdp.entity.Shop;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.hmdp.utils.RedisConstants.CACHE_SHOP_KEY;
import static com.hmdp.utils.RedisConstants.CACHE_SHOP_TYPE_KEY;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result queryTypeList() {
        //1.从redis查询
        String key = CACHE_SHOP_TYPE_KEY + UUID.randomUUID();
        String shopTypeJson =stringRedisTemplate.opsForValue().get(key);
        List<ShopType>typeList =null;
        //2.判断是否存在
        if (StrUtil.isNotBlank(shopTypeJson)) {
            //3.存在，直接返回
            typeList = JSONUtil.toList(shopTypeJson,ShopType.class);
            return Result.ok(typeList);
        }
        //4.不存在，根据id查询数据库
        typeList = this.list(new LambdaQueryWrapper<ShopType>().orderByAsc(ShopType::getSort));
        //5.不存在，返回错误
        if (Objects.isNull(typeList)) {
            return Result.fail("店铺类型不存在");
        }
        //6.存在，写入redis
        stringRedisTemplate.opsForValue().set(key,JSONUtil.toJsonStr(typeList));


        return Result.ok(typeList);
    }
}
