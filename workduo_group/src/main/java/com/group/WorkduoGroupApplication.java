package com.group;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.core.*"})
@EntityScan(basePackages = {"com.core.*"})
@EnableJpaRepositories(basePackages = {"com.core.*"})
public class WorkduoGroupApplication{

    static {
        System.setProperty("com.amazonaws.sdk.disableEc2Metadata", "true");
    }

    public static void main(String[] args) {
        SpringApplication.run(WorkduoGroupApplication.class, args);
    }

    /**
     *  Spring Data JPA가 Entity가 등록되거나 수정 될 때 마다
     *  auditorProvider() 를 호출을
     *  실제로는 세션등 에서 유저 정보를 가져와서 반환을 해줘야 함
     * @return
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return null;
    }
}
