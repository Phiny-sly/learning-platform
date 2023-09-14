package com.turntabl.labs.tenantservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.turntabl.labs.tenantservice.dto.CreateTenantDTO;
import com.turntabl.labs.tenantservice.dto.TenantDTO;
import com.turntabl.labs.tenantservice.dto.UpdateTenantDTO;
import com.turntabl.labs.tenantservice.exception.InternalServerError;
import com.turntabl.labs.tenantservice.exception.NotFoundError;
import com.turntabl.labs.tenantservice.model.Tenant;
import com.turntabl.labs.tenantservice.repository.TenantRepository;
import com.turntabl.labs.tenantservice.util.Util;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TenantService {

    @Autowired private TenantRepository tenantRepository;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ModelMapper modelMapper;

    public TenantDTO create(CreateTenantDTO payload){
        return modelMapper.map(tenantRepository.save(modelMapper.map(payload, Tenant.class)), TenantDTO.class);
    }

//    add query param filtering
    public List<TenantDTO> read(Integer page, Integer size, String[] sort){
        return tenantRepository.findAll(
                PageRequest.of(page, size, Sort.by(Util.generateSortOrders(sort)))
        ).stream().map(t -> modelMapper.map(t, TenantDTO.class)).toList();
    }

    public TenantDTO readById(UUID id){
        return modelMapper.map(tenantRepository.findById(id).orElseThrow(()->new NotFoundError()), TenantDTO.class);
    }

    public TenantDTO updateById(UUID id, UpdateTenantDTO payload){

        Tenant tenant = tenantRepository.findById(id).orElseThrow(()-> new NotFoundError(id));

        try {objectMapper.readerForUpdating(tenant).readValue(objectMapper.writeValueAsString(payload));}
        catch (JsonProcessingException e) { throw new InternalServerError("update failed, something went wrong -> " + e.getMessage());}

        return modelMapper.map(tenant, TenantDTO.class);

    }

    public void deleteById(UUID id){ tenantRepository.deleteById(id); }

}
