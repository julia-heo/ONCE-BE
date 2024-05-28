package ewha.lux.once.domain.card.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TestSummaryIndexDto {
    private String prompt;
    private String model_name;
    private long start_index;
    private long end_index;
}
