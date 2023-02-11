package com.ydles.goods.feign;

import com.ydles.entity.Result;
import com.ydles.goods.pojo.Spu;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Created by IT李老师
 * 公主号 “IT李哥交朋友”
 * 个人微 itlils
 */
@FeignClient(name = "goods")
public interface SpuFeign {
    //spuId ---> spu
    @GetMapping("/spu/findSpuById/{id}")
    public Result<Spu> findSpuById(@PathVariable String id);
}
