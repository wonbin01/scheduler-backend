package wonbin.scheduler.Repository.Member;

import wonbin.scheduler.Entity.Member.MemberInfo;

@org.springframework.stereotype.Repository
public interface Repository {
    public void save(MemberInfo member);
    public MemberInfo findById(int Id);
}
