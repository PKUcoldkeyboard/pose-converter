package llm.poseconverter.service;

import java.util.List;


import llm.poseconverter.entity.Post;

public interface PostService {
    /*
    *  帖子的增删查改
    */
    Post addPost(Post post);
    void deletePost(Long id);
    Post getPost(Long id);
    void updatePost(Post post);
    List<Post> getPostList(Long pageNum, Long pageSize);
}
