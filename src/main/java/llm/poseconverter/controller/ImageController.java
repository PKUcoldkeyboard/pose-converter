package llm.poseconverter.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.util.SaResult;
import llm.poseconverter.service.ImageService;

@RestController
@RequestMapping("/api/image")
public class ImageController {
    @Resource
    private ImageService imageService;

    @PostMapping("convert")
    @ResponseBody
    public SaResult convert(@RequestParam String bucketName, @RequestParam("imageUrl") String imageUrl) throws Exception {
        return SaResult.data(imageService.convert(bucketName, imageUrl));
    }
}
