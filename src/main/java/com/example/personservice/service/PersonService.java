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
        List<Person> per = repository.findAll();
        return per;
    }

    public Optional<Person> findById(int id){
        return repository.findById(id);
    }

    public Person pushInDb(Person person){
        Optional<Person> person2 = repository.findById(person.getId());
        if(person2.isEmpty())
            repository.save(person);
        return person2.orElse(person);
    }

}
