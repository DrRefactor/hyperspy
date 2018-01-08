package hyperspy.repository;

import hyperspy.domain.entity.Capsule;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ICapsuleRepository extends JpaRepository<Capsule, Integer>{
}
