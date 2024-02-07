package ewha.lux.once.domain.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MypageResponseDto {

    private String nickname;
    private String userProfileImg;
    private int ownedCardCount;
    private int month;
    private int receivedBenefit;
    private int benefitGoal;

}
