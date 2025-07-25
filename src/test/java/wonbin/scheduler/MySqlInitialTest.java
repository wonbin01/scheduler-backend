package wonbin.scheduler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wonbin.scheduler.Entity.Member.MemberInfo;
import wonbin.scheduler.Repository.Member.MemberInfoRepository;

import java.util.Optional;

@SpringBootTest
@Transactional
public class MySqlInitialTest {
    @Autowired
    private MemberInfoRepository memberInfoRepository;

    @Test
    void testInsertMemberInfo(){
        MemberInfo newMember=new MemberInfo();
        newMember.setPassword("test");
        newMember.setUsername("tester");
        int uniqueUserNumber = (int) (System.currentTimeMillis() % 1000000); // 간단한 예시, 실제 서비스에서는 더 복잡하게 고유성을 보장해야 함
        newMember.setUsernumber(uniqueUserNumber); // 고유한 값 설정

        memberInfoRepository.save(newMember);
        Optional<MemberInfo> foundMemberOptional=memberInfoRepository.findById(newMember.getUsernumber());

        Assertions.assertTrue(foundMemberOptional.isPresent());

        MemberInfo foundMember=foundMemberOptional.get();

        Assertions.assertEquals(newMember.getUsernumber(), foundMember.getUsernumber(), "회원 번호가 일치하지 않습니다.");
        Assertions.assertEquals(newMember.getPassword(), foundMember.getPassword(), "비밀번호가 일치하지 않습니다.");
        Assertions.assertEquals(newMember.getUsername(), foundMember.getUsername(), "사용자 이름이 일치하지 않습니다.");
    }
}
