package llm.poseconverter.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import cn.dev33.satoken.util.SaResult;
import llm.poseconverter.dto.PageDto;
import llm.poseconverter.entity.Comment;
import llm.poseconverter.service.CommentService;

@RestController
@RequestMapping("/api/comment")
public class CommentController {
    
    @Autowired
    private CommentService commentService;
    
    @GetMapping("/{id}")
    public SaResult getCommentById(@PathVariable Long id) {
        Comment comment = commentService.getCommentById(id);
        return SaResult.data(comment);
    }
    
    @GetMapping("/post/{postId}")
    public SaResult getCommentsByPostId(@PathVariable Long postId, @RequestBody @Valid PageDto pageDto) {
        List<Comment> comments = commentService.getCommentsByPostId(postId, pageDto.getPageNum(), pageDto.getPageSize());
        return SaResult.data(comments);
    }
    
    @PostMapping("/")
    public SaResult createComment(@RequestBody Comment comment) {
        Comment createdComment = commentService.saveComment(comment);
        return SaResult.data(createdComment);
    }
    
    @PutMapping("/{id}")
    public SaResult updateComment(@PathVariable Long id, @RequestBody Comment comment) {
        Comment updatedComment = commentService.updateComment(id, comment);
        return SaResult.data(updatedComment);
    }
    
    @DeleteMapping("/{id}")
    public SaResult deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return SaResult.ok("删除成功");
    }
}

