package drone_simulator_api.api.services;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import drone_simulator_api.api.entities.Drone;
import drone_simulator_api.api.entities.Package;
import drone_simulator_api.api.exceptions.ResourceNotFoundException;
import drone_simulator_api.api.repositories.DroneRepository;
import drone_simulator_api.api.repositories.PackageRepository;

@Service
public class DroneService {

  @Autowired
  private DroneRepository droneRepository;

  @Autowired
  private PackageRepository packageRepository;

  public List<Drone> getAllDrones() {
    return droneRepository.findAll();
  }

  public Drone getDroneById(Long droneId) {
    return droneRepository.findById(droneId)
        .orElseThrow(() -> new ResourceNotFoundException("Drone not found with id " + droneId));
  }

  public List<Drone> getDronesByState(Drone.DroneState state) {
    return droneRepository.findByState(state);
  }

  @Transactional
  public Drone createDrone(Drone newDrone) {
    return droneRepository.save(newDrone);
  }

  private double calculateDistance(double x1, double y1, double x2, double y2) {
    return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
  }

  @Transactional
  public void allocatePendingPackages() {

    List<Package> pendingPackages = packageRepository.findByStatus(Package.PackageStatus.PENDING);
    pendingPackages.sort(Comparator.comparing(pkg -> pkg.getPriority().ordinal()));

    for (Package pkg : pendingPackages) {

      List<Drone> availableDrones = droneRepository.findByState(Drone.DroneState.IDLE);

      Optional<Drone> bestDrone = findBestDroneForPackage(pkg, availableDrones);

      if (bestDrone.isPresent()) {
        Drone drone = bestDrone.get();

        pkg.setAllocatedDrone(drone);
        drone.setCurrentWeight(drone.getCurrentWeight() + pkg.getWeight());
        drone.setState(Drone.DroneState.LOADING);

        packageRepository.save(pkg);
        droneRepository.save(drone);
      }
    }
  }

  private Optional<Drone> findBestDroneForPackage(Package pkg, List<Drone> availableDrones) {

    return availableDrones.stream()
        .filter(drone -> canDroneCarryPackage(drone, pkg))
        .filter(drone -> canDroneDeliverPackage(drone, pkg))
        .findFirst();
  }

  public boolean canDroneCarryPackage(Drone drone, Package pkg) {
    return drone.getMaxWeightCapacity() >= drone.getCurrentWeight() + pkg.getWeight();
  }

  public boolean canDroneDeliverPackage(Drone drone, Package pkg) {
    // Assume que a base do drone está em (0,0)
    double distanceToBaseToPackage = calculateDistance(0, 0, pkg.getDestinationX(), pkg.getDestinationY());

    double totalTripDistance = distanceToBaseToPackage * 2;
    return drone.getMaxFlightRange() >= totalTripDistance;
  }

  @Transactional
  public Package allocatePackageToDrone(Long packageId, Long droneId) {
    Package pkg = packageRepository.findById(packageId)
        .orElseThrow(() -> new ResourceNotFoundException("Package not found with id " + packageId));

    Drone drone = droneRepository.findById(droneId)
        .orElseThrow(() -> new ResourceNotFoundException("Drone not found with id " + droneId));

    if (!canDroneCarryPackage(drone, pkg)) {
      throw new IllegalArgumentException("Drone does not have enough capacity for this package.");
    }

    if (!canDroneDeliverPackage(drone, pkg)) {
      throw new IllegalArgumentException("Drone does not have enough flight range for this package.");
    }

    pkg.setAllocatedDrone(drone);
    pkg.setStatus(Package.PackageStatus.IN_TRANSIT);

    drone.setCurrentWeight(drone.getCurrentWeight() + pkg.getWeight());
    drone.setState(Drone.DroneState.LOADING);

    packageRepository.save(pkg);
    droneRepository.save(drone);

    return pkg;
  }

  @Transactional
  public Drone updateDroneState(Long droneId, Drone.DroneState newState) {
    Drone drone = droneRepository.findById(droneId)
        .orElseThrow(() -> new ResourceNotFoundException("Drone not found with id " + droneId));
    drone.setState(newState);
    return droneRepository.save(drone);
  }

}