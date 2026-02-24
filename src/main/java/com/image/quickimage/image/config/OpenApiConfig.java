package com.image.quickimage.image.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Advanced AI-driven Image CDN - Quick Image AI ")
                        .version("1.0")
                        .description("###  Quick Image AI: High-Performance Image Processing Engine\n\n" +
                                "A next-generation Image CDN that bridges the gap between raw storage and intelligent delivery. \n\n" +
                                "**Key Capabilities:**\n" +
                                "* **Smart Focus & Auto-Crop:** Utilizing the **DETR (ResNet-50)** Deep Learning model to identify subjects and perform context-aware cropping (`mode=profile`).\n" +
                                "* **Visual Analytics:** Real-time extraction of vibrant color palettes and hex-codes for dynamic UI theming.\n" +
                                "* **Ultra-Low Latency:** Optimized with **Redis caching** to serve transformed assets at millisecond speeds.\n" +
                                "* **On-the-Fly Optimization:** Dynamic resizing, quality adjustment, and WebP format conversion via URL parameters.\n" +
                                "**Rate Limiting Policy:**\n" +
                                "* This API implements **Token Bucket algorithm** via Bucket4j.\n" +
                                "* Standard limits apply to the `/upload` and AI transformation endpoints to ensure high availability.\n" +
                                "* Check `X-Rate-Limit-Remaining` headers for real-time quota tracking.")
                        .contact(new Contact().name("Karan").email("karanc4143@gmail.com")))
                        .addServersItem(new Server().url("http://localhost:8082").description("Development Server"));
    }
}
