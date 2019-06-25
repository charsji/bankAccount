package com.hodo.jjaccount;

import com.ace.cache.EnableAceCache;
import com.github.wxiaoqi.merge.EnableAceMerge;
import com.github.wxiaoqi.security.auth.client.EnableAceAuthClient;
import com.spring4all.swagger.EnableSwagger2Doc;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableEurekaClient
@EnableCircuitBreaker
@SpringBootApplication
@EnableFeignClients({"com.github.wxiaoqi.security.auth.client.feign", "com.hodo.jjaccount.feign"})
@EnableScheduling
@EnableAceCache
@EnableTransactionManagement
@MapperScan({"com.hodo.jjaccount.mapper", "com.github.wxiaoqi.security.admin.client.mapper"})
@EnableAceAuthClient
@EnableSwagger2Doc
@EnableAceMerge
public class JjaccountApplication {

    public static void main(String[] args) {
        SpringApplication.run(JjaccountApplication.class, args);
    }

}
