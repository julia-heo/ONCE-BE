package ewha.lux.once.domain.home.repository;

import ewha.lux.once.domain.home.entity.Announcement;
import ewha.lux.once.domain.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findAnnouncementByUsers(Users users);
}
