package com.example.demo.repo;

import org.springframework.data.r2dbc.repository.R2dbcRepository;

import com.example.demo.entity.ViewEntity;
import com.example.demo.entity.compositeId.ViewLikeId;


public interface ViewRepo extends R2dbcRepository<ViewEntity, ViewLikeId>{


}
