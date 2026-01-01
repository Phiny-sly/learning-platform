package com.phiny.labs.courseservice.controller;

import com.phiny.labs.courseservice.dto.rating.CreateRatingDTO;
import com.phiny.labs.courseservice.dto.rating.RatingDTO;
import com.phiny.labs.courseservice.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/ratings")
@RequiredArgsConstructor
public class RatingController {

    @Autowired private RatingService ratingService;

    @PostMapping(value = {"", "/"})
    @ResponseStatus(HttpStatus.CREATED)
    RatingDTO create(
            @RequestBody CreateRatingDTO payload,
            @RequestParam(required = true) UUID course
    ){
        return ratingService.create(course, payload);
    }

    @GetMapping(value = {"", "/"})
    @ResponseStatus(HttpStatus.OK)
    List<RatingDTO> read(
            @RequestParam(required = false) UUID course,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "created, asc") String[] sort
    ){
        return ratingService.read(page, size, sort, course);
    }

    @GetMapping(value = {"/{id}", "/{id}/"})
    @ResponseStatus(HttpStatus.OK)
    RatingDTO readById(@PathVariable("id") UUID id){
        return ratingService.readById(id);
    }

    @DeleteMapping(value = {"/{id}", "/{id}/"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void removeById(@PathVariable("id") UUID id){
        ratingService.deleteById(id);
    }

}
