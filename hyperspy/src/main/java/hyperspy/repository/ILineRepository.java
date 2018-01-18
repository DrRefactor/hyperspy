package hyperspy.repository;

import hyperspy.domain.entity.Line;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ILineRepository extends JpaRepository<Line, Integer>{
    Optional<Line> findById(final Integer id);
}
