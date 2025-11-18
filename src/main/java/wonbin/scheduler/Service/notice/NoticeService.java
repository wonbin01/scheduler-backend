package wonbin.scheduler.Service.notice;

import jakarta.servlet.http.HttpSession;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import wonbin.scheduler.Entity.Member.MemberInfo;
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

    public ResponseEntity<?> getDetailedPost(long postId, HttpSession session) {
        Optional<PostInfo> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty()) {
            log.info("해당 postId를 찾을 수 없습니다 : {}", postId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 게시글이 존재하지 않습니다");
        }
        PostInfo post = optionalPost.get();
        MemberInfo loginMember = (MemberInfo) session.getAttribute("loginMember");
        loginMember.setPassword("");
        Map<String, Object> response = new HashMap<>();
        response.put("post", post);
        response.put("userInfo", loginMember);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> deletePost(long postId) {
        boolean deleted = postRepository.DeleteById(postId);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 게시글이 없습니다.");
        }
        log.info("삭제 성공");
        return ResponseEntity.ok(String.format("삭제 성공 ID=%d", postId));
    }

    public void updatePost(String category, long postId, PostInfo update) {
        Optional<PostInfo> optionalPost = postRepository.findById(postId);
        if (optionalPost.isPresent()) {
            PostInfo post = optionalPost.get();
            post.setTitle(update.getTitle());
            post.setContent(update.getContent());
            post.setCategoryName(category);
            postRepository.update(post);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다.");
        }
    }
}
