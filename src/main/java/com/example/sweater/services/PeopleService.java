package com.example.sweater.services;

import com.example.sweater.models.Person;
import com.example.sweater.repositories.PeopleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PeopleService {
    private final PeopleRepository peopleRepository;
    private final RegistrationService registrationService;

    @Autowired
    public PeopleService(PeopleRepository peopleRepository, RegistrationService registrationService) {
        this.peopleRepository = peopleRepository;
        this.registrationService = registrationService;
    }

    @Transactional
    public Optional<Person> findByUsername(String username) {
        return peopleRepository.findByUsername(username);
    }

    @Transactional
    public List<Person> findAll() {
        return peopleRepository.findAll();
    }

    @Transactional
    public Person findById(int id) {
        return peopleRepository.findById(id).orElse(null);
    }

    @Transactional
    public void update(int id, Person person) {
        person.setId(id);
        peopleRepository.save(person);
        registrationService.sendActivateMail(person);
    }

    @Transactional
    public Optional<Person> findByEmail(String email) {
        return peopleRepository.findByEmail(email);
    }

    public void subscribe(Person person, int id) {
        findById(id).getSubscribers().add(person);
        peopleRepository.save(findById(id));

    }

    public void unsubscribe(Person person, int id) {
        findById(id).getSubscribers().remove(person);
        peopleRepository.save(findById(id));
    }
}
