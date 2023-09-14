package com.example.personservice.service;

import com.example.personservice.database.entity.Person;
import com.example.personservice.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonService {

    private final PersonRepository repository;

    @Autowired
    public PersonService(PersonRepository repository) {
        this.repository = repository;
    }

    public List<Person> getPersons(){
        return repository.findAll();
    }

    public Optional<Person> findById(int id){
        return repository.findById(id);
    }

    public Optional<Person> pushInDb(Person person){
        Optional<Person> person2 = repository.findById(person.getId());
        if(person2.isEmpty())
            return Optional.of(repository.save(person));
        return Optional.empty();
    }

}
