package wonbin.scheduler.Repository.salary;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import wonbin.scheduler.Entity.Salary.SalaryInfo;

@Repository
@RequiredArgsConstructor
public class JdbcSalaryRepository implements SalaryRepository {
    private final JdbcTemplate jdbcTemplate;
    private final RestTemplate restTemplate;

    @Override
    public int getNextMonthWeeklyBonus(int usernumber, int year, int month) {
        int prevmonth = month - 1;
        int prevyear = year;
        if (prevmonth == 0) {
            prevmonth = 12;
            prevyear = year - 1;
        }
        String sql = "select next_month_weekly_bonus from salary_info where usernumber=? and year=? and month=?";
        try {
            Integer bonus = jdbcTemplate.queryForObject(sql, Integer.class, usernumber, prevyear, prevmonth);
            return bonus != null ? bonus : 0;
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return 0; // 조회 결과가 없으면 0 반환
        }
    }

    @Override
    public void saveSalary(SalaryInfo salary, int usernumber, int year, int month) {
        String sql = """
                    INSERT INTO salary_info
                    (usernumber, year, month, default_salary, night_salary, weekly_bonus, holiday_bonus, before_tax, after_tax,next_month_weekly_bonus)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?,?)
                    ON DUPLICATE KEY UPDATE
                        default_salary = VALUES(default_salary),
                        night_salary = VALUES(night_salary),
                        weekly_bonus = VALUES(weekly_bonus),
                        holiday_bonus = VALUES(holiday_bonus),
                        before_tax = VALUES(before_tax),
                        after_tax = VALUES(after_tax),
                        next_month_weekly_bonus=VALUES(next_month_weekly_bonus)
                """;
        jdbcTemplate.update(sql,
                usernumber,
                year,
                month,
                salary.getDefault_salary(),
                salary.getNight_salary(),
                salary.getWeekly_bonus(),
                salary.getHoliday_bonus(),
                salary.getBefore_tax(),
                salary.getAfter_tax(),
                salary.getNext_month_weekly_bonus());
    }
}
