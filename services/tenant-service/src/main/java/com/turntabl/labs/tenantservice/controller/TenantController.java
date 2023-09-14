package com.turntabl.labs.tenantservice.controller;

import com.turntabl.labs.tenantservice.dto.CreateTenantDTO;
import com.turntabl.labs.tenantservice.dto.TenantDTO;
import com.turntabl.labs.tenantservice.dto.UpdateTenantDTO;
import com.turntabl.labs.tenantservice.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/tenants")
@RequiredArgsConstructor
public class TenantController {

    @Autowired private TenantService tenantService;

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    TenantDTO create(@RequestBody CreateTenantDTO payload){
        return tenantService.create(payload);
    }

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    List<TenantDTO> read(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "created, asc") String[] sort
    ){
        return tenantService.read(page, size, sort);
    }

    @GetMapping(value = {"/{id}", "/{id}/"})
    @ResponseStatus(HttpStatus.OK)
    TenantDTO readById(@PathVariable("id") UUID id){
        return tenantService.readById(id);
    }

    @PatchMapping(value = {"/{id}", "/{id}/"})
    @ResponseStatus(HttpStatus.ACCEPTED)
    TenantDTO updateById(@PathVariable("id") UUID id, @RequestBody UpdateTenantDTO payload){
        return tenantService.updateById(id, payload);
    }

    @DeleteMapping(value = {"/{id}", "/{id}/"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void removeById(@PathVariable("id") UUID id){
        tenantService.deleteById(id);
    }

}
