package ewha.lux.once.domain.home.repository;

import ewha.lux.once.domain.home.entity.Card;
import ewha.lux.once.domain.home.entity.CardCompany;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardCompanyRepository extends JpaRepository<CardCompany, Long> {
    CardCompany findByCode(String code);

}
