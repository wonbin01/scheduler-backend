package wonbin.scheduler.Repository.Member;

import java.util.List;
import java.util.Optional;
import wonbin.scheduler.Entity.Member.MemberInfo;

public interface MemberInfoRepository {
    public void save(MemberInfo member);

    public Optional<MemberInfo> findById(int Id);

    public List<MemberInfo> findAll();
}
