package ewha.lux.once.domain.user.service;

import ewha.lux.once.domain.user.dto.SignInRequestDto;
import ewha.lux.once.domain.user.dto.SignupRequestDto;
import ewha.lux.once.domain.user.dto.UserEditResponseDto;
import ewha.lux.once.domain.user.entity.Users;
import ewha.lux.once.domain.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    public Users signup(SignupRequestDto request) throws ParseException {
        String loginId = request.getLoginId();
        String username =  request.getUsername();
        String password = request.getPassword();
        String nickname = request.getNickname();
        String phone = request.getUserPhoneNum();
        String birth = request.getBirthday();

        if (usersRepository.existsByLoginId(loginId)) {
            throw new RuntimeException("이미 존재하는 ID 입니다 -> " + loginId);
        }

        password = passwordEncoder.encode(password);
        Timestamp now = new Timestamp(System.currentTimeMillis());

        Users.UsersBuilder usersBuilder = Users.builder()
                .loginId(loginId)
                .username(username)
                .nickname(nickname)
                .password(password)
                .lastLogin(now);

        // phone 값이 존재하는 경우에만 설정
        if (StringUtils.hasText(phone)) {
            usersBuilder.phone(phone);
        }

        // birthday 값이 존재하는 경우에만 설정
        if (StringUtils.hasText(birth)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
            Date birthday = dateFormat.parse(birth);
            usersBuilder.birthday(birthday);
        }

        return usersRepository.save(usersBuilder.benefitGoal(100000).build());
    }

    public Users authenticate(SignInRequestDto request) {
        String loginId = request.getLoginId();
        String password = request.getPassword();

        Users users = usersRepository.findByLoginId(loginId);

        if (!passwordEncoder.matches(password, users.getPassword())){
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        users.setLastLogin();
        usersRepository.save(users);
        return users;
    }

    public UserEditResponseDto getUserEdit(Users nowUser){
        return UserEditResponseDto.fromEntity(nowUser);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users users = usersRepository.findByLoginId(username);

        return new User(users.getLoginId(), users.getPassword(),
                users.getAuthorities());
    }
}
