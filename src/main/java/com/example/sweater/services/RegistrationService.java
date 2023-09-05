package com.example.sweater.services;

import com.example.sweater.models.Person;
import com.example.sweater.repositories.PeopleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
public class RegistrationService {

    private final PeopleRepository peopleRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailSenderService mailSenderService;

    @Autowired
    public RegistrationService(PeopleRepository peopleRepository, PasswordEncoder passwordEncoder, MailSenderService mailSenderService) {
        this.peopleRepository = peopleRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSenderService = mailSenderService;
    }

    @Transactional
    public void register(Person person) {
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        person.setRole("ROLE_USER");
        person.setActivationCode(UUID.randomUUID().toString());

        peopleRepository.save(person);
        System.out.println("register");
        sendActivateMail(person);
    }

    public void sendActivateMail(Person person) {
        if (!StringUtils.isEmpty(person.getEmail())) {
            String message = String.format(
                    "Hello, %s! \n" +
                            "Welcome to Sweater, please visit next link: http://localhost:8080/auth/activate/%s",
                    person.getUsername(),
                    person.getActivationCode()
            );
            System.out.println(message);
            mailSenderService.send(person.getEmail(), "Activation code", message);
        }
    }

    public boolean isPersonActivate(String code) {
        Person person = peopleRepository.findByActivationCode(code);
        System.out.println("activatePerson() " + code);
        if (person == null) {
            return false;
        }

        person.setActivationCode(null);

        peopleRepository.save(person);

        return true;
    }
}
