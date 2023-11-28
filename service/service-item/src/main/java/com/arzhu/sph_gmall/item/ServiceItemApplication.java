package com.arzhu.sph_gmall.item;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * ClassName: ServiceItemApplication
 * Package: com.arzhu.sph_gmall
 * Description:
 *
 * @Author arzhu
 * @Create 2023/11/16 15:49
 * @Version 1.0
 */
// 取消 数据源的自动配置
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@ComponentScan({"com.arzhu.sph_gmall"})  // 开启组件扫描
@EnableFeignClients(basePackages= {"com.arzhu.sph_gmall"})  // 将扫描 com.arzhu.sph_gmall 包下的 feign
@EnableDiscoveryClient  // nacos 注册

public class ServiceItemApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceItemApplication.class,args);
    }
}
