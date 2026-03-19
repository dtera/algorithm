package cn.cstn.algorithm.workclaw.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 * <p>
 * 配置 CORS、静态资源映射和前端路由转发。
 * </p>
 *
 * @author zhaohuiqiang
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/api/**")
      .allowedOriginPatterns("*")
      .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
      .allowedHeaders("*")
      .allowCredentials(true);
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // 前端静态资源映射
    registry.addResourceHandler("/**")
      .addResourceLocations("classpath:/static/");
  }

  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    // SPA 前端路由转发
    registry.addViewController("/").setViewName("forward:/index.html");
  }

}
