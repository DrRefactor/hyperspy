package hyperspy.repository;

import hyperspy.domain.entity.CapsuleType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICapsuleTypeRepository extends JpaRepository<CapsuleType, Integer>{
}
