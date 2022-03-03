package com.xuecheng.framework.domain.course;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by admin on 2018/2/7.
 */
@Data
@ToString
@Entity
@Table(name="category")
//assigned在插入数据的时候主键由程序处理（很常用），这是 <generator>元素没有指定时的默认生成策略
@GenericGenerator(name = "jpa-assigned", strategy = "assigned")
// 采用128位的uuid算法生成主键，uuid被编码为一个32位16进制数字的字符串。占用空间大（字符串类型）。
//@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class Category implements Serializable {
    private static final long serialVersionUID = -906357110051689484L;
    @Id
    @GeneratedValue(generator = "jpa-assigned")
    @Column(length = 32)
    private String id;
    private String name;
    private String label;
    private String parentid;
    private String isshow;
    private Integer orderby;
    private String isleaf;

}
