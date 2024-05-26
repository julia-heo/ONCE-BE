package ewha.lux.once.global.repository;

import ewha.lux.once.domain.card.entity.Card;
import ewha.lux.once.domain.card.entity.OwnedCard;
import ewha.lux.once.domain.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OwnedCardRepository extends JpaRepository<OwnedCard, Long> {
    int countAllByUsers(Users users);

    OwnedCard findOwnedCardByCardAndUsers(Card card, Users users);

    OwnedCard findOwnedCardByCardIdAndUsers(Long ownedCardId, Users users);

    List<OwnedCard> findOwnedCardByUsers(Users nowUser);

    OwnedCard findOwnedCardByIdAndUsers(Long ownedCardId, Users nowUser);

    List<OwnedCard> findOwnedCardByIsMain(boolean isMain);

    Optional<OwnedCard> findOwnedCardByUsersAndCard(Users users, Card card);

    List<OwnedCard> findOwnedCardByUsersAndIsMain(Users nowUser, boolean isMain);
}
