package llm.poseconverter.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import javax.annotation.Resource;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import llm.poseconverter.entity.Comment;
import llm.poseconverter.exception.CustomException;
import llm.poseconverter.mapper.CommentMapper;
import llm.poseconverter.service.CommentService;

@Service
public class CommentServiceImpl implements CommentService {
    @Resource
    private CommentMapper commentMapper;

    @Override
    public Comment saveComment(Comment comment) {
        comment.setCreateTime(LocalDateTime.now());
        comment.setUpdateTime(LocalDateTime.now());
        commentMapper.insert(comment);
        return comment;
    }

    @Override
    public Comment getCommentById(Long id) {
        Comment comment = commentMapper.selectById(id);
        if (comment == null) {
            throw new CustomException("该评论不存在");
        }
        return comment;
    }

    @Override
    public List<Comment> getCommentsByPostId(Long postId) {
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getPostId, postId);
        List<Comment> records = commentMapper.selectList(queryWrapper);
        return records;
    }

    @Override
    public Comment updateComment(Long id, Comment updatedComment) {
        Comment existingComment = commentMapper.selectById(id);
        if (existingComment == null) {
            throw new CustomException("该评论不存在");
        }
        updatedComment.setUpdateTime(LocalDateTime.now());
        commentMapper.updateById(updatedComment);
        return existingComment;
    }

    @Override
    public void deleteComment(Long id) {
        commentMapper.deleteById(id);
    }

    @Override
    public List<Comment> getComments() {
        List<Comment> records = commentMapper.selectList(null);
        return records;
    }
}
