package com.example.demo.dto;

import java.util.List;

import com.example.demo.entity.ContentEntity;

import lombok.Builder;
import lombok.Data;

public class ContentDto {
	
	@Builder
	@Data
	public static class ContentList{
		List<ContentEntity> contents;
		Long maxPage;
	}
}
