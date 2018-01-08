package hyperspy.repository;

import hyperspy.domain.entity.Connection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IConnectionRepository extends JpaRepository<Connection, Integer>{
    Optional<Connection> findById(final Integer id);
}
