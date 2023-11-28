package com.arzhu.sph_gmall.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * ClassName: ServiceProductApplication
 * Package: com.arzhu.sph_gmall.product
 * Description:
 *
 * @Author arzhu
 * @Create 2023/11/13 18:36
 * @Version 1.0
 */
@SpringBootApplication
@ComponentScan({"com.arzhu.sph_gmall"})
public class ServiceProductApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceProductApplication.class, args);
    }
}
