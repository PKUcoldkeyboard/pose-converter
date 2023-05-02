package llm.poseconverter.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.jwt.StpLogicJwtForStateless;
import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {
    @Bean
    public StpLogic getStpLogicJwt() {
        return new StpLogicJwtForStateless();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册Sa-Token拦截器
        registry.addInterceptor(new SaInterceptor(handle -> StpUtil.checkLogin()))
                .addPathPatterns("/**")
                .excludePathPatterns("/api/login", "/api/register");
    }

    /**
     * 全局过滤器
     */
    @Bean
    public SaServletFilter getSaServletFilter() {
        return new SaServletFilter()
                // 拦截与排除path
                .addInclude("/**").addExclude("/favicon.ico")
                .setBeforeAuth(obj -> {
                    // 设置跨域响应头
                    SaHolder.getResponse()
                    // 允许指定域访问跨域资源
                    .setHeader("Access-Control-Allow-Origin", "http://localhost:9104")
                    // 允许所有请求方式
                    .setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT")
                    // 有效时间
                    .setHeader("Access-Control-Max-Age", "3600")
                    // 允许的Header参数
                    .setHeader("Access-Control-Allow-Headers", "Content-Type, sa-token")
                    // 允许携带cookie
                    .setHeader("Access-Control-Allow-Credentials", "true");

                    // 如果是预检请求，则立即返回前端
                    SaRouter.match(SaHttpMethod.OPTIONS)
                        .free(r -> System.out.println("OPTIONS请求，不做处理"))
                        .back();
                });
    }
}
