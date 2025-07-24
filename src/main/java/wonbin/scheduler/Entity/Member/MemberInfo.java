package wonbin.scheduler.Entity.Member;

import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("member_info")
public class MemberInfo {
    private int usernumber; //사번
    private String password;
    private String username;
}
