package com.custodix.insite.local.ehr2edc.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class EHR2EDCController {

	@RequestMapping(value = "/app/ehr2edc/studies")
	public String index() {
		return "ehr2edc";
	}

	@GetMapping(value = "/app/ehr2edc/studies/**")
	String forwardAppRoutes(Model model) {
		return "forward:/app/ehr2edc/studies/";
	}
}