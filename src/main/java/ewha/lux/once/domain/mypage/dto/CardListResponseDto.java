package ewha.lux.once.domain.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CardListResponseDto {
    private int cardCount;
    private List<CardListDto> cardList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CardListDto {
        private Long ownedCardId;
        private Boolean isMain;
        private String cardCompany;
        private String cardName;
        private Boolean isCreditCard;
        private String cardImg;
    }
}
