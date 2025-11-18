package wonbin.scheduler.Service.comment;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import wonbin.scheduler.Entity.Comment.CommentInfo;
import wonbin.scheduler.Repository.Comment.CommentRepositoy;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final CommentRepositoy commentRepository;

    public void saveComment(long id, CommentInfo comment, HttpSession session) {
        comment.setCommentInfo(id, comment, session);
        commentRepository.save_comment(comment);
    }

    public List<CommentInfo> findById(long id) {
        return commentRepository.findby_Id(id);
    }

    public ResponseEntity<String> deleteComment(long id, long commentId) {
        List<CommentInfo> commentInfos = findById(id); //게시글 찾기
        if (commentInfos.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시글을 찾을 수 없습니다");
        }
        for (CommentInfo candidate : commentInfos) {
            if (candidate.getComment_Id() == commentId) {
                log.info("댓글 삭제 왼료 commentId={}", commentId);
                break;
            }
        }
        return ResponseEntity.ok("댓글이 삭제되었습니다.");
    }
}
