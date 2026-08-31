package com.fit.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.fit.mapper")
public class MybatisPlusConfig {
}