package hyperspy.repository;

import hyperspy.domain.entity.WhereIsCapsule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IWhereIsCapsuleRepository extends JpaRepository<WhereIsCapsule, Integer>{

    Optional<WhereIsCapsule> findByCapsuleSideNumber(Integer id);
}
