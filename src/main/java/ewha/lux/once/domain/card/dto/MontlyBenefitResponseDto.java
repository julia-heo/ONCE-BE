package ewha.lux.once.domain.card.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class MontlyBenefitResponseDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class MontlyBenefitProfileDto {
        private int month;
        private int receivedSum;
        private int benefitGoal;
        private int remainBenefit;
        List<MontlyBenefitResponseDto.BenefitListDto> benefitList;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class BenefitListDto {
        private String category;
        private int discountPriceSum;
        private int priceSum;
    }

}
