package ewha.lux.once.domain.card.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;

@Entity
@Table(name="event_summary")
@AllArgsConstructor
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@Getter
@Builder
public class EventSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_summary_id")
    private Long id;

    @Column(name = "event_field")
    private String eventField;

    @Lob
    @Column(name = "event_contents")
    private String eventContents;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "expire_date")
    private Date expireDate;

    @ManyToOne
    @JoinColumn(name = "cardCompanyId")
    private CardCompany cardCompany;
}
