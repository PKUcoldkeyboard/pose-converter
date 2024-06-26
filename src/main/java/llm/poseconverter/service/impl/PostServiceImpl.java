package llm.poseconverter.service.impl;

import java.time.LocalDateTime;

import javax.annotation.Resource;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.springframework.stereotype.Service;

import llm.poseconverter.entity.Post;
import llm.poseconverter.exception.CustomException;
import llm.poseconverter.mapper.PostMapper;
import llm.poseconverter.service.PostService;

@Service
public class PostServiceImpl implements PostService{

    @Resource
    private PostMapper postMapper;

    @Override
    public Post addPost(Post post) {
        post.setCreateTime(LocalDateTime.now());
        post.setUpdateTime(LocalDateTime.now());
        postMapper.insert(post);
        return post;
    }

    @Override
    public void deletePost(Long id) {
        postMapper.deleteById(id);
    }

    @Override
    public Post getPost(Long id) {
        Post post = postMapper.selectById(id);
        if (post == null) {
            throw new CustomException("该帖子不存在");
        }
        return post;
    }

    @Override
    public void updatePost(Post post) {
        Post existingPost = postMapper.selectById(post.getId());
        if (existingPost == null) {
            throw new CustomException("该帖子不存在");
        }
        post.setUpdateTime(LocalDateTime.now());
        postMapper.updateById(post);
    }

    @Override
    public Page<Post> getPostList(Long pageNum, Long pageSize) {
        Page<Post> page = new Page<>(pageNum, pageSize);
        Page<Post> postPage = postMapper.selectPage(page, null);
        return postPage;
    }

    @Override
    public Page<Post> getPostListByUserId(Long userId, Long pageNum, Long pageSize) {
        Page<Post> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        Page<Post> postPage = postMapper.selectPage(page, queryWrapper);
        return postPage;
    }
}
