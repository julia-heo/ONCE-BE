package ewha.lux.once.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EditUserInfoRequestDto {
    private String username;
    private String nickname;
    @JsonFormat(pattern = "yyyy.MM.dd")
    private Date birthday;
    private String userPhoneNum;
}
