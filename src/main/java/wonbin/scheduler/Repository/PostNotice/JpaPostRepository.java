package wonbin.scheduler.Repository.PostNotice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wonbin.scheduler.Entity.Post.PostInfo;

import java.util.List;

@Repository
public interface JpaPostRepository extends JpaRepository<PostInfo,Long> {
    List<PostInfo> findByCategoryNameOrderByIdDesc(String categoryName);
}
