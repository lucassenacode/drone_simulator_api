package drone_simulator_api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import drone_simulator_api.entities.Drone;

@Repository
public interface DroneRepository extends JpaRepository<Drone, Long> {

  Drone findBySerialNumber(String serialNumber);
}