package com.itartisan.gateway.config;

///**
// * 网关限流配置
// */
//@Configuration
//public class GatewayConfig {
//    @Bean
//    @Order(Ordered.HIGHEST_PRECEDENCE)
//    public SentinelFallbackHandler sentinelGatewayExceptionHandler() {
//        return new SentinelFallbackHandler();
//    }
//
//    @Bean
//    @Order(-1)
//    public GlobalFilter sentinelGatewayFilter() {
//        return new SentinelGatewayFilter();
//    }
//}