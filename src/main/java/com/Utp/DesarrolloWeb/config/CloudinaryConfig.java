package com.Utp.DesarrolloWeb.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    // Credenciales proporcionadas por el usuario
    private final String CLOUD_NAME = //generas tu cloud name
    private final String API_KEY = //colocas tu apikey
    // Nota: Reemplazaremos el API_SECRET directamente aquí.
    private final String API_SECRET = //genera api secret

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
            "cloud_name", CLOUD_NAME,
            "api_key", API_KEY,
            "api_secret", API_SECRET,
            "secure", true
        ));
    }
}
