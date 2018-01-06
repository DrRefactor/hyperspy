package hyperspy.repository;

import hyperspy.domain.entity.Capsule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICapsuleRepository extends JpaRepository<Capsule, Integer>{
}
