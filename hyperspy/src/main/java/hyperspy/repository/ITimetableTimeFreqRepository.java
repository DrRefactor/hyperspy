package hyperspy.repository;

import hyperspy.domain.entity.TimetableTimeFreq;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITimetableTimeFreqRepository extends JpaRepository<TimetableTimeFreq, TimetableTimeFreq.TimetableTimeFrequencyPK>{
}
