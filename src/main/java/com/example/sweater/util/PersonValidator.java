package com.example.sweater.util;

import com.example.sweater.models.Person;
import com.example.sweater.services.PeopleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

@Component
public class PersonValidator implements Validator {

    private final PeopleService peopleService;

    @Autowired
    public PersonValidator(PeopleService peopleService) {
        this.peopleService = peopleService;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return Person.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Person person = (Person)o;
        Optional<Person> newPerson = peopleService.findByUsername(person.getUsername());
        if (newPerson.isPresent() && person.getId() != newPerson.get().getId()) {
            errors.rejectValue("username", "", "Человек с таким именем пользователя уже существует");
        }
        newPerson = peopleService.findByEmail(person.getEmail());
        if (newPerson.isPresent() && person.getId() != newPerson.get().getId()) {
            errors.rejectValue("email", "", "Человек с такой почтой уже существует");
        }
    }
}
