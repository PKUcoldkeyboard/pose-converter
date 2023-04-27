package llm.poseconverter.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

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
import llm.poseconverter.dto.PageDto;
import llm.poseconverter.entity.Post;
import llm.poseconverter.service.PostService;

@RestController
@RequestMapping("/api/post")
public class PostController {
    @Resource
    private PostService postService;

    @GetMapping("/{id}")
    @ResponseBody
    public SaResult getPostById(@PathVariable Long id) {
        Post post = postService.getPost(id);
        return SaResult.data(post);
    }

    @GetMapping("/")
    @ResponseBody
    public SaResult getPostList(@RequestBody @Valid PageDto pageDto) {
        List<Post> posts = postService.getPostList(pageDto.getPageNum(), pageDto.getPageSize());
        return SaResult.data(posts);
    }

    @PostMapping("/")
    @ResponseBody
    public SaResult createPost(@RequestBody Post post) {
        Post createdPost = postService.addPost(post);
        return SaResult.data(createdPost);
    }

    @PutMapping("/{id}")
    @ResponseBody
    public SaResult updatePost(@PathVariable Long id, @RequestBody Post post) {
        post.setId(id);
        postService.updatePost(post);
        return SaResult.data(post);
    }

    @DeleteMapping("/{id}")
    public SaResult deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return SaResult.ok();
    }
}
