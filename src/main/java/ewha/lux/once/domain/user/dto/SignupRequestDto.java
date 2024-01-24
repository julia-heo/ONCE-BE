package ewha.lux.once.domain.user.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDto {
    @NotBlank
    private String loginId;
    @NotBlank
    private String username;
    @NotBlank
    private String nickname;
    @NotBlank
    private String password;

    private String userPhoneNum;
    private String birthday;

}
