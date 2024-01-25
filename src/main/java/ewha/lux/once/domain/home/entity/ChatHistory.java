package ewha.lux.once.domain.home.entity;

import ewha.lux.once.domain.user.entity.Users;
import ewha.lux.once.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name="ChatHistory")
@Getter
@Setter
@Builder
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChatHistory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatHistoryId")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userId")
    private Users users;

    @Column(name = "keyword",nullable = false)
    private String keyword;

    @Column(name = "paymentAmount",nullable = false)
    private int paymentAmount;

    @Column(name = "cardName",nullable = false)
    private String cardName;

    @Column(name = "benefit")
    private String benefit;

    @Column(name = "discount")
    private int discount;

    @Column(name = "hasPaid",nullable = false)
    @ColumnDefault("false")
    private boolean hasPaid;

    @Column(name = "category")
    private String category;

}
