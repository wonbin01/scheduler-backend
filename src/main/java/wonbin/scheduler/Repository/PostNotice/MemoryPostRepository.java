package wonbin.scheduler.Repository.PostNotice;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import wonbin.scheduler.Entity.Post.PostInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@Setter
public class MemoryPostRepository implements PostRepository{
    private final List<PostInfo> posts=new ArrayList<>();
    private  Long sequence=0L;

    @Override
    public PostInfo save(String title,String content,String category) {
        PostInfo saved=new PostInfo(++sequence,title,content,category);
        posts.add(saved);
        return saved;
    }

    @Override
    public List<PostInfo> findByCategory(String category) {
        return posts.stream()
                .filter(post-> post.getCategoryName().equals(category))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<PostInfo> findById(int id) {
        for(PostInfo variable: posts){
            if(variable.getId()==id){
                return Optional.of(variable);
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean DeleteById(int id) {
        Optional<PostInfo> found = findById(id);
        if (found.isEmpty()) return false;
        posts.remove(found.get());  // Optional 안의 실제 PostInfo 객체를 삭제해야 함
        return true;
    }

}
