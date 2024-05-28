package ewha.lux.once.global.repository;

import ewha.lux.once.domain.card.entity.BenefitSummary;
import ewha.lux.once.domain.card.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BenefitSummaryRepository extends JpaRepository<BenefitSummary, Long> {
    List<BenefitSummary> findByCard(Card card);
}
