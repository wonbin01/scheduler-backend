package wonbin.scheduler.Repository.Category;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import wonbin.scheduler.Entity.NoticeCategory.NoticeCategory;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@Transactional
public class JdbcCategoryTest {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Test
    void saveCategory(){
        String name="213123123";
        categoryRepository.save_category(name);
        Optional<NoticeCategory> foundCategory=categoryRepository.find_by_name(name);
        Assertions.assertTrue(foundCategory.isPresent());

        Assertions.assertEquals(name,foundCategory.get().getName());
    }
    @Test
    void findAllCategory(){
        categoryRepository.save_category("원빈1");
        categoryRepository.save_category("원빈2");
        categoryRepository.save_category("원빈3");

        List<String> allList=categoryRepository.find_all_category();
        Assertions.assertTrue(allList.contains("원빈1"));
        Assertions.assertTrue(allList.contains("원빈2"));
        Assertions.assertTrue(allList.contains("원빈3"));
    }

}
