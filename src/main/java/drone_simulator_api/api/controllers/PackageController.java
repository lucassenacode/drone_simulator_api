package drone_simulator_api.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import drone_simulator_api.api.entities.Package;
import drone_simulator_api.api.entities.Package.PackageStatus;
import drone_simulator_api.api.exceptions.ResourceNotFoundException;
import drone_simulator_api.api.services.PackageService;

@RestController
@RequestMapping("/api/packages")
public class PackageController {

  @Autowired
  private PackageService packageService;

  @PostMapping
  public ResponseEntity<Package> createPackage(@RequestBody Package newPackage) {
    try {
      Package createdPackage = packageService.createPackage(newPackage);
      return new ResponseEntity<>(createdPackage, HttpStatus.CREATED);
    } catch (IllegalArgumentException ex) {

      return ResponseEntity.badRequest().body(null);
    }
  }

  @GetMapping("/{packageId}/status")
  public ResponseEntity<PackageStatus> getPackageStatus(@PathVariable Long packageId) {
    try {
      PackageStatus status = packageService.getPackageStatus(packageId);
      return ResponseEntity.ok(status);
    } catch (ResourceNotFoundException ex) {

      return ResponseEntity.notFound().build();
    }
  }

  @PostMapping("/{packageId}/allocate/{droneId}")
  public ResponseEntity<Object> allocatePackageToDrone(@PathVariable Long packageId, @PathVariable Long droneId) {
    try {
      Package allocatedPackage = packageService.allocatePackageToDrone(packageId, droneId);
      return ResponseEntity.ok(allocatedPackage);
    } catch (ResourceNotFoundException ex) {

      return ResponseEntity.notFound().build();
    } catch (IllegalArgumentException ex) {

      return ResponseEntity.badRequest().body("Drone does not have enough capacity or range for this package.");
    }
  }

  @GetMapping("/pending")
  public ResponseEntity<List<Package>> getPendingPackages() {
    List<Package> pendingPackages = packageService.getPendingPackages();
    return ResponseEntity.ok(pendingPackages);
  }

  @GetMapping("/delivered")
  public ResponseEntity<List<Package>> getDeliveredPackages() {
    List<Package> deliveredPackages = packageService.getDeliveredPackages();
    return ResponseEntity.ok(deliveredPackages);
  }
}
