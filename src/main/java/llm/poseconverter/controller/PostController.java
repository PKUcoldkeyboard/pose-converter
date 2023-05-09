package llm.poseconverter.controller;

import javax.annotation.Resource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.util.SaResult;
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

    @GetMapping("/user/{userId}")
    @ResponseBody
    public SaResult getPostListByUserId(@PathVariable Long userId, @RequestParam("pageNum") Long pageNum, @RequestParam("pageSize") Long pageSize) {
        Page<Post> posts = postService.getPostListByUserId(userId, pageNum, pageSize);
        return SaResult.data(posts);
    }

    @GetMapping("/")
    @ResponseBody
    public SaResult getPostList(@RequestParam("pageNum") Long pageNum, @RequestParam("pageSize") Long pageSize) {
        Page<Post> posts = postService.getPostList(pageNum, pageSize);
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
