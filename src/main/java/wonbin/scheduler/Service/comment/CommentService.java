package wonbin.scheduler.Service.comment;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import wonbin.scheduler.Entity.Comment.CommentInfo;
import wonbin.scheduler.Entity.Member.MemberInfo;
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

    public ResponseEntity<String> updateComment(long id, long commentId, CommentInfo editCommentContent,
                                                HttpSession session) {
        MemberInfo loginMember = validateSession(session);
        if (loginMember == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        List<CommentInfo> candidate = getCommentInfo(id);
        if (candidate.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시글이 존재하지 않습니다");
        }
        Optional<CommentInfo> targetComment = findCommentInfo(commentId, candidate,
                loginMember);
        if (targetComment.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("댓글이 존재하지 않습니다.");
        }
        CommentInfo comment = targetComment.get();
        comment.setComment_content(editCommentContent.getComment_content());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdated(true);
        commentRepository.update_comment(comment);
        return ResponseEntity.ok("댓글이 수정되었습니다.");
    }

    @NotNull
    private Optional<CommentInfo> findCommentInfo(long commentId, List<CommentInfo> candidate,
                                                  MemberInfo loginMember) {
        return candidate.stream()
                .filter(c -> Objects.equals(c.getUserId(), loginMember.getUsernumber())
                        && Objects.equals(c.getComment_Id(), commentId))
                .findFirst();
    }

    private List<CommentInfo> getCommentInfo(long id) {
        List<CommentInfo> candidate = commentRepository.findby_Id(id); //게시글확인
        if (candidate.isEmpty()) {
            return null;
        }
        return candidate;
    }

    private MemberInfo validateSession(HttpSession session) {
        MemberInfo loginMember = (MemberInfo) session.getAttribute("loginMember");
        if (loginMember == null) {
            return null;
        }
        return loginMember;
    }
}
