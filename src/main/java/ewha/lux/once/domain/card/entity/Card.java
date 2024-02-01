package ewha.lux.once.domain.card.entity;

import ewha.lux.once.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="Card")
@Getter
@Builder
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@AllArgsConstructor
public class Card extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cardId")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cardCompanyId")
    private CardCompany cardCompany;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "imgUrl", nullable = false)
    private String imgUrl;

    @Lob
    @Column(name = "benefits")
    private String benefits;

    @Column(name = "benefitSummary")
    private String benefitSummary;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private CardType type;

}
