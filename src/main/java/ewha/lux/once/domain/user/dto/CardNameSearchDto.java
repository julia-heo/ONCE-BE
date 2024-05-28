package ewha.lux.once.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CardNameSearchDto {
    private Long cardId;
    private String cardName;
    private String cardImg;
    private String companyName;
    private String type;

}
