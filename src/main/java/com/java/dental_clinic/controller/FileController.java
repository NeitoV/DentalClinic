package com.java.dental_clinic.controller;


import com.java.dental_clinic.service.FileService;
import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/file")
public class FileController {
    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.status(HttpStatus.OK).body(fileService.uploadFile(file));
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        Resource resource = fileService.downloadFile(fileName);
        String originalFilename = resource.getFilename().split("_", 2)[1];
        String mediaType = URLConnection.guessContentTypeFromName(originalFilename);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mediaType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + originalFilename + "\"")
                .body(resource);
    }

    @PostMapping("/copy/{fileName}")
    public ResponseEntity<?> copy(@PathVariable String fileName) {
        return ResponseEntity.status(HttpStatus.OK).body(fileService.generateCopy(fileName));
    }

    @GetMapping("/display/{fileName}")  // view on web
    public ResponseEntity<byte[]> displayFile(@PathVariable String fileName) throws IOException {
        Resource resource = fileService.downloadFile(fileName);
        String originalFilename = resource.getFilename().split("_", 2)[1];

        Tika tika = new Tika();
        String mediaType = tika.detect(resource.getInputStream());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(mediaType));
        headers.setContentDisposition(ContentDisposition.inline().filename(originalFilename).build());

        InputStream inputStream = resource.getInputStream();
        byte[] fileBytes = IOUtils.toByteArray(inputStream);
        inputStream.close();

        return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
    }
}
