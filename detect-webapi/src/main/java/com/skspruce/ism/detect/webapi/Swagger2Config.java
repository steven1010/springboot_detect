package com.skspruce.ism.detect.webapi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
public class Swagger2Config {
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(getApiInfo())
                .select()
                //为当前包路径
                .apis(RequestHandlerSelectors.basePackage("com.skspruce.ism.detect.webapi.strategy.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * 构建 api文档的详细信息函数
     *
     * @return
     */
    private ApiInfo getApiInfo() {
        return new ApiInfoBuilder()
                //页面标题
                .title("探针系统测试API")
                //创建人
                .contact(new Contact("Tengfei Wang", "http://www.skspruce.com/", "tengfeiwang@skspruce.net"))
                //版本号
                .version("1.0.0")
                //描述
                .description("测试API,在测试期间可能会发生改动!!!")
                .build();
    }
}
