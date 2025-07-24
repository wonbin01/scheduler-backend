package wonbin.scheduler.Repository.Category;

import wonbin.scheduler.Entity.NoticeCategory.NoticeCategory;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    public void save_category(String newcategory);

    public List<String> find_all_category();

    public Optional<NoticeCategory> find_by_name(String name);
}
