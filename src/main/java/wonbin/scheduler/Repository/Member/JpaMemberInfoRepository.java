package wonbin.scheduler.Repository.Member;

import org.springframework.data.jpa.repository.JpaRepository;
import wonbin.scheduler.Entity.Member.MemberInfo;

public interface JpaMemberInfoRepository extends JpaRepository<MemberInfo,Integer> {
}
