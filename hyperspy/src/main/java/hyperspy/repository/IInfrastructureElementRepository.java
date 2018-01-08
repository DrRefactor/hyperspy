package hyperspy.repository;

import hyperspy.domain.entity.InfrastructureElement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IInfrastructureElementRepository extends JpaRepository<InfrastructureElement, Integer>{
}
