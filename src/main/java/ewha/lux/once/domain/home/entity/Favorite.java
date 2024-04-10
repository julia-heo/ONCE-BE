package ewha.lux.once.domain.home.entity;

import ewha.lux.once.domain.user.entity.Users;
import ewha.lux.once.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="Favorite")
@Getter
@Setter
@Builder
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@AllArgsConstructor
public class Favorite extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favoriteId")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userId")
    private Users users;

    @Column(name = "name",nullable = false)
    private String name;


}
