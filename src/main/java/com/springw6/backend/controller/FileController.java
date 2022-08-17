package com.springw6.backend.controller;

import com.springw6.backend.domain.Message;
import com.springw6.backend.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class FileController {
   private final FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<?> upload(MultipartFile[] multipartFileList) throws Exception {
       List<String> imageUrlList=fileService.getImgUrlList(multipartFileList);
       return new ResponseEntity<>(Message.success(imageUrlList), HttpStatus.OK);
    }

}