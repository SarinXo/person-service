package com.example.personservice.controller;

import com.example.personservice.database.entity.Person;
import com.example.personservice.database.entity.Weather;
import com.example.personservice.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
public class PersonController {

    private final String locationUrl;
    private final PersonService service;
    private final RestTemplate restTemplate;

    private final String instanceId;


    @Autowired
    public PersonController(@Value("${url.services.location}")String locationUrl,
                            @Value("${eureka.instance.instance-id}")String instanceId,
                            PersonService service,
                            RestTemplate restTemplate) {
        this.instanceId = instanceId;
        this.locationUrl = locationUrl;
        this.service = service;
        this.restTemplate = restTemplate;
    }

    //По-хорошему нужно установить макс размер, но этого задание не требует
    @GetMapping("/persons")
    public ResponseEntity<List<Person>> findPersons() {
        List<Person> personList = service.getPersons();
        var status = personList.isEmpty() ? HttpStatus.NOT_FOUND : HttpStatus.OK;
        return new ResponseEntity<>(personList, status);
    }

    @GetMapping("/persons/{id}")
    public ResponseEntity<Person> findById(@PathVariable int id) {
        Optional<Person> person = service.findById(id);
        return person.isPresent()
                ? new ResponseEntity<>(person.get() , HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/persons")
    public ResponseEntity<?> save(@RequestBody Person person) {
        Optional<Person> person2 = service.pushInDb(person);
        return person2.isPresent()
                ? new ResponseEntity<>(person2, HttpStatus.CREATED)
                : new ResponseEntity<>("Попытка заменить запись", HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/persons/{id}/weather")
    public ResponseEntity<Weather> getWeather(@PathVariable int id) {
        if (!service.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        try{
            String location = service.findById(id).get().getLocation();
            URI url = new URI(locationUrl + "/weather/main?location=" + location);
            return restTemplate.getForEntity(url, Weather.class);
        }catch (RestClientException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/service/id")
    public String getServiceId(){
        return instanceId;
    }

}
