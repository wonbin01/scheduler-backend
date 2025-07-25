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
            scheduleApplyInfo.setTimeSlot(rs.getString("timeslot"));
            scheduleApplyInfo.setApplyDate(rs.getTimestamp("applydate").toLocalDateTime().toLocalDate());
            scheduleApplyInfo.setReason(rs.getString("reason"));
            scheduleApplyInfo.setAlternativePlan(rs.getString("alternativePlan"));
            scheduleApplyInfo.setEtc(rs.getString("etc"));
            scheduleApplyInfo.setCreateAt(rs.getTimestamp("createdAt").toLocalDateTime());
            scheduleApplyInfo.setUpdatedAt(rs.getBoolean("updatedAt"));
            scheduleApplyInfo.setApplyId(rs.getLong("applyId"));
            return scheduleApplyInfo;
        }
    }

    @Override
    public void save(ScheduleApplyInfo info) {
        String sql="INSERT INTO apply_info (usernumber,username,timeslot,applydate,reason,alternativePlan,etc,createdAt,updatedAt) VALUES (?,?,?,?,?,?,?,?,?)";
        KeyHolder keyHolder=new GeneratedKeyHolder();

        jdbcTemplate.update(connection ->{
            PreparedStatement ps=connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1,info.getUsernumber());
            ps.setString(2,info.getUsername());
            ps.setString(3,info.getTimeSlot());
            ps.setTimestamp(4,Timestamp.valueOf(info.getApplyDate().atStartOfDay()));
            ps.setString(5,info.getReason());
            ps.setString(6,info.getAlternativePlan());
            ps.setString(7,info.getEtc());
            ps.setTimestamp(8,Timestamp.valueOf(LocalDateTime.now()));
            ps.setBoolean(9,false);
            return ps;
        },keyHolder);
        if(keyHolder.getKey()!=null){
            info.setApplyId(keyHolder.getKey().longValue());
        }
    }

    @Override
    public List<ScheduleApplyInfo> findApplyUseMonth(int year, int month) {
        String sql="SELECT * FROM apply_info WHERE YEAR(applydate)=? AND MONTH(applydate)=?";
        return jdbcTemplate.query(sql,applyInfoRowMapper,year,month);
    }

    @Override
    public void delete(long applyId) {
        String sql="DELETE FROM apply_info WHERE applyId=?";
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
        String sql = "SELECT applyId, usernumber, username, timeslot, applydate, reason, alternativePlan, etc, createdAt, updatedAt FROM apply_info WHERE applyId = ?";
        try {
            return jdbcTemplate.queryForObject(sql, applyInfoRowMapper, applyId);
        } catch (EmptyResultDataAccessException e) {
            log.info("해당 applyId를 가진 신청 정보를 찾을 수 없습니다: applyId={}", applyId);
            return null; // 결과가 없을 경우 null 반환
        }
    }

    @Override
    public void update(ScheduleApplyInfo info) {
        String sql = "UPDATE apply_info SET usernumber = ?, username = ?, timeslot = ?, applydate = ?, reason = ?, alternativePlan = ?, etc = ?, updatedAt = ? WHERE applyId = ?";

        // update 메서드는 영향받은 행의 수를 반환하지만, 여기서는 void 타입이므로 반환값을 무시합니다.
        jdbcTemplate.update(sql,
                info.getUsernumber(),
                info.getUsername(),
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
