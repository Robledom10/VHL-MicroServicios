package com.hernandolopera.operation_servicio.servicio;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.hernandolopera.operation_servicio.excepcion.ExcepcionReglaNegocio;
import java.io.IOException;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ServicioImagenPaquete {
    private static final String CARPETA_PAQUETES = "vhl/packages";

    private final Cloudinary cloudinary;

    public ServicioImagenPaquete(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String subir(MultipartFile archivo) {
        validarArchivo(archivo);
        try {
            Map<?, ?> resultado = cloudinary.uploader().upload(archivo.getBytes(), ObjectUtils.asMap(
                "folder", CARPETA_PAQUETES,
                "resource_type", "image"
            ));
            Object urlSegura = resultado.get("secure_url");
            if (urlSegura == null) {
                throw new ExcepcionReglaNegocio("Cloudinary no retorno la URL segura de la imagen");
            }
            return urlSegura.toString();
        } catch (IOException excepcion) {
            throw new ExcepcionReglaNegocio("No se pudo leer la imagen para subirla a Cloudinary");
        } catch (RuntimeException excepcion) {
            throw new ExcepcionReglaNegocio("No se pudo subir la imagen a Cloudinary: " + excepcion.getMessage());
        }
    }

    private void validarArchivo(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new ExcepcionReglaNegocio("Debe enviar una imagen");
        }
        String tipoContenido = archivo.getContentType();
        if (tipoContenido == null || !tipoContenido.startsWith("image/")) {
            throw new ExcepcionReglaNegocio("El archivo debe ser una imagen");
        }
    }
}
