package ewha.lux.once.domain.card.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CardType {
    DebitCard("체크카드"),
    CreditCard("신용카드");

    private String type;
    CardType(String type){
        this.type = type;
    }

}
