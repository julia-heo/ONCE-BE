package ewha.lux.once.domain.card.entity;

import ewha.lux.once.domain.user.entity.Users;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="ConnectedCardCompany")
@Getter
@Setter
@Builder
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@AllArgsConstructor
public class ConnectedCardCompany {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "connectedCardCompanyId")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userId")
    private Users users;

    @Column(name = "cardCompany", nullable = false)
    private String cardCompany;

    @Column(name = "name", nullable = false)
    private LocalDateTime connectedAt;

}
