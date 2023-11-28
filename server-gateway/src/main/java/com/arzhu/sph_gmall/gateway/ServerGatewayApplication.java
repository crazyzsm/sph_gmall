package com.arzhu.sph_gmall.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * ClassName: ServerGatewayApplication
 * Package: com.arzhu.sph_gmall.gateway
 * Description:
 *
 * @Author arzhu
 * @Create 2023/11/14 18:01
 * @Version 1.0
 */
@SpringBootApplication
public class ServerGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServerGatewayApplication.class,args);
    }
}

