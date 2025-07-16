package wonbin.scheduler.Repository.Category;

import wonbin.scheduler.Entity.NoticeCategory.NoticeCategory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryCategoryRepository implements CategoryRepository{
    private final Map<String, NoticeCategory> hm=new HashMap<>();

    public MemoryCategoryRepository() {
        for(String name : List.of("매점","웰컴","엔젤","인사","소방","기타")){
            hm.put(name,new NoticeCategory(name));
        }
    }

    @Override
    public void save_category(String newcategory){
        if(!hm.containsKey(newcategory)){
            hm.put(newcategory,new NoticeCategory(newcategory));
        }
    }

    @Override
    public List<String> find_all_category() {
        return new ArrayList<>(hm.keySet());
    }
}
