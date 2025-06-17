package com.markcollab.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    /**
     * Faz o upload de um arquivo para o Cloudinary.
     * @param file O arquivo enviado pelo usuário.
     * @return A URL segura (HTTPS) do arquivo no Cloudinary.
     * @throws IOException Se ocorrer um erro durante o upload.
     */
    public String upload(MultipartFile file) throws IOException {
        Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());

        return result.get("secure_url").toString();
    }
}