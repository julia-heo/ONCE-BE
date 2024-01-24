package ewha.lux.once.global.common;

import ewha.lux.once.domain.user.entity.Users;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

@Getter
public class UserAccount extends User {
    private Users users;
    public UserAccount(Users users) {
        super(users.getLoginId(), users.getPassword(), users.getAuthorities());
        this.users = users;
    }
}