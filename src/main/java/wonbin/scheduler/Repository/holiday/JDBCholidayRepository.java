package wonbin.scheduler.Repository.holiday;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import wonbin.scheduler.Entity.holiday.holidayInfo;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JDBCholidayRepository implements holidayRepository{
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<holidayInfo> findByYearAndMonth(int year, int month) {
        String sql = "SELECT * FROM holiday WHERE YEAR(holidate) = ? AND MONTH(holidate) = ?";

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new holidayInfo(
                        rs.getDate("holidate").toLocalDate(),
                        rs.getString("name")
                ),
                year,
                month
        );
    }


    @Override
    public void save(LocalDate date, String name) {
        String sql = "INSERT IGNORE INTO holiday (holidate, name) VALUES (?, ?)";
        jdbcTemplate.update(sql, date, name);
    }
}
