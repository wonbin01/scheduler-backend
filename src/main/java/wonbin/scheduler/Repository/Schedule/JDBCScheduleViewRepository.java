package wonbin.scheduler.Repository.Schedule;

import lombok.RequiredArgsConstructor;
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

import java.sql.*;
import java.util.List;

@Slf4j
@Repository
public class JDBCScheduleViewRepository implements ScheduleViewRepository{

    private final JdbcTemplate jdbcTemplate;
    @Autowired
    MemberInfoRepository memberInfoRepository;

    public JDBCScheduleViewRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }
    ViewInfoRowMapper viewInfoRowMapper=new ViewInfoRowMapper();

    private static class ViewInfoRowMapper implements RowMapper<ScheduleViewInfo> {
        @Override
        public ScheduleViewInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
            ScheduleViewInfo scheduleviewInfo=new ScheduleViewInfo();
            scheduleviewInfo.setUserNumber(rs.getInt("userNumber"));
            scheduleviewInfo.setPosition(rs.getString("position"));
            scheduleviewInfo.setApplyDate(rs.getTimestamp("applyDate").toLocalDateTime().toLocalDate());
            scheduleviewInfo.setUserName(rs.getString("userName"));
            scheduleviewInfo.setStartTime(rs.getTime("startTime").toLocalTime());
            scheduleviewInfo.setEndTime(rs.getTime("endTime").toLocalTime());
            scheduleviewInfo.setScheduleEventId(rs.getLong("scheduleEventId"));
            return scheduleviewInfo;
        }
    }
    @Override
    public List<ScheduleViewInfo> findByYear_Month(int year, int month) {
        String sql = "SELECT userNumber,position,applydate,userName,startTime,endTime,scheduleEventId FROM view_info WHERE YEAR(applyDate)=? AND MONTH(applyDate)=?";
        List<ScheduleViewInfo> schedules = jdbcTemplate.query(sql, viewInfoRowMapper, year, month);
        return schedules;
    }

    @Override
    public void save(ScheduleViewInfo info) {
        String sql="INSERT INTO view_info (userNumber,position,applyDate,userName,startTime,endTime) VALUES (?,?,?,?,?,?)";
        KeyHolder keyHolder=new GeneratedKeyHolder();
        jdbcTemplate.update(connection->{
            PreparedStatement ps=connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1,info.getUserNumber());
            ps.setString(2,info.getPosition());
            ps.setTimestamp(3, Timestamp.valueOf(info.getApplyDate().atStartOfDay()));
            ps.setString(4,info.getUserName());
            ps.setTime(5, Time.valueOf(info.getStartTime()));
            ps.setTime(6,Time.valueOf(info.getEndTime()));
            return ps;
        },keyHolder);
        if(keyHolder.getKey()!=null){
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
        String sql="SELECT * FROM view_info WHERE scheduleEventId=?";
        try{
            return jdbcTemplate.queryForObject(sql,viewInfoRowMapper,id);
        } catch (EmptyResultDataAccessException e){
            log.info("해당 scheduleEventId를 찾을 수 없습니다. scheduleEventId={}",id);
            return null;
        }
    }

    @Override
    public void delete(ScheduleViewInfo info) {
        String sql="DELETE FROM view_info WHERE scheduleEventId=?";
        int affectedRow=jdbcTemplate.update(sql,info.getScheduleEventId());
        if(affectedRow>0){
            log.info("삭제 성공 scheduleEventId={}",info.getScheduleEventId());
        } else{
            log.info("삭제 성공 scheduleEvnetId={}",info.getScheduleEventId());
        }
    }
}
