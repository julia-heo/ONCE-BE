package ewha.lux.once.global.common;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

// 생성시간,수정시간 자동 업데이트
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {
    @CreatedDate
    @Column(name = "createdAt",updatable = false, nullable = false)
    private LocalDateTime created_at;

    @LastModifiedDate
    @Column(name = "updatedAt")
    private LocalDateTime updated_at;
}