package com.example.sweater.controllers;

import com.example.sweater.dto.MessageDto;
import com.example.sweater.models.Message;
import com.example.sweater.models.Person;
import com.example.sweater.security.PersonDetails;
import com.example.sweater.services.MessagesService;
import com.example.sweater.services.PeopleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Controller
@RequestMapping("/messages")
public class MessagesController {

    private final MessagesService messagesService;

    private final PeopleService peopleService;

    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    public MessagesController(MessagesService messagesService, PeopleService peopleService) {
        this.messagesService = messagesService;
        this.peopleService = peopleService;
    }

    @GetMapping()
    public String index(@RequestParam(name = "filter", defaultValue = "") String filter,
                        @ModelAttribute("message") Message message,
                        Model model,
                        @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        Person person = peopleService.findById(personDetails.getPerson().getId());

        model.addAttribute("personDetails", person);
        List<Integer> paginated = new ArrayList<>();
        if (filter == null || filter.isEmpty()) {
            Page<MessageDto> page = messagesService.findAll(pageable, person);
            paginated = messagesService.findPaginated(page);
            model.addAttribute("messages", page);
        }
        if (filter != null && !filter.isEmpty()){
            Page<MessageDto> page = messagesService.findByTextContaining("%" + filter + "%", pageable, person);
            paginated = messagesService.findPaginated(page);
            model.addAttribute("messages", page);
        }

        if (!paginated.isEmpty())
            model.addAttribute("pages", paginated);
        model.addAttribute("filter", filter);
        return "messages/index";
    }

    @GetMapping("/{tagName}/tag")
    public String findByTag(@RequestParam(name = "filter", defaultValue = "") String filter,
                            @PathVariable("tagName") String tag,
                            Model model,
                            @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        Person person = peopleService.findById(personDetails.getPerson().getId());

        model.addAttribute("personDetails", person);
        List<Integer> paginated;
        Page<MessageDto> page = messagesService.findByTag(tag, pageable, person);
        paginated = messagesService.findPaginated(page);
        model.addAttribute("messages", page);

        if (filter != null && !filter.isEmpty()) {
            Page<MessageDto> page2 = messagesService.findByTagAndTextContaining(tag, "%" + filter + "%", pageable, person);
            paginated = messagesService.findPaginated(page2);
            model.addAttribute("messages", page2);
        }

        if (!paginated.isEmpty())
            model.addAttribute("pages", paginated);
        model.addAttribute("filter", filter);
        model.addAttribute("tag", tag);
        return "messages/tag";
    }

    @PostMapping()
    public String add(@ModelAttribute("message") Message message,
                      @RequestParam(name = "filename", required = false) MultipartFile file
    ) throws IOException {
        System.out.println(file);
        if (!file.isEmpty()) {
            System.out.println("file != null");
            File uploadDir = new File(uploadPath);

            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            String uuidFile = UUID.randomUUID().toString();
            String resultFileName = uuidFile + "." + file.getOriginalFilename();

            file.transferTo(new File(uploadPath + "/" + resultFileName));

            message.setFile(resultFileName);
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        Person person = peopleService.findById(personDetails.getPerson().getId());
        message.setOwner(person);

        messagesService.save(message);
        return "redirect:/messages";
    }

    @GetMapping("/{id}")
    public String showMessage(@PathVariable("id") int messageId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        Person person = peopleService.findById(personDetails.getPerson().getId());

        model.addAttribute("message", messagesService.findById(messageId, person));
        model.addAttribute("personDetails", person);
        return "messages/show";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") int id, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        Person person = peopleService.findById(personDetails.getPerson().getId());

        model.addAttribute("message", messagesService.findById(id, person));
        return "messages/edit";
    }

    @PatchMapping("/{id}")
    public String update(@PathVariable("id") int id,
                         @ModelAttribute("message") @Valid Message message,
                         @RequestParam(name = "filename", required = false) MultipartFile file,
                         BindingResult bindingResult
    ) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        Person person = peopleService.findById(personDetails.getPerson().getId());

        if (!file.isEmpty()) {
            System.out.println("file != null");
            File uploadDir = new File(uploadPath);

            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            String uuidFile = UUID.randomUUID().toString();
            String resultFileName = uuidFile + "." + file.getOriginalFilename();

            file.transferTo(new File(uploadPath + "/" + resultFileName));

            message.setFile(resultFileName);
        }
        if (bindingResult.hasErrors()) {
            return "messages/edit";
        }
        message.setOwner(messagesService.findById(id, person).getOwner());
        messagesService.update(id, message);
        return "redirect:/messages";
    }

    @DeleteMapping("/{id}")
    String deleteMessage(@PathVariable("id") int id) {
        messagesService.delete(id);
        return "redirect:/messages";
    }

    @GetMapping("/{id}/like")
    public String likeMessage(@PathVariable("id") int id,
                              RedirectAttributes redirectAttributes,
                              @RequestHeader(required = false) String referer
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        Person person = peopleService.findById(personDetails.getPerson().getId());

        Message message = messagesService.findById(id);
        Set<Person> likes = message.getLikes();

        if (likes.contains(person)) {
            likes.remove(person);
        } else {
            likes.add(person);
        }

        messagesService.save(message);

        UriComponents components = UriComponentsBuilder.fromHttpUrl(referer).build();

        components.getQueryParams().forEach(redirectAttributes::addAttribute);

        return "redirect:" + components.getPath();
    }
}
