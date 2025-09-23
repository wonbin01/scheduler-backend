package wonbin.scheduler.Controller.Salary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import wonbin.scheduler.Entity.Salary.SalaryDto;
import wonbin.scheduler.Entity.Salary.SalaryInfo;
import wonbin.scheduler.Entity.Schedule.ScheduleViewInfo;
import wonbin.scheduler.Entity.holiday.holidayInfo;
import wonbin.scheduler.Repository.Schedule.ScheduleViewRepository;
import wonbin.scheduler.Repository.holiday.holidayRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class SalaryController {

    @Autowired
    ScheduleViewRepository scheduleViewRepository;
    @Autowired
    holidayRepository holidayRepository;
    @Autowired
    private RestTemplate restTemplate;  // RestTemplate 주입
    @Autowired
    JdbcTemplate jdbcTemplate;

    @PostMapping("/api/salary")
    public SalaryInfo calculateSalary(@RequestBody SalaryDto req){
        int usernumber = req.getUsernumber();
        int year=req.getYear();
        int month = req.getMonth();

        List<ScheduleViewInfo> result = scheduleViewRepository.findByUsernumberYearMonth(usernumber, year, month);
        List<holidayInfo> byYearAndMonth = holidayRepository.findByYearAndMonth(year, month);
        int bonus = getNextMonthWeeklyBonus(usernumber, year, month);
        Map<String,Object> request=new HashMap<>();
        request.put("usernumber",usernumber);
        request.put("year",year);
        request.put("month",month);
        request.put("workLogs",result);
        request.put("holidayList",byYearAndMonth);
        request.put("prev_weekly_bonus",bonus);

//        //테스트용
//        System.out.println(usernumber);
//        System.out.println(year);
//        System.out.println(month);
//        System.out.println(result);
//        System.out.println(byYearAndMonth);

        String url="http://calculator:8000/calculate";
        SalaryInfo response=restTemplate.postForObject(url,request, SalaryInfo.class);

        saveSalary(response,usernumber,year,month);
        return response;

    }

    private void saveSalary(SalaryInfo salary,int usernumber,int year,int month){
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

    private int getNextMonthWeeklyBonus(int usernumber, int year, int month) {
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

}
