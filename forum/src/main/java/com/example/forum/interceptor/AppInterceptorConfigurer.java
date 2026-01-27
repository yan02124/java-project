package com.example.forum.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

//表示一个配置类
@Configuration
public class AppInterceptorConfigurer implements WebMvcConfigurer {

    //注入自定义的登录拦截器
    @Resource
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")                 // 拦截所有请求
                .excludePathPatterns("/sign-in.html")   // 排除登录HTML
                .excludePathPatterns("/sign-up.html")   // 排除注册HTML
                .excludePathPatterns("/index.html")     // 排除首页（游客可访问）
                .excludePathPatterns("/article_list.html")  // 排除帖子列表页（游客可访问）
                .excludePathPatterns("/details.html")   // 排除帖子详情页（游客可访问）
                .excludePathPatterns("/user/login")     // 排除登录api接口
                .excludePathPatterns("/user/register")  // 排除注册api接口
                .excludePathPatterns("/user/logout")    // 排除退出api接口
                .excludePathPatterns("/user/info")      // 排除获取用户信息接口（游客返回空）
                .excludePathPatterns("/board/**")       // 排除板块接口（游客可查看）
                .excludePathPatterns("/article/getAllByBoardId")  // 游客可查看帖子列表
                .excludePathPatterns("/article/details")          // 游客可查看帖子详情
                .excludePathPatterns("/article/search")           // 游客可搜索帖子
                .excludePathPatterns("/article/advancedSearch")   // 游客可高级搜索帖子
                .excludePathPatterns("/reply/getReplies")         // 游客可查看回复
                .excludePathPatterns("/swagger*/**")    // 排除登录swagger下所有
                .excludePathPatterns("/v3*/**")         // 排除登录v3下所有，与swagger相关
                .excludePathPatterns("/dist/**")        // 排除所有静态文件
                .excludePathPatterns("/image/**")
                .excludePathPatterns("/js/**")
                .excludePathPatterns("/upload/**")      // 排除上传的图片
                .excludePathPatterns("/**.ico");
    }
}
