package ewha.lux.once.domain.home.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AnnouncListDto {
    private long announceCount;
    private List<AnnounceDto> announceTodayList;
    private List<AnnounceDto> announcePastList;
    public AnnouncListDto(Long announceCount,List<AnnounceDto> announceTodayList,List<AnnounceDto> announcePastList){
        this.announceCount = announceCount;
        this.announceTodayList = announceTodayList;
        this.announcePastList = announcePastList;
    }
}
