package wonbin.scheduler.Repository.Comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wonbin.scheduler.Entity.Comment.CommentInfo;

import java.util.List;

@Repository
public interface JpaCommentRepository extends JpaRepository<CommentInfo,Long> {
    List<CommentInfo> findByPostedId(Long postId);
}
