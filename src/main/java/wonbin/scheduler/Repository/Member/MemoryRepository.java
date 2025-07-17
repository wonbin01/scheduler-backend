package wonbin.scheduler.Repository.Member;

import wonbin.scheduler.Entity.Member.MemberInfo;

import java.util.HashMap;

public class MemoryRepository implements Repository {
    HashMap<Integer, MemberInfo> hm=new HashMap<>();
    @Override
    public void save(MemberInfo member) {
        hm.put(member.getUsernumber(),member);
    }

    @Override
    public MemberInfo findById(int Id) {
        return hm.get(Id);
    }
}
