package ewha.lux.once.global.repository;

import ewha.lux.once.domain.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    boolean existsByLoginId(String loginId);
    Optional<Users> findByLoginId(String loginId);
}
