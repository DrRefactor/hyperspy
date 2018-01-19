package hyperspy.controller;

import hyperspy.domain.TypeEnum;
import hyperspy.domain.dto.CapsuleLocationDto;
import hyperspy.domain.dto.TimetableFreqDto;
import hyperspy.service.IHyperSpyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

@CrossOrigin
@RestController
public class HyperSpyController {

    private final IHyperSpyService hyperSpyService;

    @Autowired
    HyperSpyController(IHyperSpyService hyperSpyService){
        this.hyperSpyService = hyperSpyService;
    }

    @GetMapping(value = "/{param}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List getAll(@PathVariable String param){
        final TypeEnum type = TypeEnum.findByName(param).orElseThrow(RuntimeException::new);
        return hyperSpyService.getAll(type);
    }

    @GetMapping(value = "/capsule/location", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CapsuleLocationDto> getCapsuleLocation(){
        return hyperSpyService.findCapsules();
    }

    @PostMapping(value = "/timetable", consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void putTimetable(@RequestBody TimetableFreqDto dto){
        hyperSpyService.createTimetableFrequency(dto);
    }

    @DeleteMapping(value = "timetable/{id}", consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteTimetableFrequency(@PathVariable(name = "id") Integer timetableId, @DateTimeFormat(pattern = "HH:mm")final Date startHour){
        hyperSpyService.deleteTimetableFrequency(timetableId, startHour);
    }
}