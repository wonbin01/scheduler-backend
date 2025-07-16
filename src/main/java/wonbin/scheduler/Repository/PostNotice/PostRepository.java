package wonbin.scheduler.Repository.PostNotice;

import wonbin.scheduler.Entity.Post.PostInfo;

import java.util.List;

public interface PostRepository {
    public PostInfo save(String title,String content,String category);
    public List<PostInfo> findByCategory(String category);
    public List<PostInfo> findAll();
}
