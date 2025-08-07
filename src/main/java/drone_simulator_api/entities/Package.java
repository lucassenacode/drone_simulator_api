package drone_simulator_api.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Package {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private double destinationX;
  private double destinationY;

  private double weight;

  @Enumerated(EnumType.STRING)
  private Priority priority;
  @Enumerated(EnumType.STRING)
  private PackageStatus status;

  @ManyToOne
  @JoinColumn(name = "drone_id")
  private Drone allocatedDrone;

  // Enum para a prioridade da entrega
  public enum Priority {
    LOW,
    MEDIUM,
    HIGH
  }

  // Enum para o status da entrega
  public enum PackageStatus {
    PENDING,
    IN_TRANSIT,
    DELIVERED
  }
}