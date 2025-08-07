package drone_simulator_api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import drone_simulator_api.entities.Package;
import drone_simulator_api.entities.Package.PackageStatus;
import drone_simulator_api.exceptions.ResourceNotFoundException;
import drone_simulator_api.repositories.PackageRepository;

@Service
public class PackageService {

  @Autowired
  private PackageRepository packageRepository;

  @Autowired
  private DroneService droneService;

  @Transactional
  public Package createPackage(Package newPackage) {
    newPackage.setStatus(PackageStatus.PENDING);
    return packageRepository.save(newPackage);
  }

  public List<Package> getPendingPackages() {
    return packageRepository.findByStatus(PackageStatus.PENDING);
  }

  @Transactional
  public Package updatePackageStatus(Long packageId, PackageStatus newStatus) {
    Package pkg = packageRepository.findById(packageId)
        .orElseThrow(() -> new ResourceNotFoundException("Package not found with id " + packageId));

    pkg.setStatus(newStatus);
    return packageRepository.save(pkg);
  }

  @Transactional
  public Package markPackageAsDelivered(Long packageId) {
    return updatePackageStatus(packageId, PackageStatus.DELIVERED);
  }

  @Transactional
  public Package allocatePackageToDrone(Long packageId, Long droneId) {

    return droneService.allocatePackageToDrone(packageId, droneId);
  }

  public PackageStatus getPackageStatus(Long packageId) {
    Package pkg = packageRepository.findById(packageId)
        .orElseThrow(() -> new ResourceNotFoundException("Package not found with id " + packageId));
    return pkg.getStatus();
  }
}
