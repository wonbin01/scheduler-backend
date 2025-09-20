package wonbin.scheduler.Configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
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
    // application.properties 또는 application.yml에서 정적 리소스 경로를 가져옵니다.
    // 기본적으로 classpath:/static/, classpath:/public/ 등이 포함됩니다.
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
                        // 1. 먼저 요청된 경로가 '실제' 정적 파일인지 확인합니다.
                        //    super.getResource를 사용하여 ResourceHandler의 기본 정적 리소스 탐색 로직을 따릅니다.
                        Resource actualStaticResource = super.getResource(resourcePath, location);
                        if (actualStaticResource != null && actualStaticResource.exists() && actualStaticResource.isReadable()) {
                            return actualStaticResource; // 실제 정적 파일이 존재하면 해당 파일을 반환
                        }

                        // 2. 만약 정적 파일이 아니라면, 요청된 경로가 API 경로인지 확인합니다.
                        //    모든 API는 "/api/" 접두사를 사용한다고 가정합니다.
                        //    (만약 /login, /signup 등 /api/ 접두사가 없는 API가 있다면 여기에 추가해야 합니다.
                        //     하지만 모든 API에 /api/를 붙이는 것이 권장됩니다.)
                        if (resourcePath.startsWith("api/")) {
                            return null; // API 경로는 ResourceHandler가 아닌 Spring MVC Controller가 처리하도록 null 반환
                        }

                        // 3. 위 조건(실제 정적 파일도 아니고, API 경로도 아닌 경우)에 해당하면,
                        //    이는 React Router가 처리할 클라이언트 측 라우팅 경로이므로, index.html을 반환합니다.
                        Resource indexHtml = new ClassPathResource("static/index.html");
                        if (indexHtml.exists() && indexHtml.isReadable()) {
                            return indexHtml;
                        }

                        // 모든 조건을 만족하지 못하면 null을 반환하여 Spring의 다음 핸들러(예: 기본 404 처리)로 넘어갑니다.
                        return null;
                    }
                });
    }
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }


}
