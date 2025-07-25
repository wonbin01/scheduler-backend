package wonbin.scheduler.Repository.PostNotice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wonbin.scheduler.Entity.Post.PostInfo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class JDBCPostRepository implements PostRepository{
    private final JdbcTemplate jdbcTemplate;
    private final PostInfoRowMapper postInfoRowMapper=new PostInfoRowMapper();

    public JDBCPostRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }

    private static class PostInfoRowMapper implements RowMapper<PostInfo>{
        @Override
        public PostInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
            PostInfo postInfo=new PostInfo();
            postInfo.setId(rs.getLong("id"));
            postInfo.setTitle(rs.getString("title"));
            postInfo.setContent(rs.getString("content"));
            postInfo.setCategoryName(rs.getString("categoryName"));
            return postInfo;
        }
    }

    @Override
    public PostInfo save(String title, String content, String category) {
        PostInfo created=new PostInfo();
        created.setTitle(title);
        created.setContent(content);
        created.setCategoryName(category);
        String sql="INSERT INTO post_info (title,content,categoryName) VALUES (?,?,?)";

        KeyHolder keyHolder=new GeneratedKeyHolder();
        //그냥 업데이트하면 자동생성된 key값을 가져올 수 없어서, 지동 생성한 키 값을 받아오게 설정
        jdbcTemplate.update(connection ->{
            PreparedStatement ps=connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1,created.getTitle());
            ps.setString(2,created.getContent());
            ps.setString(3,created.getCategoryName());
            return ps;
        },keyHolder);

        if(keyHolder.getKey()!=null){
            created.setId(keyHolder.getKey().longValue());
        }
        return created;
    }

    @Override
    public List<PostInfo> findByCategory(String category) {
        String sql="SELECT id,title,content,categoryName FROM post_info WHERE categoryName=?";
        return jdbcTemplate.query(sql,postInfoRowMapper,category);
    }

    @Override
    public Optional<PostInfo> findById(Long id) {
        String sql="SELECT id,title,content,categoryName FROM post_info WHERE id=?";
        try{
            PostInfo postInfo=jdbcTemplate.queryForObject(sql,postInfoRowMapper,id);
            return Optional.ofNullable(postInfo);
        } catch (EmptyResultDataAccessException e){
            return Optional.empty();
        }
    }

    @Override
    public boolean DeleteById(Long id) {
        String sql="DELETE FROM post_info WHERE id=?";
        int affectedRows=jdbcTemplate.update(sql,id);
        return affectedRows>0;
    }

    @Override
    public void update(PostInfo post) {
        String sql="UPDATE post_info SET title=?, content=?, categoryName=? WHERE id=?";
        int affectedRows=jdbcTemplate.update(
                sql,
                post.getTitle(),
                post.getContent(),
                post.getCategoryName(),
                post.getId());
        if(affectedRows==0){
            log.info("업데이트 실패 postedId={}",post.getId());
        }
        log.info("업데이트 성공 postedID={}",post.getId());
    }
}
