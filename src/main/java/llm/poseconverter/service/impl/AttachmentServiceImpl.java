package llm.poseconverter.service.impl;

import java.util.List;

import javax.annotation.Resource;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import org.springframework.stereotype.Service;

import llm.poseconverter.entity.Attachment;
import llm.poseconverter.exception.CustomException;
import llm.poseconverter.mapper.AttachmentMapper;
import llm.poseconverter.service.AttachmentService;

@Service
public class AttachmentServiceImpl implements AttachmentService {
    
    @Resource
    private AttachmentMapper attachmentMapper;

    @Override
    public Attachment saveAttachment(Attachment attachment) {
        attachmentMapper.insert(attachment);
        return attachment;
    }

    @Override
    public Attachment getAttachmentById(Long id) {
        Attachment attachment = attachmentMapper.selectById(id);
        if (attachment == null) {
            throw new CustomException("该附件不存在");
        }
        return attachment;
    }

    @Override
    public List<Attachment> getAttachmentsByPostId(Long postId) {
        LambdaQueryWrapper<Attachment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Attachment::getPostId, postId);
        List<Attachment> attachments = attachmentMapper.selectList(queryWrapper);
        return attachments;
    }

    @Override
    public Attachment updateAttachment(Attachment updatedAttachment) {
        attachmentMapper.updateById(updatedAttachment);
        return updatedAttachment;
    }

    @Override
    public void deleteAttachment(Long id) {
        attachmentMapper.deleteById(id);
    }
    
}
