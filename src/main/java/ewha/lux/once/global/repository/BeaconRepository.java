package ewha.lux.once.global.repository;

import ewha.lux.once.domain.home.entity.Beacon;
import org.springframework.data.jpa.repository.JpaRepository;
public interface BeaconRepository extends JpaRepository<Beacon, Long> {
    Beacon findAllByProximityUUIDAndMajorAndMinor(String uuid, int major, int minor);
}