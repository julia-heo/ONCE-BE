package ewha.lux.once.global.repository;

import ewha.lux.once.domain.home.entity.FCMToken;
import ewha.lux.once.domain.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FCMTokenRepository extends JpaRepository<FCMToken, Long> {
    List<FCMToken> findAllByUsers(Users users);
}
