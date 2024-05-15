package com.example.demo.dto;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ResponseDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4136681180966716688L;
	int result;
	Object data;
}
