package hyperspy.repository;

import hyperspy.domain.entity.Timetable;
import hyperspy.domain.entity.TimetableTimeFreq;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;

public interface ITimetableTimeFreqRepository extends JpaRepository<TimetableTimeFreq, TimetableTimeFreq.TimetableTimeFrequencyPK>{

    Optional<TimetableTimeFreq> findByTimetableAndStartHour(final Integer timetable, final Date startHour);

}
