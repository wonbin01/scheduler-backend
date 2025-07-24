package wonbin.scheduler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wonbin.scheduler.Entity.Member.MemberInfo;
import wonbin.scheduler.mapper.MemberInfoMapper;

@SpringBootTest
@Transactional
public class MySqlInitialTest {
    @Autowired
    private MemberInfoMapper memberInfoMapper;

    @Test
    void testInsertMemberInfo(){
        MemberInfo newMember=new MemberInfo();
        newMember.setPassword("test");
        newMember.setUsername("tester");
        int uniqueUserNumber = (int) (System.currentTimeMillis() % 1000000); // 간단한 예시, 실제 서비스에서는 더 복잡하게 고유성을 보장해야 함
        newMember.setUsernumber(uniqueUserNumber); // 고유한 값 설정

        int insertedRows=memberInfoMapper.insertMemberInfo(newMember);

        Assertions.assertEquals(1,insertedRows,"한개 삽입");
        MemberInfo foundMember=memberInfoMapper.findByUserNumber(newMember.getUsernumber());

        Assertions.assertEquals(uniqueUserNumber,foundMember.getUsernumber() ,"비밀번호가 일치해야됩니다.");
    }
}
