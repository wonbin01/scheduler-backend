package wonbin.scheduler.Repository.Comment;

import wonbin.scheduler.Entity.Comment.CommentInfo;

import java.util.List;

public interface CommentRepositoy {
    public void save_comment(CommentInfo comment);
    public void delete_comment(CommentInfo comment);
    public void update_comment(CommentInfo comment);
    public List<CommentInfo> findby_Id(Long id);
}
