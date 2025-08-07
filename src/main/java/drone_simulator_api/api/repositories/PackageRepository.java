package drone_simulator_api.api.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import drone_simulator_api.api.entities.Drone;
import drone_simulator_api.api.entities.Package;

@Repository
public interface PackageRepository extends JpaRepository<Package, Long> {
  List<Package> findByStatus(Package.PackageStatus status);

  List<Package> findByAllocatedDroneAndStatus(Drone allocatedDrone, Package.PackageStatus status);
}