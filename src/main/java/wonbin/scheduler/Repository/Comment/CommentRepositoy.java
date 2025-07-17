package wonbin.scheduler.Repository.Comment;

import wonbin.scheduler.Entity.Comment.CommentInfo;

public interface CommentRepositoy {
    public void save_comment(CommentInfo comment);
    public void delete_comment(CommentInfo comment);
    public void update_comment(CommentInfo comment);
}
