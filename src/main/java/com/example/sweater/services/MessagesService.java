package com.example.sweater.services;

import com.example.sweater.dto.MessageDto;
import com.example.sweater.models.Message;
import com.example.sweater.models.Person;
import com.example.sweater.repositories.MessagesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class MessagesService {

    private final MessagesRepository messagesRepository;
    private final PeopleService peopleService;

    @Autowired
    public MessagesService(MessagesRepository messagesRepository, PeopleService peopleService) {
        this.messagesRepository = messagesRepository;
        this.peopleService = peopleService;
    }

    @Transactional
    public Page<MessageDto> findAll(Pageable pageable, Person person) {
        return messagesRepository.findAll(pageable, person);
    }

    @Transactional
    public void save(Message message) {
        messagesRepository.save(message);
    }

    @Transactional
    public Page<MessageDto> findByTextContaining(String like, Pageable pageable, Person person) {
        return messagesRepository.findByTextContaining(like, pageable, person);
    }

    @Transactional
    public void update(int id, Message message) {
        message.setId(id);
        messagesRepository.save(message);
    }

    public MessageDto findById(int id, Person person) {
        return messagesRepository.findById(id, person);
    }

    public void delete(int id) {
        messagesRepository.deleteById(id);
    }

    public Page<MessageDto> findByTag(String tag, Pageable pageable, Person person) {
        return messagesRepository.findByTag(tag, pageable, person);
    }

    public Page<MessageDto> findByOwnerAndTextContaining(int id, String filter, Pageable pageable, Person person) {
        return messagesRepository.findByOwnerAndTextContaining(peopleService.findById(id).getId(), filter, pageable, person);
    }

    public List<Integer> findPaginated(Page<MessageDto> page) {
        List<Integer> paginated = new ArrayList<>();
        if (page.getTotalPages() > 5) {
            int currentPage = page.getNumber() + 1;
            if (currentPage > 4) {
                paginated.addAll(Arrays.asList(1, -1, currentPage - 2, currentPage - 1, currentPage));
            } else {
                for (int i = 1; i <= currentPage; i++) {
                    paginated.add(i);
                }
            }
            if (page.getTotalPages() - currentPage > 3) {
                paginated.addAll(Arrays.asList(currentPage + 1, currentPage + 2, -1, page.getTotalPages()));
            } else {
                for (int i = currentPage + 1; i <= page.getTotalPages(); i++) {
                    paginated.add(i);
                }
            }
        } else {
            for (int i = 1; i <= page.getTotalPages(); i++) {
                paginated.add(i);
            }
            return paginated;
        }
        return paginated;
    }

    public Page<MessageDto> findByTagAndTextContaining(String tag, String filter, Pageable pageable, Person person) {
        return messagesRepository.findByTagAndTextContaining(tag, filter, pageable, person);
    }

    public Message findById(int id) {
        return messagesRepository.findById(id).orElse(null);
    }

//    public Page<MessageDto> getMessageList(Pageable pageable, Person person, String filter, String tag, int personId) {
//        if (filter.isEmpty()) {
//            if (tag.isEmpty()) {
//                if (personId == 0) {
//                    return messagesRepository.findAll(pageable, person);
//                }
//            }
//            return messagesRepository.findByTag(tag, pageable, person);
//        }
//        return ;
//    }
}
