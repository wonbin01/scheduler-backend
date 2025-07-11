package wonbin.scheduler.Repository;

import wonbin.scheduler.member.MemberInfo;

import java.util.HashMap;

public class MemoryRepository implements Repository {
    HashMap<Integer, MemberInfo> hm=new HashMap<>();
    @Override
    public void save(MemberInfo member) {
        hm.put(member.getUsername(),member);
    }

    @Override
    public MemberInfo findById(int Id) {
        return hm.get(Id);
    }
}
