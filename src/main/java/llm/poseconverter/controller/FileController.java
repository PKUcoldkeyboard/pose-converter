package llm.poseconverter.controller;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cn.dev33.satoken.util.SaResult;
import llm.poseconverter.service.MinioService;

@RestController
@RequestMapping("/api/file")
public class FileController {

    @Value("${minio.endpoint}")
    private String endPoint;

    @Resource
    private MinioService minioService;

    @PostMapping("/upload/{bucketName}")
    public SaResult uploadFile(@PathVariable("bucketName") String bucketName, 
                               @RequestParam("file") MultipartFile file) throws Exception {
        String url = minioService.uploadFile(bucketName, file);
        return SaResult.data(url);
    }

    @GetMapping("/")
    public SaResult listFiles(@RequestParam("bucketName") String bucketName, 
                              @RequestParam("prefix") String prefix) throws Exception {
        SaResult result = minioService.listFiles(bucketName, prefix);
        return result;
    }

    @DeleteMapping("/delete")
    public SaResult deleteFile(@RequestParam("bucketName") String bucketName, 
                               @RequestParam("objectName") String objectName) throws Exception {
        minioService.deleteFile(bucketName, objectName);
        return SaResult.data("删除成功");
    }

    @GetMapping("/search")
    public SaResult searchFiles(@RequestParam("bucketName") String bucketName, 
                                @RequestParam("prefix") String prefix,
                                @RequestParam("keyword") String keyword) throws Exception {
        
        return minioService.searchFiles(bucketName, prefix, keyword);
    }

    @GetMapping("/meta")
    public SaResult getBucketMetaData(@RequestParam("bucketName") String bucketName) throws Exception {
        SaResult result = minioService.getBucketMetaData(bucketName);
        return result;
    }

    @PutMapping("/rename")
    public SaResult renameFile(@RequestParam("bucketName") String bucketName, 
                               @RequestParam("oldName") String oldName,
                               @RequestParam("newName") String newName) throws Exception {
        minioService.renameFile(bucketName, oldName, newName);
        return SaResult.data("重命名成功");
    }

    @PutMapping("/rename/dir")
    public SaResult renameDir(@RequestParam("bucketName") String bucketName, 
                              @RequestParam("oldName") String oldName,
                              @RequestParam("newName") String newName) throws Exception {
        minioService.renameDirectory(bucketName, oldName, newName);
        return SaResult.data("重命名成功");
    }
}
