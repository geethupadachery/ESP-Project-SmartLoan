package com.sjsu.esp.technocrats.service;



import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Service
public class SmartLoanS3BucketService {

	@Value("${cloud.aws.credentials.accessKey}")
	private String key;

	@Value("${cloud.aws.credentials.secretKey}")
	private String secretKey;

	private S3Client s3Client;

	private AmazonS3 s3;

	private static final String AWS_BUCKET = "esp-project-smartloan";

	private static final String AWS_BUCKET_CDN = "https://cloud-project-studentapp.s3-us-west-1.amazonaws.com";

	@PostConstruct
	public void initialize() {

		AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(key, secretKey);

		s3Client = S3Client.builder().credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials))
				.region(Region.US_WEST_2).build();

		AWSCredentials cred = new BasicAWSCredentials(key, secretKey);
		s3 = new AmazonS3Client(cred);
	}

	public boolean uploadFile(MultipartFile multipartFile, String username) {
		Boolean result= false;
		try {
			System.out.println("inside uploadFile");
			System.out.println("username is " + username);
			File file = convertMultiPartToFile(multipartFile);
			String fileName = generateFileName(multipartFile);
			result = uploadFileTos3bucket(fileName, file, username);
			file.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;

	}

	private File convertMultiPartToFile(MultipartFile file) throws IOException {
		File convFile = new File(file.getOriginalFilename());
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(file.getBytes());
		fos.close();
		return convFile;
	}

	private String generateFileName(MultipartFile multiPart) {
		return multiPart.getOriginalFilename();
	}

	private boolean uploadFileTos3bucket(String fileName, File file, String username) {
		PutObjectResult putObject = s3.putObject("esp-project-smartloan", username + "/" + fileName, file);
		System.out.println("uploadFileTos3bucket");
		System.out.println(putObject.getETag());
		System.out.println(putObject.getContentMd5());
		System.out.println(putObject.getVersionId());
		if(putObject.getETag()!=null)
		{System.out.println("Uploaded Successfully");
			return true;
		}
		else 
			return false;
	}


	public void downloadFile(String fileName, String username) {
		System.out.println("Inside Download File");
		File file;
		Download download = null;
		try {

			file = File.createTempFile("tempFile", null);

			TransferManager tm = TransferManagerBuilder.standard().withS3Client(s3).build();

			// download the object to file
			download = tm.download(AWS_BUCKET, username + "/" + fileName, file);

			// block until download finished download.waitForCompletion();
			tm.shutdownNow();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (AmazonServiceException e) {
			e.printStackTrace();
		} catch (AmazonClientException e) {
			e.printStackTrace();
		}
	}
}
