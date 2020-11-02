package com.zwzch.fool.test.controller;

import com.zwzch.fool.test.db.BankTelReflect;
import com.zwzch.fool.test.db.BankTelReflectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.Date;

@RestController
public class DeomController {

    @Autowired
    private BankTelReflectMapper bankTelReflectMapper;

    @RequestMapping("/select")
    public void select() {
        BankTelReflect bankTelReflect = bankTelReflectMapper.selectByCardNo("1");
        System.out.println(bankTelReflect);
    }

    @RequestMapping("/insert")
    public void insert() {
        BankTelReflect bankTelReflect = new BankTelReflect();
        bankTelReflect.setReqNo("123");
        bankTelReflect.setCardNo("testxxx");
        bankTelReflect.setCountryCode("CN");
        bankTelReflect.setTelephone("123321");
        bankTelReflect.setAddTime(new Timestamp(System.currentTimeMillis()));
        bankTelReflect.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        int insert = bankTelReflectMapper.insert(bankTelReflect);
        Assert.isTrue(insert > 0);
    }

    @RequestMapping("/update")
    public void update() {
        BankTelReflect bankTelReflect = new BankTelReflect();
//        bankTelReflect.setReqNo("123");
        bankTelReflect.setCardNo("testxxx");
        bankTelReflect.setCountryCode("gakki");
//        bankTelReflect.setTelephone("123321");
//        bankTelReflect.setAddTime(new Timestamp(System.currentTimeMillis()));
//        bankTelReflect.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        int update = bankTelReflectMapper.update(bankTelReflect);
        Assert.isTrue(update > 0);
    }

    @RequestMapping("/delete")
    public void delete() {
        BankTelReflect bankTelReflect = new BankTelReflect();
//        bankTelReflect.setReqNo("123");
        bankTelReflect.setCardNo("testxxx");
        bankTelReflect.setCountryCode("gakki");
//        bankTelReflect.setTelephone("123321");
//        bankTelReflect.setAddTime(new Timestamp(System.currentTimeMillis()));
//        bankTelReflect.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        int delete = bankTelReflectMapper.delete(bankTelReflect);
        Assert.isTrue(delete > 0);
    }
}
