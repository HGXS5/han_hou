package com.xuecheng.manage_cms.dao;

import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;
@Data
@ToString
public class Student {
    private String name;//姓名
    private int age;//年龄
    private Date birthday;//生日
    private Float mondy;//钱包
    private List<Student> friends;//朋友列表
    private Student bestFriend;//最好的朋友
}
