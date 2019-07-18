package com.ladyishenlong.gatewaysqr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * gateway 网关服务
 */
@EnableDiscoveryClient
@SpringBootApplication
public class GatewaySqrApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewaySqrApplication.class, args);
    }

}
