package vn.candicode.config;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.apache.commons.io.FileUtils;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import vn.candicode.exception.handler.CandicodeError;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
    private static final long MAX_UPLOAD_FILE_SIZE = 10 * FileUtils.ONE_MB; // 10Mb

    @Bean
    public Hibernate5Module hibernate5Module() {
        return new Hibernate5Module();
    }

    @Bean
    public ErrorAttributes errorAttributes() {
        return new CandicodeError.Attributes();
    }

    @Bean
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();

        multipartResolver.setMaxUploadSize(MAX_UPLOAD_FILE_SIZE);
        multipartResolver.setDefaultEncoding("UTF-8");
        multipartResolver.setResolveLazily(true);

        return multipartResolver;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins("*")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH");
    }
}
