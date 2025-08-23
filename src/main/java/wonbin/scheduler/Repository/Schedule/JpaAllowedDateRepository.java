package wonbin.scheduler.Repository.Schedule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wonbin.scheduler.Entity.Schedule.AllowedDate;

import java.time.LocalDate;
import java.util.Optional;


@Repository
public interface JpaAllowedDateRepository extends JpaRepository<AllowedDate,Long> {
    long deleteByDate(String date);
    Optional<AllowedDate> findByDate(String date);
}
