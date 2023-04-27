package llm.poseconverter.service;

import java.util.List;

import llm.poseconverter.entity.Comment;

public interface CommentService {
    Comment saveComment(Comment comment);
    Comment getCommentById(Long id);
    List<Comment> getCommentsByPostId(Long postId, Long pageNum, Long pageSize);
    Comment updateComment(Long id, Comment updatedComment); 
    void deleteComment(Long id);
}
