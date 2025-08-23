package wonbin.scheduler.Repository.Schedule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import wonbin.scheduler.Entity.Schedule.ScheduleViewInfo;

import java.util.List;

@Repository
public interface JpaScheduleViewRepository extends JpaRepository<ScheduleViewInfo, Long> {
    @Query("SELECT s FROM ScheduleViewInfo s WHERE YEAR(s.applyDate) = :year AND MONTH(s.applyDate) = :month")
    List<ScheduleViewInfo> findByApplyDateYearAndMonth(@Param("year") int year, @Param("month") int month);
}
