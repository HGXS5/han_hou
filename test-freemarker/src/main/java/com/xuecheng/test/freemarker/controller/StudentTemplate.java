package com.xuecheng.test.freemarker.controller;

import com.xuecheng.test.freemarker.model.Student;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@ToString
@Document(collection = "cms_config")
public class StudentTemplate {
    @Id
    private String id;
    private String name;
    private List<Student> stus;
    private Student stu1;
    private Map<String, Student> stuMap;

}
