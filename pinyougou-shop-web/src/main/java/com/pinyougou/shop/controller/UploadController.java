package com.pinyougou.shop.controller;

import entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import util.FastDFSClient;

@RestController
public class UploadController {
    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;
    @RequestMapping("/upload")
    public Result upload(MultipartFile file){
        try {
            FastDFSClient client = new FastDFSClient("classpath:config/fdfs_client.conf");
            String originalFilename = file.getOriginalFilename();
            String fileId = client.uploadFile(file.getBytes(), originalFilename.substring(originalFilename.lastIndexOf(".") + 1));
            String url = FILE_SERVER_URL+fileId;//图片完整地址
            return new Result(true,url);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传失败");
        }
    }
}
