package llm.poseconverter.service;

import java.util.List;

import llm.poseconverter.entity.Attachment;

public interface AttachmentService {
    Attachment saveAttachment(Attachment attachment); 
    Attachment getAttachmentById(Long id);
    List<Attachment> getAttachmentsByPostId(Long postId);
    Attachment updateAttachment(Attachment updatedAttachment);
    void deleteAttachment(Long id);
}
