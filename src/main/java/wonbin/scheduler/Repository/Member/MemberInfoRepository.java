package wonbin.scheduler.Repository.Member;

import org.springframework.stereotype.Repository;
import wonbin.scheduler.Entity.Member.MemberInfo;

import java.util.List;

@Repository
public interface MemberInfoRepository {
    public void save(MemberInfo member);
    public MemberInfo findById(int Id);
    public List<MemberInfo> findAll();
}
