package wonbin.scheduler.Service.comment;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wonbin.scheduler.Entity.Comment.CommentInfo;
import wonbin.scheduler.Repository.Comment.CommentRepositoy;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepositoy CommentRepository;

    public void saveComment(long id, CommentInfo comment, HttpSession session) {
        comment.setCommentInfo(id, comment, session);
        CommentRepository.save_comment(comment);
    }

    public List<CommentInfo> findById(long id) {
        return CommentRepository.findby_Id(id);
    }
}
