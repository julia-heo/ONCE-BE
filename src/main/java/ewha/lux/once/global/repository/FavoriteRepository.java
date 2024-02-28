package ewha.lux.once.global.repository;

import ewha.lux.once.domain.home.entity.Favorite;
import ewha.lux.once.domain.home.entity.Store;
import ewha.lux.once.domain.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    boolean existsByStoreAndUsers(Store store, Users users);
}
