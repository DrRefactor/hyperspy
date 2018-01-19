package hyperspy.service;

import hyperspy.domain.TypeEnum;
import hyperspy.domain.dto.CapsuleLocationDto;
import hyperspy.domain.dto.TimetableFreqDto;
import hyperspy.domain.entity.*;
import hyperspy.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

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
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

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

    private CapsuleLocationDto findCapsule(final WhereIsCapsule whereIsCapsule){

        final CapsuleLocationDto capsuleLocation = new CapsuleLocationDto();
        capsuleLocation.setCapsuleId(whereIsCapsule.getCapsuleSideNumber());
        
        final InfrastructureElement infrastructureElement = whereIsCapsule.getInfrastructureElement();
        capsuleLocation.setInfratuctureElementId(infrastructureElement.getId());

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
            final Double s = speed.doubleValue() * ((double)(new Date().getTime() - start) / (1000*3600));
            final BigDecimal part = BigDecimal.valueOf(s / distance.doubleValue());
            final Station startStation = connection.getStartStation();
            final Station endStation = connection.getEndStation();
            final BigDecimal coorX = startStation.getCoorX().add(endStation.getCoorX().subtract(startStation.getCoorX()).multiply(part));
            final BigDecimal coorY = startStation.getCoorY().add(endStation.getCoorY().subtract(startStation.getCoorY()).multiply(part));
            capsuleLocation.setCoorX(coorX);
            capsuleLocation.setCoorY(coorY);
        }

        return capsuleLocation;
    }

    @Transactional
    @Override
    public List<CapsuleLocationDto> findCapsules() {
        return whereIsCapsuleRepository.findAll()
            .stream()
            .map(x -> findCapsule(x))
            .collect(Collectors.toList());

    }

    @Transactional
    @Override
    public void createTimetableFrequency(final TimetableFreqDto dto){
        final Line line = lineRepository.findOne(dto.getLineId());
        final Set<Timetable> timetables = timetableRepository.findByLine(line).collect(Collectors.toSet());
        final Set<TimetableTimeFreq> freqTimetables = timetables.stream()
                .map(t -> timetableTimeFreqRepository.findByTimetableAndStartHour(t.getId(), dto.getStartHour()))
                .filter(t -> t.isPresent())
                .map(t -> t.get())
                .collect(Collectors.toSet());
        if(freqTimetables.isEmpty()){
            timetables.forEach(t -> {
//                Timetable timetable = new Timetable();
//                timetable.setLine(line);
//                try {
//                    timetable.setFromDate(sdf.parse("2018-01-31"));
//                    timetable.setUntil(sdf.parse("2018-12-31"));
//                    timetableRepository.save(timetable);
                    TimetableTimeFreq freqEntity = new TimetableTimeFreq();
                    freqEntity.setStartHour(dto.getStartHour());
                    freqEntity.setTimetable(t.getId());
                    freqEntity.setFrequency(dto.getFrequency());
                    timetableTimeFreqRepository.save(freqEntity);
//                } catch (ParseException e) {
//                    e.printStackTrace();
         //       }
            });
        }
    }

    @Transactional
    @Override
    public void deleteTimetableFrequency(final Integer timetableId, final Date startHour){

        final Optional<Timetable> timetable = timetableRepository.findById(timetableId);
        if(timetable.isPresent()){
            timetableTimeFreqRepository.findByTimetableAndStartHour(timetableId, startHour).ifPresent(t -> timetableTimeFreqRepository.delete(t));
        }
    }
    
    private List findAll(JpaRepository repository){
        return repository.findAll();
    }

}