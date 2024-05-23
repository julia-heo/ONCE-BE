package ewha.lux.once.domain.home.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BeaconRequestDto {
    private String proximityUUID;
    private Integer major;
    private Integer minor;
}