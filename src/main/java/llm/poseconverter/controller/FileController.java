package llm.poseconverter.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cn.dev33.satoken.util.SaResult;
import io.minio.messages.Item;
import llm.poseconverter.service.MinioService;

@RestController
@RequestMapping("/api/file")
public class FileController {

    @Value("${minio.endpoint}")
    private String endPoint;

    @Resource
    private MinioService minioService;

    @PostMapping("/")
    public SaResult uploadFile(@RequestParam("bucketName") String bucketName, 
                               @RequestParam("file") MultipartFile file) throws Exception {
        String url = minioService.uploadFile(bucketName, file);
        return SaResult.data(url);
    }

    @GetMapping("/")
    public SaResult listFiles(@RequestParam("bucketName") String bucketName, 
                              @RequestParam("prefix") String prefix) throws Exception {
        List<Item> results = minioService.listFiles(bucketName, prefix);
        List<Map<String, Object>> files = new ArrayList<>();
        for (Item item : results) {
            Map<String, Object> file = new HashMap<>();
            file.put("name", item.objectName());
            file.put("size", item.size());
            file.put("lastModified", item.lastModified().toString());
            file.put("isDir", item.isDir());
            file.put("url", endPoint + "/" + bucketName + "/" + item.objectName());
            file.put("etag", item.etag());
            file.put("isDeleteMarker", item.isDeleteMarker());
            file.put("isLatest", item.isLatest());
            file.put("versionId", item.versionId());
            file.put("storageClass", item.storageClass());
            files.add(file);
        }
        return SaResult.data(files);
    }

    @DeleteMapping("/{bucketName}/{objectName}")
    public SaResult deleteFile(@PathVariable("bucketName") String bucketName, 
                               @PathVariable("objectName") String objectName) throws Exception {
        minioService.deleteFile(bucketName, objectName);
        return SaResult.data("删除成功");
    }

    @GetMapping("/search")
    public SaResult searchFiles(@RequestParam("bucketName") String bucketName, 
                                @RequestParam("prefix") String prefix,
                                @RequestParam("keyword") String keyword) throws Exception {
        return SaResult.data(minioService.searchFiles(bucketName, prefix, keyword));
    }
}
