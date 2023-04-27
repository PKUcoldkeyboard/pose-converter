package llm.poseconverter.service.impl;

import java.util.List;

import javax.annotation.Resource;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

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
    public List<Comment> getCommentsByPostId(Long postId, Long pageNum, Long pageSize) {
        Page<Comment> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getPostId, postId);
        Page<Comment> commentPage = commentMapper.selectPage(page, queryWrapper);
        List<Comment> records = commentPage.getRecords();
        return records;
    }

    @Override
    public Comment updateComment(Long id, Comment updatedComment) {
        Comment existingComment = commentMapper.selectById(id);
        if (existingComment == null) {
            throw new CustomException("该评论不存在");
        }
        commentMapper.updateById(updatedComment);
        return existingComment;
    }

    @Override
    public void deleteComment(Long id) {
        commentMapper.deleteById(id);
    }
}
