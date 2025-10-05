package wonbin.scheduler.Configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class WebConfig  implements WebMvcConfigurer {
    private final LoginCheckInterceptor loginCheckInterceptor;
    @Value("${spring.web.resources.static-locations:classpath:/static/,classpath:/public/}")
    private String[] staticLocations;
    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")
                .allowCredentials(true)
                .allowedMethods("*");
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(loginCheckInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/login", "/signup","/error",
                        "/css/**" , "/js/**","/images/**",
                        "/logo192.png",
                        "/favicon.ico",
                        "/manifest.json",
                        "/static/**",
                        "/",                   // 루트
                        "/index.html"        // 리액트 루트 HTML

                );
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 모든 요청을 처리하는 핸들러를 추가
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/", "classpath:/static/static/") // React 빌드 파일이 있는 static 폴더만 명시
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        Resource actualStaticResource = super.getResource(resourcePath, location);
                        if (actualStaticResource != null && actualStaticResource.exists() && actualStaticResource.isReadable()) {
                            return actualStaticResource; // 실제 정적 파일이 존재하면 해당 파일을 반환
                        }
                        if (resourcePath.startsWith("api/")) {
                            return null;
                        }
                        Resource indexHtml = new ClassPathResource("static/index.html");
                        if (indexHtml.exists() && indexHtml.isReadable()) {
                            return indexHtml;
                        }
                        return null;
                    }
                });
    }
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}