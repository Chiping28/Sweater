package com.example.sweater.repositories;

import com.example.sweater.dto.MessageDto;
import com.example.sweater.models.Message;
import com.example.sweater.models.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MessagesRepository extends JpaRepository<Message, Integer> {

    @Query("select new com.example.sweater.dto.MessageDto(" +
            "m, count(ml), sum(case when ml = :person then 1 else 0 end) > 0) " +
            "from Message m left join m.likes ml " +
            "where m.text like :text " +
            "group by m")
    Page<MessageDto> findByTextContaining(@Param("text") String filter, Pageable pageable, @Param("person") Person person);

    @Query("select new com.example.sweater.dto.MessageDto(" +
            "m, count(ml), sum(case when ml = :person then 1 else 0 end) > 0) " +
            "from Message m left join m.likes ml " +
            "group by m")
    Page<MessageDto> findAll(Pageable pageable, @Param("person") Person person);

    @Query("select new com.example.sweater.dto.MessageDto(" +
            "m, count(ml), sum(case when ml = :person then 1 else 0 end) > 0) " +
            "from Message m left join m.likes ml " +
            "where m.tag = :tag " +
            "group by m")
    Page<MessageDto> findByTag(@Param("tag") String tag, Pageable pageable, @Param("person") Person person);

    @Query("select new com.example.sweater.dto.MessageDto(" +
            "m, count(ml), sum(case when ml = :person then 1 else 0 end) > 0) " +
            "from Message m left join m.likes ml " +
            "where m.owner.id = :id and m.text like :text " +
            "group by m")
    Page<MessageDto> findByOwnerAndTextContaining(@Param("id") int id, @Param("text") String filter, Pageable pageable, @Param("person") Person person);

    @Query("select new com.example.sweater.dto.MessageDto(" +
            "m, count(ml), sum(case when ml = :person then 1 else 0 end) > 0) " +
            "from Message m left join m.likes ml " +
            "where m.tag = :tag and m.text like :text " +
            "group by m")
    Page<MessageDto> findByTagAndTextContaining(@Param("tag") String tag, @Param("text") String filter, Pageable pageable, @Param("person") Person person);

    @Query("select new com.example.sweater.dto.MessageDto(" +
            "m, count(ml), sum(case when ml = :person then 1 else 0 end) > 0) " +
            "from Message m left join m.likes ml " +
            "where m.id = :id " +
            "group by m")
    MessageDto findById(@Param("id") int id, @Param("person") Person person);
}
