package hyperspy.repository;

import hyperspy.domain.entity.StateType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IStateTypeRepository extends JpaRepository<StateType, Integer>{
}
