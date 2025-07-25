package wonbin.scheduler.Repository.Comment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wonbin.scheduler.Entity.Comment.CommentInfo;

import java.sql.*;
import java.util.List;

@Repository
@Slf4j
public class JDBCCommentRepository implements CommentRepositoy{

    private final JdbcTemplate jdbcTemplate;
    private final CommentInfoRowMapper commentInfoRowMapper=new CommentInfoRowMapper();
    public JDBCCommentRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }
    @Override
    public void save_comment(CommentInfo created) {
        String sql="INSERT INTO comment_info (username,userId,comment_content,postedId,updated,createdAt) VALUES (?,?,?,?,?,?)";

        KeyHolder keyHolder=new GeneratedKeyHolder();
        //그냥 업데이트하면 자동생성된 key값을 가져올 수 없어서, 지동 생성한 키 값을 받아오게 설정
        jdbcTemplate.update(connection ->{
            PreparedStatement ps=connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1,created.getUsername());
            ps.setInt(2,created.getUserId());
            ps.setString(3,created.getComment_content());
            ps.setLong(4,created.getPostedId());
            ps.setBoolean(5,false);
            ps.setTimestamp(6, Timestamp.valueOf(created.getCreatedAt()));
            return ps;
        },keyHolder);

        if(keyHolder.getKey()!=null){
            created.setComment_Id(keyHolder.getKey().longValue());
        }
    }

    @Override
    public void delete_comment(CommentInfo comment) {
        String sql="DELETE FROM comment_info WHERE comment_id=?";
        jdbcTemplate.update(sql,comment.getComment_Id());
    }

    @Override
    public void update_comment(CommentInfo comment) {
        String sql="UPDATE comment_info SET comment_content=?, createdAt=?, updated=? WHERE comment_id=?";
        int affectedRows=jdbcTemplate.update(
                sql,
                comment.getComment_content(),
                comment.getCreatedAt(),
                true,
                comment.getComment_Id());
        if(affectedRows<1){
            log.info("업데이트 실패 comment_id={}",comment.getComment_Id());
        }
        log.info("업데이트 성공 comment_id={}",comment.getComment_Id());
    }

    @Override
    public List<CommentInfo> findby_Id(Long id) {
        String sql="SELECT * FROM comment_info WHERE postedId=?";
        return jdbcTemplate.query(sql,commentInfoRowMapper,id);
    }

    private static class CommentInfoRowMapper implements RowMapper<CommentInfo> {
        @Override
        public CommentInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
            CommentInfo commentInfo=new CommentInfo();
            commentInfo.setComment_Id(rs.getLong("comment_id"));
            commentInfo.setUserId(rs.getInt("userId"));
            commentInfo.setUsername(rs.getString("username"));
            commentInfo.setComment_content(rs.getString("comment_content"));
            commentInfo.setPostedId(rs.getLong("postedId"));
            commentInfo.setUpdated(rs.getBoolean("updated"));
            commentInfo.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
            return commentInfo;
        }
    }
}
