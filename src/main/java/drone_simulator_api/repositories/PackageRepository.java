package drone_simulator_api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import drone_simulator_api.entities.Package;

@Repository
public interface PackageRepository extends JpaRepository<Package, Long> {

}