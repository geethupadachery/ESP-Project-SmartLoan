package com.sjsu.esp.technocrats.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sjsu.esp.technocrats.service.SmartLoanS3BucketService;



@RestController
@RequestMapping(value = "/smartloan")
@CrossOrigin(origins = "*")
public class SmartLoanController  {
	
	@Autowired
	SmartLoanS3BucketService S3BucketService;
	
	
	
	@PostMapping("/uploadfile")
	public boolean uploadDocuments(@RequestPart(value = "file") MultipartFile file,
			@RequestPart(value = "username") String username) throws Exception{		
		
		return (boolean)this.S3BucketService.uploadFile(file, username);		
	}
	
	
	@GetMapping("/downloadfile")
	public void downloadDocuments(@RequestParam(value = "fileName") String filename,
			@RequestParam(value = "username") String username) throws Exception {
		this.S3BucketService.downloadFile(filename, username);
	}
	

	
}