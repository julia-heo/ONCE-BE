package ewha.lux.once.domain.home.entity;

import ewha.lux.once.domain.user.entity.Users;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="subscriptions")
public class FCMToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String token;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users users;

}
