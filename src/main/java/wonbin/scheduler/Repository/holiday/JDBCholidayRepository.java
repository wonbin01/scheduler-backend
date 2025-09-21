package wonbin.scheduler.Repository.holiday;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
@RequiredArgsConstructor
public class JDBCholidayRepository implements holidayRepository{
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void save(LocalDate date, String name) {
        String sql = "INSERT IGNORE INTO holiday (holidate, name) VALUES (?, ?)";
        jdbcTemplate.update(sql, date, name);
    }
}
