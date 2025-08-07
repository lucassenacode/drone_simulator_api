package drone_simulator_api.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import drone_simulator_api.api.entities.Drone;
import drone_simulator_api.api.entities.Package;
import drone_simulator_api.api.repositories.DroneRepository;
import drone_simulator_api.api.repositories.PackageRepository;

@Service
public class SimulationService {

  @Autowired
  private DroneRepository droneRepository;

  @Autowired
  private PackageRepository packageRepository;

  @Scheduled(fixedRate = 5000) // Executa a cada 5 segundos
  @Transactional
  public void runDeliverySimulation() {

    List<Drone> activeDrones = droneRepository.findByStateIn(List.of(
        Drone.DroneState.LOADING,
        Drone.DroneState.IN_FLIGHT,
        Drone.DroneState.DELIVERING,
        Drone.DroneState.RETURNING));

    for (Drone drone : activeDrones) {
      switch (drone.getState()) {
        case LOADING:
          // Simular tempo de carregamento antes de voar
          drone.setState(Drone.DroneState.IN_FLIGHT);
          droneRepository.save(drone);
          break;

        case IN_FLIGHT:
          // Encontra os pacotes que estão alocados a este drone e em trânsito
          List<Package> packagesInTransit = packageRepository.findByAllocatedDroneAndStatus(
              drone, Package.PackageStatus.IN_TRANSIT);

          if (!packagesInTransit.isEmpty()) {

            drone.setState(Drone.DroneState.DELIVERING);
            droneRepository.save(drone);
          }
          break;
        case DELIVERING:

          List<Package> packagesToDeliver = packageRepository.findByAllocatedDroneAndStatus(
              drone, Package.PackageStatus.IN_TRANSIT);

          for (Package pkg : packagesToDeliver) {
            pkg.setStatus(Package.PackageStatus.DELIVERED);
            packageRepository.save(pkg);
          }

          // Drone agora pode retornar para a base
          drone.setState(Drone.DroneState.RETURNING);
          drone.setCurrentWeight(0.0); // O peso é zerado após a entrega
          droneRepository.save(drone);
          break;

        case RETURNING:
          drone.setLocationX(0.0); // Retorna para a base
          drone.setLocationY(0.0);
          drone.setState(Drone.DroneState.IDLE);
          droneRepository.save(drone);
          break;

        default:
          break;
      }
    }
  }
}
