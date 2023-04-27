package llm.poseconverter.controller;

import javax.annotation.Resource;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cn.dev33.satoken.util.SaResult;
import llm.poseconverter.utils.MinioUtils;

@RestController
@RequestMapping("/api/minio")
public class MinioController {
    @Resource
    private MinioUtils minioUtils;

    @PostMapping("upload")
    @ResponseBody
    // 就不实现幂等了，前端控制一下
    public SaResult upload(@RequestParam("file") MultipartFile file) throws Exception {
        String url = minioUtils.upload(file);
        return SaResult.data(url);
    }

    @GetMapping(value = "download/{objectName}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> download(@PathVariable String objectName) throws Exception {
        ResponseEntity<byte[]> responseEntity = minioUtils.downloadObject(objectName);
        return responseEntity;
    }
}