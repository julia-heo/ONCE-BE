package ewha.lux.once.global.repository;

import ewha.lux.once.domain.home.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long> {
    List<Store> findByNameIn(List<String> names);
}