package wonbin.scheduler.Repository;

import wonbin.scheduler.member.MemberInfo;

@org.springframework.stereotype.Repository
public interface Repository {
    public void save(MemberInfo member);
    public MemberInfo findById(int Id);
}
