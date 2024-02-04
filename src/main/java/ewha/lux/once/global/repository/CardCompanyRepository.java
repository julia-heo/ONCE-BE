package ewha.lux.once.global.repository;

import ewha.lux.once.domain.card.entity.CardCompany;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardCompanyRepository extends JpaRepository<CardCompany, Long> {
    CardCompany findByCode(String code);

}
