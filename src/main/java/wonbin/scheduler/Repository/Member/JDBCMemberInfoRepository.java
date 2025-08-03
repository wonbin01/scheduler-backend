package wonbin.scheduler.Repository.Member;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import wonbin.scheduler.Entity.Member.MemberInfo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class JDBCMemberInfoRepository implements MemberInfoRepository{

    private final JdbcTemplate jdbcTemplate;
    public JDBCMemberInfoRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }

    // MemberInfo 객체와 DB Row를 매핑하기 위한 내부 RowMapper 클래스
    private static class MemberInfoRowMapper implements RowMapper<MemberInfo> {
        @Override
        public MemberInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
            MemberInfo member = new MemberInfo();
            member.setUsernumber(rs.getInt("usernumber"));
            member.setPassword(rs.getString("password"));
            member.setUsername(rs.getString("username"));
            return member;
        }
    }

    @Override
    public void save(MemberInfo member) {
        String sql = "INSERT INTO member_info (usernumber, password, username) VALUES (?,?,?)";
        boolean isDuplicated=false;
        try {
            jdbcTemplate.update(sql, member.getUsernumber(), member.getPassword(), member.getUsername());
        } catch (DuplicateKeyException e) {
            // 중복 아이디 예외를 더 명확한 커스텀 예외로 변환
            throw new DuplicateMemberException("이미 존재하는 아이디입니다.");
        }
    }

    public class DuplicateMemberException extends RuntimeException {
        public DuplicateMemberException(String message) {
            super(message);
        }
    }

    @Override
    public Optional<MemberInfo> findById(int Id) {
        String sql="SELECT usernumber,password,username FROM member_info WHERE usernumber=?";
        try{
            MemberInfo member=jdbcTemplate.queryForObject(sql,new MemberInfoRowMapper(),Id);
            return Optional.ofNullable(member);
        } catch (EmptyResultDataAccessException e){
            return Optional.empty();
        }
    }

    @Override
    public List<MemberInfo> findAll() {
        String sql="SELECT usernumber,password,username FROM member_info";
        return jdbcTemplate.query(sql,new MemberInfoRowMapper());
    }
}
