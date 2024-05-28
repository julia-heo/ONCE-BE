package ewha.lux.once.global.repository;

import ewha.lux.once.domain.card.entity.ConnectedCardCompany;
import ewha.lux.once.domain.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConnectedCardCompanyRepository extends JpaRepository<ConnectedCardCompany, Long> {
    Optional<ConnectedCardCompany> findByUsersAndCardCompany(Users users, String cardcompany);
    List<ConnectedCardCompany> findAllByUsers(Users users);
}
