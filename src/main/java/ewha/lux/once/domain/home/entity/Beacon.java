package ewha.lux.once.domain.home.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="Beacon")
@Getter
@Setter
@Builder
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@AllArgsConstructor
public class Beacon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "beaconId")
    private Long id;

    @Column(name = "proximityUUID", nullable = false)
    private String proximityUUID;

    @Column(name = "major")
    private Integer major;

    @Column(name = "minor")
    private Integer minor;

    @Column(name = "name")
    private String name;

    @Column(name = "store")
    private String store;

    @Column(name = "latitude")
    private String latitude;

    @Column(name = "longitude")
    private String longitude;
}