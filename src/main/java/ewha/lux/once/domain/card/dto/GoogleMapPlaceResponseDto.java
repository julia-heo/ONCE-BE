package ewha.lux.once.domain.card.dto;

import java.util.List;

import lombok.ToString;

@ToString
public class GoogleMapPlaceResponseDto {

    private List<Place> places;

    public GoogleMapPlaceResponseDto() {
    }

    public GoogleMapPlaceResponseDto(List<Place> places) {
        this.places = places;
    }

    public List<Place> getPlaces() {
        return places;
    }

    public void setPlaces(List<Place> places) {
        this.places = places;
    }
}