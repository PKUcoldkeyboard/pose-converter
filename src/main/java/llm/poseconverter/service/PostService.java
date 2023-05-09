package llm.poseconverter.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import llm.poseconverter.entity.Post;

public interface PostService {
    /*
    *  帖子的增删查改
    */
    Post addPost(Post post);
    void deletePost(Long id);
    Post getPost(Long id);
    void updatePost(Post post);
    Page<Post> getPostList(Long pageNum, Long pageSize);
    Page<Post> getPostListByUserId(Long userId, Long pageNum, Long pageSize);
}
