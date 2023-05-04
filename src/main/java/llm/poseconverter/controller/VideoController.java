package llm.poseconverter.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.util.SaResult;
import llm.poseconverter.service.VideoService;

@RestController
@RequestMapping("/api/video")
public class VideoController {
    @Resource
    private VideoService videoService;

    @PostMapping("convert")
    @ResponseBody
    public SaResult convert(@RequestParam String bucketName, @RequestParam("videoUrl") String videoUrl) throws Exception {
        return SaResult.data(videoService.convert(bucketName, videoUrl));
    }
}
