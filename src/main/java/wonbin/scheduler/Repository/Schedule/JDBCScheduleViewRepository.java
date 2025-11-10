package wonbin.scheduler.Repository.Schedule;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wonbin.scheduler.Entity.Schedule.ScheduleViewInfo;
import wonbin.scheduler.Repository.Member.MemberInfoRepository;

@Slf4j
@Repository
public class JDBCScheduleViewRepository implements ScheduleViewRepository {
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    MemberInfoRepository memberInfoRepository;

    public JDBCScheduleViewRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    ViewInfoRowMapper viewInfoRowMapper = new ViewInfoRowMapper();

    private static class ViewInfoRowMapper implements RowMapper<ScheduleViewInfo> {
        @Override
        public ScheduleViewInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
            ScheduleViewInfo scheduleviewInfo = new ScheduleViewInfo();
            scheduleviewInfo.setUserNumber(rs.getInt("user_number"));
            scheduleviewInfo.setPosition(rs.getString("position"));
            scheduleviewInfo.setApplyDate(rs.getTimestamp("apply_date").toLocalDateTime().toLocalDate());
            scheduleviewInfo.setStartTime(rs.getTime("start_time").toLocalTime());
            scheduleviewInfo.setEndTime(rs.getTime("end_time").toLocalTime());
            scheduleviewInfo.setScheduleEventId(rs.getLong("schedule_event_id"));
            scheduleviewInfo.setUserName(rs.getString("username"));
            return scheduleviewInfo;
        }
    }

    @Override
    public List<ScheduleViewInfo> findByYear_Month(int year, int month) {
        String sql = "SELECT " +
                "v.user_number, v.position, v.apply_date, m.username, v.start_time, v.end_time, v.schedule_event_id " +
                "FROM view_info v " +
                "JOIN member_info m ON m.usernumber = v.user_number " +
                "WHERE YEAR(v.apply_date) = ? AND MONTH(v.apply_date) = ?";
        List<ScheduleViewInfo> schedules = jdbcTemplate.query(sql, viewInfoRowMapper, year, month);
        return schedules;
    }

    @Override
    public void save(ScheduleViewInfo info) {
        String sql = "INSERT INTO view_info (user_number,position,apply_date,start_time,end_time) VALUES (?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, info.getUserNumber());
            ps.setString(2, info.getPosition());
            ps.setTimestamp(3, Timestamp.valueOf(info.getApplyDate().atStartOfDay()));
//            ps.setString(4,info.getUserName());
            ps.setTime(4, Time.valueOf(info.getStartTime()));
            ps.setTime(5, Time.valueOf(info.getEndTime()));
            return ps;
        }, keyHolder);
        if (keyHolder.getKey() != null) {
            info.setScheduleEventId(keyHolder.getKey().longValue());
        }
    }

    @Override
    public void saveAll(List<ScheduleViewInfo> infoList) {
        for (ScheduleViewInfo info : infoList) {
            save(info);
        }
    }

    @Override
    public ScheduleViewInfo findByScheduleId(long id) {
        String sql = "SELECT v.user_number, v.position, v.apply_date, " +
                "m.username, v.start_time, v.end_time, v.schedule_event_id " +
                "FROM view_info v " +
                "JOIN member_info m ON m.usernumber = v.user_number " +
                "WHERE v.schedule_event_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, viewInfoRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            log.info("해당 scheduleEventId를 찾을 수 없습니다. scheduleEventId={}", id);
            return null;
        }
    }

    @Override
    public void delete(ScheduleViewInfo info) {
        String sql = "DELETE FROM view_info WHERE schedule_event_id=?";
        int affectedRow = jdbcTemplate.update(sql, info.getScheduleEventId());
        if (affectedRow > 0) {
            log.info("삭제 성공 scheduleEventId={}", info.getScheduleEventId());
        } else {
            log.info("삭제 성공 scheduleEvnetId={}", info.getScheduleEventId());
        }
    }

    @Override
    public List<ScheduleViewInfo> findByUsernumberYearMonth(int usernumber, int year, int month) {
        String sql = "SELECT v.user_number, v.position, v.apply_date, " +
                "m.username, v.start_time, v.end_time, v.schedule_event_id " +
                "FROM view_info v " +
                "JOIN member_info m ON m.usernumber = v.user_number " +
                "WHERE v.user_number = ? AND YEAR(v.apply_date) = ? AND MONTH(v.apply_date) = ?";

        List<ScheduleViewInfo> schedules = jdbcTemplate.query(sql, viewInfoRowMapper, usernumber, year, month);
        return schedules;
    }

    @Override
    public int findByUserName(String username) {
        String sql = "SELECT usernumber FROM member_info WHERE username = ?";
        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, username.trim());
        } catch (EmptyResultDataAccessException e) {
            return -1;
        }
    }

}
