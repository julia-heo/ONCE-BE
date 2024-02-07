package ewha.lux.once.domain.home.dto;

import ewha.lux.once.domain.home.entity.Announcement;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;
@Getter
@Setter
public class AnnounceDetailDto {
    private int type;
    private String content;
    private String moreInfo;
    private String announceDate;

    public AnnounceDetailDto(Announcement announce){
        this.content = announce.getContent();
        this.moreInfo = announce.getMoreInfo();
        this.type = announce.getType();
        this.announceDate = announce.getCreatedAt().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss"));
    }

}
