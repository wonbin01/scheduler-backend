package wonbin.scheduler.Service.notice;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import wonbin.scheduler.Entity.Post.PostInfo;
import wonbin.scheduler.Repository.Category.CategoryRepository;
import wonbin.scheduler.Repository.PostNotice.PostRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class NoticeService {
    private final CategoryRepository categoryRepository;
    private final PostRepository postRepository;

    public ResponseEntity<?> getCategoryList() {
        List<String> categories = categoryRepository.find_all_category();
        Map<String, Object> response = new HashMap<>();
        response.put("categories", categories);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> addCategory(String categoryName) {
        List<String> allCategory = categoryRepository.find_all_category();
        if (allCategory.contains(categoryName.trim())) {
            log.info("중복되 카테고리입니다.");
            return ResponseEntity.badRequest().body("중복된 카테고리");
        }
        if (categoryName.isEmpty() || categoryName.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("카테고리 이름이 비어있습니다.");
        }
        categoryRepository.save_category(categoryName);
        log.info("카테고리 : {} 추가", categoryName.trim());
        return ResponseEntity.ok("카테고리 추가 성공");
    }

    public List<PostInfo> getPostByCategory(String category) {
        log.info("category : {}", category);
        List<PostInfo> posts = postRepository.findByCategory(category);
        posts.sort(Comparator.comparing(PostInfo::getId).reversed());
        return posts;
    }

    public PostInfo createPost(String category, PostInfo post) {
        post.setCategoryName(category);
        PostInfo savedPost = postRepository.save(post.getTitle(), post.getContent(), category);
        log.info("post 성공! : {}", category);
        return savedPost;
    }
}
