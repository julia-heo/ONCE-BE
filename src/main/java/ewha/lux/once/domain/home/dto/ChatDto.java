package ewha.lux.once.domain.home.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatDto {
    private String nickname;
    private int ownedCardCount;
    private Long chatId;
    private String cardName;
    private String cardImg;
    private String benefit;
    private int discount;

    public ChatDto(String nickname, int ownedCardCount, Long chatId, String cardName, String cardImg, String benefit, int discount){
        this.nickname = nickname;
        this.ownedCardCount = ownedCardCount;
        this.chatId = chatId;
        this.cardName = cardName;
        this.cardImg = cardImg;
        this.benefit = benefit;
        this.discount = discount;
    }
}
