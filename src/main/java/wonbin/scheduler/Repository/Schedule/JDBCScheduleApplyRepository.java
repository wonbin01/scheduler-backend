package wonbin.scheduler.Repository.Schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import wonbin.scheduler.Entity.Schedule.ScheduleApplyInfo;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@Slf4j
@Transactional
public class JDBCScheduleApplyRepository implements ScheduleApplyRepository{
    private final JdbcTemplate jdbcTemplate;
    public JDBCScheduleApplyRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }
    ApplyInfoRowMapper applyInfoRowMapper=new ApplyInfoRowMapper();

    private static class ApplyInfoRowMapper implements RowMapper<ScheduleApplyInfo> {
        @Override
        public ScheduleApplyInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
            ScheduleApplyInfo scheduleApplyInfo=new ScheduleApplyInfo();
            scheduleApplyInfo.setUsernumber(rs.getInt("usernumber"));
            scheduleApplyInfo.setUsername(rs.getString("username"));
            scheduleApplyInfo.setTimeSlot(rs.getString("time_slot"));
            scheduleApplyInfo.setApplyDate(rs.getTimestamp("apply_date").toLocalDateTime().toLocalDate());
            scheduleApplyInfo.setReason(rs.getString("reason"));
            scheduleApplyInfo.setAlternativePlan(rs.getString("alternative_plan"));
            scheduleApplyInfo.setEtc(rs.getString("etc"));
            scheduleApplyInfo.setCreateAt(rs.getTimestamp("create_at").toLocalDateTime());
            scheduleApplyInfo.setUpdatedAt(rs.getBoolean("updated_at"));
            scheduleApplyInfo.setApplyId(rs.getLong("apply_id"));
            return scheduleApplyInfo;
        }
    }

    @Override
    public void save(ScheduleApplyInfo info) {
        String sql="INSERT INTO apply_info " +
                "(usernumber,time_slot,apply_date,reason,alternative_plan,etc,create_at,updated_at) VALUES (?,?,?,?,?,?,?,?)";
        KeyHolder keyHolder=new GeneratedKeyHolder();

        jdbcTemplate.update(connection ->{
            PreparedStatement ps=connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1,info.getUsernumber());
//            ps.setString(2,info.getUsername());
            ps.setString(2,info.getTimeSlot());
            ps.setTimestamp(3,Timestamp.valueOf(info.getApplyDate().atStartOfDay()));
            ps.setString(4,info.getReason());
            ps.setString(5,info.getAlternativePlan());
            ps.setString(6,info.getEtc());
            ps.setTimestamp(7,Timestamp.valueOf(LocalDateTime.now()));
            ps.setBoolean(8,false);
            return ps;
        },keyHolder);
        if(keyHolder.getKey()!=null){
            info.setApplyId(keyHolder.getKey().longValue());
        }
    }

    @Override
    public List<ScheduleApplyInfo> findApplyUseMonth(int year, int month) {
        String sql = "SELECT a.apply_id, a.usernumber, m.username, a.time_slot, a.apply_date, " +
                "a.reason, a.alternative_plan, a.etc, a.create_at, a.updated_at " +
                "FROM apply_info a " +
                "JOIN member_info m ON a.usernumber = m.usernumber " +
                "WHERE YEAR(a.apply_date) = ? AND MONTH(a.apply_date) = ?";

        return jdbcTemplate.query(sql, applyInfoRowMapper, year, month);
    }

    @Override
    public void delete(long applyId) {
        String sql="DELETE FROM apply_info WHERE apply_id=?";
        int affectedRow=jdbcTemplate.update(sql,applyId);
        if(affectedRow>0){
            log.info("삭제 성공 applyId={}",applyId);
        }
        else {
            log.warn("삭제 실패 applyId={}",applyId);
        }
    }

    @Override
    public ScheduleApplyInfo findByApplyId(long applyId) {
        String sql = "SELECT a.applyId, a.usernumber, m.username, a.time_slot, a.apply_date, a.reason, a.alternative_plan, a.etc, a.create_at, a.updated_at " +
                "FROM apply_info a " +
                "join member_info m on a.usernumber=m.usernumber " +
                "WHERE apply_id = ? ";
        try {
            return jdbcTemplate.queryForObject(sql, applyInfoRowMapper, applyId);
        } catch (EmptyResultDataAccessException e) {
            log.info("해당 applyId를 가진 신청 정보를 찾을 수 없습니다: applyId={}", applyId);
            return null; // 결과가 없을 경우 null 반환
        }
    }

    @Override
    public void update(ScheduleApplyInfo info) {
        String sql = "UPDATE apply_info SET usernumber = ?, time_slot = ?, apply_date = ?, reason = ?, alternative_plan = ?, etc = ?, updated_at = ? WHERE apply_id = ?";

        // update 메서드는 영향받은 행의 수를 반환하지만, 여기서는 void 타입이므로 반환값을 무시합니다.
        jdbcTemplate.update(sql,
                info.getUsernumber(),
                info.getTimeSlot(),
                // applyDate (LocalDate)를 Timestamp로 변환
                Timestamp.valueOf(info.getApplyDate().atStartOfDay()),
                info.getReason(),
                info.getAlternativePlan(),
                info.getEtc(),
                true, // updatedAt 필드를 true로 설정 (수정됨을 표시)
                info.getApplyId() // WHERE 절의 조건 (Primary Key)
        );
    }
}
