package ewha.lux.once.domain.card.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class MyWalletResponseDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class MyWalletProfileDto {
        private String nickname;
        List<OwnedCardListDto> ownedCardList;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class OwnedCardListDto {
        private Long ownedCardId;
        private String cardName;
        private String cardCompany;
        private int cardType;
        private String cardImg;
        private boolean isMaincard;
        private int performanceCondition;
        private int currentPerformance;
        private int remainPerformance;
        List<CardBenefitListDto> cardBenefitList;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CardBenefitListDto {
        private String category;
        private String benefit;
    }
}
