package ewha.lux.once.domain.home.repository;

import ewha.lux.once.domain.home.entity.OwnedCard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OwnedCardRepository extends JpaRepository<OwnedCard, Long> {
}
