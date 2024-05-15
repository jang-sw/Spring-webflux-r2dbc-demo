package com.example.demo.repo;

import org.springframework.data.r2dbc.repository.R2dbcRepository;

import com.example.demo.entity.ContentEntity;


public interface ContentRepo extends R2dbcRepository<ContentEntity, Long>{

}
