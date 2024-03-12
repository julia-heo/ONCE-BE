package ewha.lux.once.domain.card.entity;

import ewha.lux.once.domain.user.entity.Users;
import ewha.lux.once.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="OwnedCard")
@Getter
@Builder
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@AllArgsConstructor
public class OwnedCard extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ownedCardId")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userId")
    private Users users;

    @ManyToOne
    @JoinColumn(name = "cardId")
    private Card card;

    @Column(name = "isMain")
    private boolean isMain;

    @Column(name = "performanceCondition")
    @Setter
    private Integer performanceCondition;

    @Column(name = "currentPerformance")
    @Setter
    private Integer currentPerformance;

    public void releaseMaincard() {
        this.isMain = false;
    }

    public void setMaincard() { this.isMain = true; }
}
