package com.nt.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;

@Service
public class AwsS3Service {
	@Autowired
	private AmazonS3 s3Client;
	@Value("${s3.bucket.name}")
	private String s3BucketName;

	public String uploadFile(MultipartFile file) throws Exception {
		File fileObj = convertMultiPartFileToFile(file);
		String fileName = file.getOriginalFilename();
		s3Client.putObject(new PutObjectRequest(s3BucketName, fileName, fileObj));
		fileObj.delete();
		return "file uploaded " +fileName;

	}

	private File convertMultiPartFileToFile(MultipartFile multipartFile) throws Exception {
		File file = new File(multipartFile.getOriginalFilename());
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(multipartFile.getBytes());
		fos.close();
		return file;
	}

	
	 public byte[] downloadFile(String fileName) {
	        S3Object s3Object = s3Client.getObject(s3BucketName, fileName);
	        S3ObjectInputStream inputStream = s3Object.getObjectContent();
	        try {
	            byte[] content = IOUtils.toByteArray(inputStream);
	            return content;
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        return null;
	    }
	
	public String deleteObject(String file) {
		s3Client.deleteObject(s3BucketName,file);
		return file+ " removed success";
		
	}
}
