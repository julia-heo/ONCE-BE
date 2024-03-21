package ewha.lux.once.global.repository;

import ewha.lux.once.domain.card.entity.CardCompany;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardCompanyRepository extends JpaRepository<CardCompany, Long> {
    Optional<CardCompany> findByCode(String code);
    List<CardCompany> findByCodeIn(List<String> codes);

}