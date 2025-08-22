package wonbin.scheduler.Repository.Category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wonbin.scheduler.Entity.NoticeCategory.NoticeCategory;

import java.util.Optional;

@Repository
public interface JpaCategoryRepository extends JpaRepository<NoticeCategory,String> {
    Optional<NoticeCategory> findByName(String name);
}
