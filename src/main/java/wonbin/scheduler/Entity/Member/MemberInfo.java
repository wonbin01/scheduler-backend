package wonbin.scheduler.Entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;


@Data
@Table(name="member_info")
@Entity
public class MemberInfo {
    @Id
    @Column(unique = true,nullable = false)
    private int usernumber; //사번
    private String password;
    private String username;
}
