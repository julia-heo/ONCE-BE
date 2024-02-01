package ewha.lux.once.domain.user.entity;

import ewha.lux.once.global.common.BaseEntity;
import jakarta.persistence.*;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name="User")
@Getter
@Builder
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@AllArgsConstructor
public class Users extends BaseEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId")
    private Long id;

    @Column(name = "loginId", nullable = false, unique = true)
    private String loginId;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "phone")
    private String phone;

    @Column(name = "profileImg")
    private String profileImg;

    @Temporal(TemporalType.DATE)
    @Column(name = "birthday")
    private Date birthday;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "lastLogin")
    private Timestamp lastLogin;

    @Column(name = "benefitGoal")
    private int benefitGoal;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("USER")); // 사용자에게 기본적인 권한을 부여
        return authorities;
    }

    @Override // 계정 만료 여부 반환
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override // 계정 잠금여부 반환
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override // 패스워 만료 여부 반환
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override // 게정 사용 가능 여부 반환
    public boolean isEnabled() {
        return true;
    }

    @PreUpdate
    public void setLastLogin() {
        this.lastLogin = new Timestamp(System.currentTimeMillis());
    }

    public void update (String userPhoneNum){
        this.username = username;
        this.nickname = nickname;
        this.birthday = birthday;
        this.phone = phone;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

}
