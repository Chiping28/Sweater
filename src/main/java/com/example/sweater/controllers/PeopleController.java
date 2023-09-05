package com.example.sweater.controllers;

import com.example.sweater.models.Person;
import com.example.sweater.models.Role;
import com.example.sweater.security.PersonDetails;
import com.example.sweater.services.MessagesService;
import com.example.sweater.services.PeopleService;
import com.example.sweater.util.PersonValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/people")
//@PreAuthorize("hasRole('ROLE_ADMIN')")
public class PeopleController {
    private final PeopleService peopleService;

    private final PersonValidator personValidator;

    private final MessagesService messagesService;

    @Autowired
    public PeopleController(PeopleService peopleService, PersonValidator personValidator, MessagesService messagesService) {
        this.peopleService = peopleService;
        this.personValidator = personValidator;
        this.messagesService = messagesService;
    }

    @GetMapping()
    public String index(Model model) {
        model.addAttribute("people", peopleService.findAll());
        return "people/index";
    }

    @GetMapping("/{id}")
    public String personPage(@PathVariable("id") int id,
                             @RequestParam(value = "filter", required = false, defaultValue = "") String filter,
                             Model model,
                             @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        Person person = peopleService.findById(personDetails.getPerson().getId());

        model.addAttribute("person", peopleService.findById(id));
        model.addAttribute("personDetails", person);
        model.addAttribute("subscribersCount", peopleService.findById(id).getSubscribers().size());
        model.addAttribute("subscriptionsCount", peopleService.findById(id).getSubscriptions().size());
        if (peopleService.findById(id).getSubscribers().contains(peopleService.findById(person.getId()))) {
            model.addAttribute("subscribe", true);
        } else {
            model.addAttribute("subscribe", false);
        }
        if (!peopleService.findById(id).getMessages().isEmpty()) {
            model.addAttribute("messages", messagesService.findByOwnerAndTextContaining(id, "%" + filter + "%", pageable, person));
        }
        model.addAttribute("filter", filter);
        return "people/show";
    }

    @GetMapping("/subscribe/{id}")
    public String subscribe(@PathVariable("id") int id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        Person person = peopleService.findById(personDetails.getPerson().getId());

        peopleService.subscribe(person, id);
        return "redirect:/people/" + id;
    }

    @GetMapping("/unsubscribe/{id}")
    public String unsubscribe(@PathVariable("id") int id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        Person person = peopleService.findById(personDetails.getPerson().getId());

        peopleService.unsubscribe(person, id);
        return "redirect:/people/" + id;
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        Person person = peopleService.findById(personDetails.getPerson().getId());

        model.addAttribute("person", person);
        return "people/profile";
    }

    @GetMapping("/profile/{id}")
    public String profilePerson(@PathVariable("id") int id, Model model) {
        model.addAttribute("person", peopleService.findById(id));
        return "people/profile";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") int id, Model model) {
        model.addAttribute("person", peopleService.findById(id));
        model.addAttribute("roles", Role.values());
        return "people/edit";
    }

    @PatchMapping("/{id}")
    public String update(@PathVariable("id") int id,
                         @ModelAttribute("person") @Valid Person person,
                         BindingResult bindingResult, Model model
    ) {
        personValidator.validate(person, bindingResult);
        model.addAttribute("roles", Role.values());

        if (bindingResult.hasErrors()) {
            return "people/edit";
        }
        peopleService.update(id, person);

        return "redirect:/people/profile";
    }
}
