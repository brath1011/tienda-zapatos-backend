package com.Utp.DesarrolloWeb.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;
    
    // Carpeta donde se subirán las fotos en Cloudinary para mantener orden
    private static final String FOLDER = "tienda_zapatos";

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    /**
     * Sube una imagen a Cloudinary y retorna la URL segura.
     */
    public String subirImagen(MultipartFile file) throws IOException {
        Map params = ObjectUtils.asMap(
            "folder", FOLDER,
            "resource_type", "auto"
        );
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
        return uploadResult.get("secure_url").toString();
    }

    /**
     * Elimina una imagen de Cloudinary a partir de su URL.
     */
    public void eliminarImagen(String url) {
        if (url == null || !url.contains("res.cloudinary.com")) {
            return; // No es de Cloudinary
        }
        
        try {
            // Ejemplo URL: https://res.cloudinary.com/kdjlvola/image/upload/v12345/tienda_zapatos/nombre_archivo.jpg
            // publicId = tienda_zapatos/nombre_archivo
            String[] partes = url.split("/");
            String nombreArchivo = partes[partes.length - 1]; // "nombre_archivo.jpg"
            String nombreSinExtension = nombreArchivo.substring(0, nombreArchivo.lastIndexOf('.'));
            
            // Reconstruimos el publicId asumiendo que está dentro del FOLDER configurado
            String publicId = FOLDER + "/" + nombreSinExtension;
            
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            System.err.println("Error eliminando imagen de Cloudinary (" + url + "): " + e.getMessage());
        }
    }
}
