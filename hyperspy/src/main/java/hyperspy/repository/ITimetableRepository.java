package hyperspy.repository;

import hyperspy.domain.entity.Line;
import hyperspy.domain.entity.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.stream.Stream;

public interface ITimetableRepository extends JpaRepository<Timetable, Integer>{
    Stream<Timetable> findByLine(final Line line);

    Optional<Timetable> findById(final Integer id);
}
