package com.rucsok.home.view.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CsrfRefreshController {

	@ResponseBody
	@RequestMapping(value = "/", method = RequestMethod.HEAD)
	public void index() {
	}
	
}