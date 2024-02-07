package ewha.lux.once.global.repository;

import ewha.lux.once.domain.card.entity.Card;
import ewha.lux.once.domain.card.entity.OwnedCard;
import ewha.lux.once.domain.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OwnedCardRepository extends JpaRepository<OwnedCard, Long> {
    int countAllByUsers( Users users );
    OwnedCard findOwnedCardByCardAndUsers(Card card, Users users);
    OwnedCard findOwnedCardByCardIdAndUsers(Long cardId, Users users);

    List<OwnedCard> findOwnedCardByUsers(Users nowUser);
}
