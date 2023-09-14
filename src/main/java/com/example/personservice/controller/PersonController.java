package com.example.personservice.controller;

import com.example.personservice.database.entity.Person;
import com.example.personservice.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class PersonController {

    private final PersonService service;

    @Autowired
    public PersonController(PersonService service) {
        this.service = service;
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

}
