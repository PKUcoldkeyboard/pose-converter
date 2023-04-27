package llm.poseconverter.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.util.SaResult;
import llm.poseconverter.entity.Attachment;
import llm.poseconverter.exception.CustomException;
import llm.poseconverter.service.AttachmentService;

@RestController
@RequestMapping("/api/attachment")
public class AttachmentController {
    
    @Resource
    private AttachmentService attachmentService;

    @GetMapping("/{id}")
    @ResponseBody
    public SaResult getAttachmentById(@PathVariable Long id) {
        Attachment attachment = attachmentService.getAttachmentById(id);
        if (attachment == null) {
            throw new CustomException("该附件不存在");
        }
        return SaResult.data(attachment);
    }

    @GetMapping("/post/{postId}")
    @ResponseBody
    public SaResult getAttachmentList(@PathVariable Long postId) {
        List<Attachment> attachments = attachmentService.getAttachmentsByPostId(postId);
        return SaResult.data(attachments);
    }

    @PostMapping("/")
    @ResponseBody
    public SaResult createAttachment(@RequestBody Attachment attachment) {
        Attachment createdAttachment = attachmentService.saveAttachment(attachment);
        return SaResult.data(createdAttachment);
    }

    @PutMapping("/{id}")
    @ResponseBody
    public SaResult updateAttachment(@PathVariable Long id, @RequestBody Attachment attachment) {
        attachment.setId(id);
        attachmentService.updateAttachment(attachment);
        return SaResult.data(attachment);
    }

    @DeleteMapping("/{id}")
    public SaResult deleteAttachment(@PathVariable Long id) {
        attachmentService.deleteAttachment(id);
        return SaResult.ok();
    }
}
