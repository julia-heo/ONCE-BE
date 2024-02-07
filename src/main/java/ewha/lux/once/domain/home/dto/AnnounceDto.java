package ewha.lux.once.domain.home.dto;

import ewha.lux.once.domain.home.entity.Announcement;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class AnnounceDto {
    private long announceId;
    private String content;
    private int type;
    private boolean hasCheck;
    private String announceDate;

    public AnnounceDto(Announcement announce){
        this.announceId = announce.getId();
        this.content = announce.getContent();
        this.type = announce.getType();
        this.hasCheck = announce.isHasCheck();
        this.announceDate = announce.getCreatedAt().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss"));
    }

}
