package ewha.lux.once.domain.card.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@AllArgsConstructor
public class Place {
    private String formattedAddress;
    private Location location;


    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    public static class Location{
        private double latitude;
        private double longitude;

    }
}