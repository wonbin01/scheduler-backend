package wonbin.scheduler.Repository.Member;

import org.springframework.stereotype.Repository;
import wonbin.scheduler.Entity.Member.MemberInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class MemoryMemberInfoRepository implements MemberInfoRepository {
    HashMap<Integer, MemberInfo> hm=new HashMap<>();
    @Override
    public void save(MemberInfo member) {
        hm.put(member.getUsernumber(),member);
    }

    @Override
    public Optional<MemberInfo> findById(int Id) {
        return Optional.empty();
    }

    @Override
    public List<MemberInfo> findAll() {
        return new ArrayList<>(hm.values());
    }
}
