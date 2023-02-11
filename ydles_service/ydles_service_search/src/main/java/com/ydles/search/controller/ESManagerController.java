package com.ydles.search.controller;

import com.ydles.entity.Result;
import com.ydles.entity.StatusCode;
import com.ydles.search.service.EsManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Created by IT李老师
 * 公主号 “IT李哥交朋友”
 * 个人微 itlils
 */
@RestController
@RequestMapping("/manager")
public class ESManagerController {
    @Autowired
    EsManagerService esManagerService;

    @GetMapping("/create")
    public Result create(){
        esManagerService.createIndexAndMapping();
        return new Result(true, StatusCode.OK,"创建索引库成功");
    }

    @GetMapping("/importData")
    public Result importData(){
        esManagerService.importData();
        return new Result(true, StatusCode.OK,"导入数据成功");
    }

}
