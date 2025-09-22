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
        Map<String,Object> request=new HashMap<>();
        request.put("usernumber",usernumber);
        request.put("year",year);
        request.put("month",month);
        request.put("workLogs",result);
        request.put("holidayList",byYearAndMonth);

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
        String sql = "INSERT INTO salary_info (usernumber, year, month, default_salary, night_salary, weekly_bonus, holiday_bonus, before_tax, after_tax) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                usernumber,
                year,
                month,
                salary.getDefault_salary(),
                salary.getNight_salary(),
                salary.getWeekly_bonus(),
                salary.getHoliday_bonus(),
                salary.getBefore_tax(),
                salary.getAfter_tax());
    }
}
