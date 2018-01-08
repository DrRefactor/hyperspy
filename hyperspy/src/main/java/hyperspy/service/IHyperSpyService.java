package hyperspy.service;

import hyperspy.domain.TypeEnum;
import hyperspy.domain.dto.CapsuleLocationDto;

import java.util.List;

public interface IHyperSpyService {
    List getAll(TypeEnum type);

    List<CapsuleLocationDto> findCapsules();
}
