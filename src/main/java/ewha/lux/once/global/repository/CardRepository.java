package ewha.lux.once.global.repository;

import ewha.lux.once.domain.card.entity.Card;
import ewha.lux.once.domain.card.entity.CardCompany;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findAllByCardCompany(CardCompany cardCompany);
    Optional<Card> findByName(String name);
    List<Card> findAllByNameContains(String name);
}
