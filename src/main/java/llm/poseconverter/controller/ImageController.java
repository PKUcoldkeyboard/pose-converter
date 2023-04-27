package llm.poseconverter.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cn.dev33.satoken.util.SaResult;
import llm.poseconverter.service.ImageService;

@RestController
@RequestMapping("/api/image")
public class ImageController {
    @Resource
    private ImageService imageService;

    @PostMapping("convert")
    @ResponseBody
    public SaResult convert(@RequestParam("file") MultipartFile file) throws Exception {
        // 检查是否是mp4
        String fileName = file.getOriginalFilename();
        if (!fileName.endsWith(".png")) {
            return SaResult.error("请上传png格式的图片");
        }
        return imageService.convert(file);
    }
}
