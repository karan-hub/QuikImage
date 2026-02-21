package com.image.quickimage.image.config;

import com.image.quickimage.image.service.RateLimiterService;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RateLimitInterceptor  implements HandlerInterceptor {
    @Autowired
    private RateLimiterService rateLimiterService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            String ip = request.getRemoteAddr();
            Bucket bucket = rateLimiterService.resolveBucket(ip);
            if (bucket.tryConsume(1)) {
                response.addHeader("e-Limit-Remaining", String.valueOf(bucket.getAvailableTokens()));
                return true;
            } else {
                response.setStatus(429);
                response.setContentType("application/json");
                response.getWriter().write("{ \"error\": \"Too many requests\", \"limit\": \"5 per minute\" }");
                return false;
            }
        }catch (Exception e){
            return  true;
        }
    }
}
