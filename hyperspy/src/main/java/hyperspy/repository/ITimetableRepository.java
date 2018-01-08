package hyperspy.repository;

import hyperspy.domain.entity.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITimetableRepository extends JpaRepository<Timetable, Integer>{
}
