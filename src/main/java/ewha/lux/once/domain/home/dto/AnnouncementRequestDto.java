package ewha.lux.once.domain.home.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AnnouncementRequestDto {
    private String targetToken;
    private String title;
    private String body;
}