package ewha.lux.once.domain.home.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnnounceFavoriteRequestDto {
    private String store;
    private String storeName;
    private double latitude;
    private double longitude;
}