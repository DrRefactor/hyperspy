package hyperspy.service;

import hyperspy.domain.TypeEnum;
import hyperspy.domain.dto.CapsuleLocationDto;
import hyperspy.domain.entity.Connection;
import hyperspy.domain.entity.InfrastructureElement;
import hyperspy.domain.entity.Station;
import hyperspy.domain.entity.WhereIsCapsule;
import hyperspy.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class HyperSpyService implements IHyperSpyService {
    private final ICityRepository cityRepository;
    private final ICapsuleRepository capsuleRepository;
    private final ICapsuleTypeRepository capsuleTypeRepository;
    private final IConnectionRepository connectionRepository;
    private final IInfrastructureElementRepository infrastructureElementRepository;
    private final IInfrastructureElementTypeRepository infrastructureElementTypeRepository;
    private final ILineRepository lineRepository;
    private final IStateRepository stateRepository;
    private final IStateTypeRepository stateTypeRepository;
    private final IStationRepository stationRepository;
    private final IStopsOnRouteRepository stopsOnRouteRepository;
    private final ITimetableAtTimetableDayRepository timetableAtTimetableDayRepository;
    private final ITimetableDayRepository timetableDayRepository;
    private final ITimetableRepository timetableRepository;
    private final ITimetableTimeFreqRepository timetableTimeFreqRepository;
    private final IWhereIsCapsuleRepository whereIsCapsuleRepository;

    @Autowired
    HyperSpyService(ICityRepository cityRepository, ICapsuleRepository capsuleRepository, ICapsuleTypeRepository capsuleTypeRepository, IConnectionRepository connectionRepository, IInfrastructureElementRepository infrastructureElementRepository,
            IInfrastructureElementTypeRepository iInfrastructureElementTypeRepository, ILineRepository lineRepository, IStateRepository stateRepository,
            IStateTypeRepository stateTypeRepository, IStationRepository stationRepository, IStopsOnRouteRepository stopsOnRouteRepository,
            ITimetableAtTimetableDayRepository timetableAtTimetableDayRepository, ITimetableDayRepository timetableDayRepository, ITimetableRepository timetableRepository,
            ITimetableTimeFreqRepository timetableTimeFreqRepository, IWhereIsCapsuleRepository whereIsCapsuleRepository) {
        this.cityRepository = cityRepository;
        this.capsuleRepository = capsuleRepository;
        this.capsuleTypeRepository = capsuleTypeRepository;
        this.connectionRepository = connectionRepository;
        this.infrastructureElementRepository = infrastructureElementRepository;
        this.infrastructureElementTypeRepository = iInfrastructureElementTypeRepository;
        this.lineRepository = lineRepository;
        this.stateRepository = stateRepository;
        this.stateTypeRepository = stateTypeRepository;
        this.stationRepository = stationRepository;
        this.stopsOnRouteRepository = stopsOnRouteRepository;
        this.timetableAtTimetableDayRepository = timetableAtTimetableDayRepository;
        this.timetableDayRepository = timetableDayRepository;
        this.timetableRepository = timetableRepository;
        this.timetableTimeFreqRepository = timetableTimeFreqRepository;
        this.whereIsCapsuleRepository = whereIsCapsuleRepository;
    }

    @Transactional
    @Override
    public List getAll(TypeEnum type){
        switch (type){
            case CITY:
                return findAll(cityRepository);
            case CAPSULE:
                return findAll(capsuleRepository);
            case CAPSULE_TYPE:
                return findAll(capsuleTypeRepository);
            case CONNECTION:
                return findAll(connectionRepository);
            case INFRASTRUCTURE_ELEMENT:
                return findAll(infrastructureElementRepository);
            case INFRASTRUCTURE_ELEMENT_TYPE:
                return findAll(infrastructureElementTypeRepository);
            case LINE:
                return findAll(lineRepository);
            case STATE:
                return findAll(stateRepository);
            case STATE_TYPE:
                return findAll(stateTypeRepository);
            case STATION:
                return findAll(stationRepository);
            case STOPS_ON_ROUTE:
                return findAll(stopsOnRouteRepository);
            case TIMETABLE:
                return findAll(timetableRepository);
            case TIMETABLE_AT_TIMETABLE_DAY:
                return findAll(timetableAtTimetableDayRepository);
            case TIMETABLE_DAY:
                return findAll(timetableDayRepository);
            case TIMETABLE_TIME_FREQ:
                return findAll(timetableTimeFreqRepository);
            case WHERE_IS_CAPSULE:
                return findAll(whereIsCapsuleRepository);
        }
        throw new RuntimeException();
    }

    @Transactional
    @Override
    public CapsuleLocationDto findCapsule(final Integer id){
        final WhereIsCapsule whereIsCapsule = whereIsCapsuleRepository.findByCapsuleSideNumber(id).orElseThrow(RuntimeException::new);

        final CapsuleLocationDto capsuleLocation = new CapsuleLocationDto();
        capsuleLocation.setCapsuleId(id);
        capsuleLocation.setInfratuctureElementId(whereIsCapsule.getInfrastructureElement().getId());

        final InfrastructureElement infrastructureElement = whereIsCapsule.getInfrastructureElement();
        final String type = infrastructureElement.getInfrastructureElementType().getType().toUpperCase();

        if(type.equals("STATION")) {
           final Station station = stationRepository.findById(infrastructureElement.getId()).orElseThrow(RuntimeException::new);
           capsuleLocation.setCoorX(station.getCoorX());
           capsuleLocation.setCoorY(station.getCoorY());
        }
        else if(type.equals("CONNECTION")) {
            final Connection connection = connectionRepository.findById(infrastructureElement.getId()).orElseThrow(RuntimeException::new);
            final Integer distance = connection.getDistance();
            final Integer speed = connection.getMaxSpeed();
            final Long start = whereIsCapsule.getStartTime().getTime();
            final Long s = speed * (new Date().getTime() - start);
            final BigDecimal part = BigDecimal.valueOf(s.doubleValue() / distance.doubleValue());
            final Station startStation = connection.getStartStation();
            final Station endStation = connection.getEndStation();
            final BigDecimal coorX = startStation.getCoorX().add(endStation.getCoorX().subtract(startStation.getCoorX()).multiply(part));
            final BigDecimal coorY = startStation.getCoorY().add(endStation.getCoorY().subtract(startStation.getCoorY()).multiply(part));
            capsuleLocation.setCoorX(coorX);
            capsuleLocation.setCoorY(coorY);
        }

        return capsuleLocation;
    }

    private List findAll(JpaRepository repository){
        return repository.findAll();
    }

}