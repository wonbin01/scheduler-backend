package wonbin.scheduler.Repository.Comment;

import wonbin.scheduler.Entity.Comment.CommentInfo;

import java.lang.reflect.Array;
import java.util.*;

public class MemoryCommentRepository implements CommentRepositoy {
    private final Map<Long, List<CommentInfo>> hm = new HashMap<>();
    private long commentIdSequence = 0;
    @Override
    public void save_comment(CommentInfo comment) {
        commentIdSequence++;               // ID 1 증가
        comment.setComment_Id(commentIdSequence);  // 댓글에 ID 할당

        Long postingId = comment.getPostedId();
        hm.computeIfAbsent(postingId, k -> new ArrayList<>()).add(comment);
    }

    @Override
    public void delete_comment(CommentInfo comment) {
        Long postingId = comment.getPostedId();
        List<CommentInfo> comments = hm.get(postingId);
        if (comments != null) {
            comments.removeIf(c -> c.getComment_Id().equals(comment.getComment_Id()));

            if (comments.isEmpty()) {
                hm.remove(postingId);
            }
        }
    }

    @Override
    public void update_comment(CommentInfo comment) {
        Long postingId = comment.getPostedId();
        List<CommentInfo> comments = hm.get(postingId);
        if (comments != null) {
            for (int i = 0; i < comments.size(); i++) {
                CommentInfo c = comments.get(i);
                if (c.getComment_Id().equals(comment.getComment_Id())) {
                    comments.set(i, comment);
                    return;
                }
            }
        }
        throw new IllegalArgumentException("수정할 댓글을 찾을 수 없습니다.");
    }

    @Override
    public List<CommentInfo> findby_Id(Long id) {
        return hm.getOrDefault(id, Collections.emptyList());
    }
}
