package wonbin.scheduler.Repository.Schedule;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import wonbin.scheduler.Entity.Schedule.AllowedDate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@Transactional
public class JDBCAllowedDateRepository implements AllowedDateRepository{

    private final JdbcTemplate jdbcTemplate;
    public JDBCAllowedDateRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }

    @Override
    public List<AllowedDate> findAllAllowedDate() {
        String sql="SELECT date,id FROM allowed_date";
        return jdbcTemplate.query(sql,(rs, rowNum) -> {
            AllowedDate allowedDate=new AllowedDate();
            allowedDate.setDate(rs.getString("date"));
            allowedDate.setId(rs.getLong("id"));
            return allowedDate;
        });
    }

    @Override
    public boolean deleteAllowedDate(String date) {
        String sql="DELETE FROM allowed_date WHERE date=?";
        int rowAffected=jdbcTemplate.update(sql,date);
        return rowAffected>0;
    }

    @Override
    public boolean saveAllowedDate(List<String> dates) {
        String sql = "INSERT IGNORE INTO allowed_date (date) VALUES (?)";
        int[] result= jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1,dates.get(i));
            }

            @Override
            public int getBatchSize() {
                return dates.size();
            }
        });
        boolean hasDuplicate = false;
        for (int res : result) {
            if (res == 0) {
                hasDuplicate = true;
                break;
            }
        }
        return !hasDuplicate;
    }
}
