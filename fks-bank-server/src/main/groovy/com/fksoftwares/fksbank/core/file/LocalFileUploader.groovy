package com.fksoftwares.fksbank.core.file

import com.fksoftwares.fksbank.core.FileException
import com.fksoftwares.fksbank.core.file.FileUploader
import groovy.transform.PackageScope
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.FileSystemResource
import org.springframework.stereotype.Component
import org.springframework.util.StreamUtils
import org.springframework.web.multipart.MultipartFile

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
@PackageScope
class LocalFileUploader implements FileUploader {

    private Logger logger = LoggerFactory.getLogger(LocalFileUploader)

    @Value("\${file.upload.folder}")
    private String uploadFolder

    @Override
    String upload(String folder, MultipartFile file) {

        def formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
        def fileRelativePath = folder + "/" + LocalDateTime.now().format(formatter) + "/" + file.getOriginalFilename()
        def path = uploadFolder + "/" + fileRelativePath

        try {

            def dir = new File(path)

            if (!dir.exists())
                dir.mkdirs()

            file.transferTo(dir)

        } catch (IllegalStateException ex) {
            def name = file.getOriginalFilename()
            logger.error("Erro ao fazer upload do arquivo: {}", name)
            throw new FileException("fileUploadError", name)
        } catch (IOException ex) {
            logger.error("Caminho para o arquivo não existe: {}", path)
            throw new FileException("filePathIsNullOrEmpty", path)
        }

        return fileRelativePath
    }

    @Override
    byte[] get(String path) throws IOException {

        if (path == null || path.isEmpty()) {
            logger.error("Caminho para o arquivo não existe: {}", path)
            throw new FileException("filePathIsNullOrEmpty", path)
        }
        def realPath = uploadFolder + "/" + path
        def resource = new FileSystemResource(realPath)
        return StreamUtils.copyToByteArray(resource.getInputStream())
    }
}
