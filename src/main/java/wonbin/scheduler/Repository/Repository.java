package wonbin.scheduler.Repository;

import wonbin.scheduler.Entity.member.MemberInfo;

@org.springframework.stereotype.Repository
public interface Repository {
    public void save(MemberInfo member);
    public MemberInfo findById(int Id);
}
