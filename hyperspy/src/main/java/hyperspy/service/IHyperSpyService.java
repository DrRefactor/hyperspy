package hyperspy.service;

import hyperspy.domain.TypeEnum;
import hyperspy.domain.dto.CapsuleLocationDto;
import hyperspy.domain.dto.TimetableFreqDto;

import java.util.Date;
import java.util.List;

public interface IHyperSpyService {
    List getAll(TypeEnum type);

    List<CapsuleLocationDto> findCapsules();

    void createTimetableFrequency(TimetableFreqDto dto);

    void deleteTimetableFrequency(Integer timetableId, Date startHour);

}
