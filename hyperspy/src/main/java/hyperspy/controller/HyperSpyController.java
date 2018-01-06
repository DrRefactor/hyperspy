package hyperspy.controller;

import hyperspy.domain.TypeEnum;
import hyperspy.domain.dto.CapsuleLocationDto;
import hyperspy.service.IHyperSpyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @GetMapping(value = "/capsule/location/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CapsuleLocationDto getCapsuleLocation(@PathVariable Integer id){
        return hyperSpyService.findCapsule(id);
    }
}