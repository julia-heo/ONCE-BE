package ewha.lux.once.domain.home.entity;

import ewha.lux.once.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="Store")
@Getter
@Setter
@Builder
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@AllArgsConstructor
public class Store extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "storeId")
    private Long id;

    @Column(name = "name",nullable = false)
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "x")
    private double x;

    @Column(name = "y")
    private double y;

}
