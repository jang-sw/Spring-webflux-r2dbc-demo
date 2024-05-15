package com.example.demo.repo;

import org.springframework.data.r2dbc.repository.R2dbcRepository;

import com.example.demo.entity.LikeEntity;
import com.example.demo.entity.compositeId.ViewLikeId;


public interface LikeRepo extends R2dbcRepository<LikeEntity, ViewLikeId>{


}
