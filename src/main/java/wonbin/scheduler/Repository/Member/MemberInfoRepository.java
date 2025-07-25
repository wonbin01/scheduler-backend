package wonbin.scheduler.Repository.Member;

import org.springframework.stereotype.Repository;
import wonbin.scheduler.Entity.Member.MemberInfo;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberInfoRepository {
    public void save(MemberInfo member);
    public Optional<MemberInfo> findById(int Id);
    public List<MemberInfo> findAll();
}
