package com.example.demo.util;

import org.springframework.stereotype.Component;

@Component
public class TranslateUtil {


	public String translateToEng(String text)  {
		try {
			return "";
		}catch (Exception e) {
			e.printStackTrace();
			return "<>";
		}
        
    }
}
