package ewha.lux.once.global.repository;

import ewha.lux.once.domain.home.entity.ChatHistory;
import ewha.lux.once.domain.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long> {
    List<ChatHistory> findByUsers(Users users);
}
