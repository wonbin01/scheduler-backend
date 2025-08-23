package wonbin.scheduler.Repository.Schedule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import wonbin.scheduler.Entity.Schedule.ScheduleApplyInfo;

import java.util.List;

@Repository
public interface JpaScheduleApplyRepository extends JpaRepository<ScheduleApplyInfo,Long> {
        @Query("SELECT s FROM ScheduleApplyInfo s WHERE YEAR(s.applyDate) = :year AND MONTH(s.applyDate) = :month")
        List<ScheduleApplyInfo> findByApplyDateYearAndMonth(@Param("year") int year, @Param("month") int month);
}
