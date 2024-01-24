package ewha.lux.once.domain.home.repository;

import ewha.lux.once.domain.home.entity.Card;
import ewha.lux.once.domain.home.entity.CardCompany;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findAllByCardCompany(CardCompany cardCompany);
}
