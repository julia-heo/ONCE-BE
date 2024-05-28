package ewha.lux.once.domain.home.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnnounceListDto {

    private String nickname;
    private long announceCount;
    private List<AnnounceDto> announceTodayList;
    private List<AnnounceDto> announcePastList;

}
