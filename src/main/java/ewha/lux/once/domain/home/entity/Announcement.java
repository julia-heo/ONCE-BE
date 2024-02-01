package ewha.lux.once.domain.home.entity;

import ewha.lux.once.domain.user.entity.Users;
import ewha.lux.once.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name="Announcement")
@Getter
@Setter
@Builder
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@AllArgsConstructor
public class Announcement extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "announcementId")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userId")
    private Users users;

    @Column(name = "type",nullable = false)
    private int type;

    @Column(name = "content",nullable = false)
    private String content;

    @Column(name = "moreInfo")
    private String moreInfo;

    @Column(name = "hasCheck",nullable = false)
    @ColumnDefault("false")
    private boolean hasCheck;
}
