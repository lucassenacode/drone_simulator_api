package drone_simulator_api.api.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import drone_simulator_api.api.entities.Drone;

@Repository
public interface DroneRepository extends JpaRepository<Drone, Long> {

  Drone findBySerialNumber(String serialNumber);

  List<Drone> findByState(Drone.DroneState state);

  List<Drone> findByStateIn(List<Drone.DroneState> states);
}