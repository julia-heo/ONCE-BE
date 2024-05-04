package ewha.lux.once.domain.home.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatDto {

    private String nickname;
    private Long chatId;
    private String cardName;
    private String cardCompany;
    private String cardImg;
    private String benefit;
    private int discount;
    private Boolean isMain;
}
