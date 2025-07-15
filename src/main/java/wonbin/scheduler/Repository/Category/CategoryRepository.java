package wonbin.scheduler.Repository.Category;

import java.util.List;

public interface CategoryRepository {
    public void save_category(String newcategory);

    public List<String> find_all_category();
}
