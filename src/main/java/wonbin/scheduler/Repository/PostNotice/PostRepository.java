package wonbin.scheduler.Repository.PostNotice;

import wonbin.scheduler.Entity.Post.PostInfo;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    public PostInfo save(String title,String content,String category);
    public List<PostInfo> findByCategory(String category);
    public Optional<PostInfo> findById(int id);
    public boolean DeleteById(int id);
}
