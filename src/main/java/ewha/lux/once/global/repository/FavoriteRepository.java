package ewha.lux.once.global.repository;

import ewha.lux.once.domain.home.entity.Favorite;
import ewha.lux.once.domain.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Optional<List<Favorite>> findAllByUsers(Users users);
}
