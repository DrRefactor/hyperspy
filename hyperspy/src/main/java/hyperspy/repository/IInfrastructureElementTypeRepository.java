package hyperspy.repository;

import hyperspy.domain.entity.InfrastructureElementType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IInfrastructureElementTypeRepository extends JpaRepository<InfrastructureElementType, Integer>{
}
