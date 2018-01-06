package hyperspy.repository;

import hyperspy.domain.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IStateRepository extends JpaRepository<State, Integer>{
}
