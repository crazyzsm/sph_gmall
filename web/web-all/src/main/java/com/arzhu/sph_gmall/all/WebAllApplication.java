package com.arzhu.sph_gmall.all;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

/**
 * ClassName: WebAllApplication
 * Package: com.arzhu.sph_gmall.all
 * Description:
 *
 * @Author arzhu
 * @Create 2023/11/18 13:37
 * @Version 1.0
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)  // 排除数据库
@ComponentScan({"com.arzhu.sph_gmall"})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.arzhu.sph_gmall")
public class WebAllApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebAllApplication.class,args);
    }
}
