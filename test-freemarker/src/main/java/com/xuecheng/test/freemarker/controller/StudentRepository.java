package com.xuecheng.test.freemarker.controller;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface StudentRepository extends MongoRepository<StudentTemplate,String> {
}
