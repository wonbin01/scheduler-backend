package wonbin.scheduler.Repository.Category;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import wonbin.scheduler.Entity.Member.MemberInfo;
import wonbin.scheduler.Entity.NoticeCategory.NoticeCategory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class JDBCCategoryRepository implements CategoryRepository{

    private final JdbcTemplate jdbcTemplate;
    RowMapper<NoticeCategory> rowMapper=new NoticeCategoryRowMapper();
    public JDBCCategoryRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }

    private static class NoticeCategoryRowMapper implements RowMapper<NoticeCategory> {
        @Override
        public NoticeCategory mapRow(ResultSet rs, int rowNum) throws SQLException {
            NoticeCategory noticeCategory=new NoticeCategory();
            noticeCategory.setName(rs.getString("name"));
            return noticeCategory;
        }
    }

    @Override
    public Optional<NoticeCategory> find_by_name(String name) {
        String sql="SELECT name FROM notice_category WHERE name=?";
        try{
            NoticeCategory category=jdbcTemplate.queryForObject(sql,rowMapper,name);
            return Optional.ofNullable(category);
        } catch (EmptyResultDataAccessException e){
            return Optional.empty();
        }
    }

    @Override
    public void save_category(String newcategory) {
        log.info("카테고리 저장 시도");
        Optional<NoticeCategory> foundCategory=find_by_name(newcategory);
        if(!foundCategory.isEmpty()){
            log.info("이미 존재하는 카테고리입니다. category={}",newcategory);
            return;
        }
        String sql="INSERT INTO notice_category (name) VALUES (?)";
        jdbcTemplate.update(sql,newcategory);
        log.info("카테고리 추가 성공 category={}",newcategory);
    }

    @Override
    public List<String> find_all_category() {
        String sql="SELECT name FROM notice_category";
        return jdbcTemplate.queryForList(sql,String.class);
    }
}
