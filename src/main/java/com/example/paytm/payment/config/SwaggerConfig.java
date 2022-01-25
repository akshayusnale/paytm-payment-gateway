package com.example.paytm.payment.config;

import com.google.common.collect.Lists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    private Object RestSecurityFilter;

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).select()
                .apis(RequestHandlerSelectors.basePackage("com.example.paytm.payment.contoller"))
                .paths(PathSelectors.any())
                .build().apiInfo(apiInfo()).securitySchemes(Lists.newArrayList(apiKey()))
                ;
    }

    private ApiInfo apiInfo() {

        return new ApiInfoBuilder()
                .title("Paytm Payment ")
                .description("Payment API Documentation")
                .version("1.0").contact(new Contact("akshay usnale", "https://www.linkedin.com/in/akshay-usnale-b7a8b713b/", "iamakshay757@gmail.com"))
                .build();
    }

     private ApiKey apiKey() {
        return new ApiKey(HttpHeaders.AUTHORIZATION, "Authorization", "header");
    }
}
