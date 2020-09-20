package com.fksoftwares.fksbank.core.file

import org.springframework.web.multipart.MultipartFile

interface FileUploader {

    String upload(String folder, MultipartFile file)

    byte[] get(String path) throws IOException

}
