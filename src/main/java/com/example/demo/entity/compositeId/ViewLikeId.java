package com.example.demo.entity.compositeId;


import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Data;

@Data
@Embeddable
@Builder
public class ViewLikeId {
	Long accountId;
	Long contentId;
}
