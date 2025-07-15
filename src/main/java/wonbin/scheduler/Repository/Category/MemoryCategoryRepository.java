package wonbin.scheduler.Repository.Category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MemoryCategoryRepository implements CategoryRepository{
    HashMap<String, List<String>> hm=new HashMap<>();

    public MemoryCategoryRepository() {
        ArrayList<String> al = new ArrayList<>();
        al.add("매점"); al.add("웰컴"); al.add("엔젤"); al.add("인사");
        al.add("소방"); al.add("기타");
        hm.put("categories", al); // 이것만 있어도 충분해
    }

    @Override
    public void save_category(String newcategory) {
        hm.get("categories").add(newcategory);
    }

    @Override
    public List<String> find_all_category() {
        return hm.get("categories");
    }
}
