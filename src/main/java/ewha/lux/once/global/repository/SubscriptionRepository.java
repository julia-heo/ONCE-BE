package ewha.lux.once.global.repository;

import ewha.lux.once.domain.home.entity.Subscription;
import ewha.lux.once.domain.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findAllByUsers(Users users);
}
