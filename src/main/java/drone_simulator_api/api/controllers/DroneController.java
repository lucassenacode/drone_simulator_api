package drone_simulator_api.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import drone_simulator_api.api.entities.Drone;
import drone_simulator_api.api.exceptions.ResourceNotFoundException;
import drone_simulator_api.api.services.DroneService;

@RestController
@RequestMapping("/api/drones")
public class DroneController {

  @Autowired
  private DroneService droneService;

  @GetMapping("/status")
  public ResponseEntity<List<Drone>> getDronesStatus(@RequestParam(required = false) Drone.DroneState state) {
    List<Drone> drones;
    if (state != null) {
      drones = droneService.getDronesByState(state);
    } else {
      drones = droneService.getAllDrones();
    }
    return ResponseEntity.ok(drones);
  }

  @GetMapping("/{droneId}")
  public ResponseEntity<Drone> getDroneById(@PathVariable Long droneId) {
    try {
      Drone drone = droneService.getDroneById(droneId);
      return ResponseEntity.ok(drone);
    } catch (ResourceNotFoundException ex) {
      return ResponseEntity.notFound().build();
    }
  }

  @PutMapping("/{droneId}/state")
  public ResponseEntity<Object> updateDroneState(@PathVariable Long droneId, @RequestParam String newState) {
    try {

      Drone.DroneState state = Drone.DroneState.valueOf(newState.toUpperCase());
      Drone updatedDrone = droneService.updateDroneState(droneId, state);
      return ResponseEntity.ok(updatedDrone);
    } catch (IllegalArgumentException ex) {
      return ResponseEntity.badRequest()
          .body("Invalid state provided. Valid states are: IDLE, LOADING, IN_FLIGHT, DELIVERING, RETURNING.");
    } catch (ResourceNotFoundException ex) {
      return ResponseEntity.notFound().build();
    }
  }

  @PostMapping
  public ResponseEntity<Drone> createDrone(@RequestBody Drone newDrone) {
    try {
      Drone createdDrone = droneService.createDrone(newDrone);
      return new ResponseEntity<>(createdDrone, HttpStatus.CREATED);
    } catch (IllegalArgumentException ex) {
      return ResponseEntity.badRequest().body(null);
    }
  }

  @PostMapping("/allocate-pending")
  public ResponseEntity<String> allocatePendingPackages() {
    try {
      droneService.allocatePendingPackages();
      return ResponseEntity.accepted().body("Pending packages allocated to available drones.");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error during allocation of pending packages.");
    }
  }
}
