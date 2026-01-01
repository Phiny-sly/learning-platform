package com.phiny.labs.courseservice.controller;

import com.phiny.labs.courseservice.dto.topic.CreateTopicDTO;
import com.phiny.labs.courseservice.dto.topic.TopicDTO;
import com.phiny.labs.courseservice.dto.topic.TopicListDTO;
import com.phiny.labs.courseservice.dto.topic.UpdateTopicDTO;
import com.phiny.labs.courseservice.service.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("api/topics")
@RequiredArgsConstructor
public class TopicController {

    @Autowired private TopicService topicService;

    @PostMapping(value = {"", "/"})
    @ResponseStatus(HttpStatus.CREATED)
    TopicDTO create(
            @RequestBody CreateTopicDTO payload,
            @RequestParam(required = true) UUID course
    ){
        return topicService.create(course, payload);
    }

    @GetMapping(value = {"", "/"})
    @ResponseStatus(HttpStatus.OK)
    List<TopicListDTO> read(
            @RequestParam(required = false) UUID course,
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "created, asc") String[] sort
    ){ return  topicService.read(page, size, sort, q, course); }

    @GetMapping(value = {"/{id}", "/{id}/"})
    @ResponseStatus(HttpStatus.OK)
    TopicDTO readById(@PathVariable("id") UUID id){
        return topicService.readById(id);
    }

    @PatchMapping(value = {"/{id}", "/{id}/"})
    @ResponseStatus(HttpStatus.ACCEPTED)
    TopicDTO updateById(@PathVariable("id") UUID id, @RequestBody UpdateTopicDTO payload){
        return topicService.updateById(id, payload);
    }

    @DeleteMapping(value = {"/{id}", "/{id}/"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void removeById(@PathVariable("id") UUID id){
        topicService.deleteById(id);
    }

}
