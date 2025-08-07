package drone_simulator_api.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Drone {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String serialNumber;
  private double currentWeight;
  private double maxWeightCapacity;

  private double maxFlightRange;

  @Enumerated(EnumType.STRING)
  private DroneState state;

  private double locationX;
  private double locationY;

  // Enum para o estado do drone
  public enum DroneState {
    IDLE,
    LOADING,
    IN_FLIGHT,
    DELIVERING,
    RETURNING
  }
}