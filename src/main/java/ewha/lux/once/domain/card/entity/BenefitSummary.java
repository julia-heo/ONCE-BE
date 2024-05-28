package ewha.lux.once.domain.card.entity;

import ewha.lux.once.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="benefit_summary")
@AllArgsConstructor
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@Getter
@Builder
public class BenefitSummary extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "benefit_summary_id")
    private Long id;

    @Column(name = "benefit_field", nullable = false)
    private String benefitField;

    @Lob
    @Column(name = "benefit_contents", nullable = false, columnDefinition = "LONGTEXT")
    private String benefitContents;

    @ManyToOne
    @JoinColumn(name = "cardId")
    private Card card;
}
