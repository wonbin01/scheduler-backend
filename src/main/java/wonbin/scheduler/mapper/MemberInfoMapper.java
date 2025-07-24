package wonbin.scheduler.mapper;

import org.apache.ibatis.annotations.Mapper;
import wonbin.scheduler.Entity.Member.MemberInfo;

@Mapper
public interface MemberInfoMapper {
    // MemberInfo 객체를 받아 데이터베이스에 삽입합니다.
    // 삽입 후 자동 생성된 usernumber가 객체에 다시 주입됩니다.
    // 메서드 이름은 insertMemberInfo로 변경 (일관성 위해)
    int insertMemberInfo(MemberInfo memberInfo);

    // 테스트를 위해 추가로 ID로 조회하는 메서드
    MemberInfo findByUserNumber(int usernumber);
}
