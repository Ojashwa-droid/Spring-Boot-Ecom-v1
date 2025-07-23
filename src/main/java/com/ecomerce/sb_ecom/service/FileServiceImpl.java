package com.ecomerce.sb_ecom.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService{

    @Override
    public String uploadImage(String path, MultipartFile file) throws IOException {

//        // Get the complete file name - from input
//        String originalFileName = file.getOriginalFilename();
//
//        // Generate a unique file name, so we don't have to deal with any conflicts occurring on the server
//        String randomId = UUID.randomUUID().toString();
//        // get the new file name and path as well
//        String filename = randomId.concat(originalFileName.substring(originalFileName.lastIndexOf('.')));
//        String filePath = path + File.separator + filename;
//
//        // Now upload the image to the server
//
//        File folder = new File(path);
//
//        if (!folder.exists()) {
//            folder.mkdir();
//        }
//
//        Files.copy(file.getInputStream(), Paths.get(filePath));
//        return filename;


        // Get the file names of current / original file
        String originalFileName = file.getOriginalFilename();

        // Generate a unique file name
        String randomId = UUID.randomUUID().toString();
        // mat.jpg ---> 1234 ---> 1234.jpg (new file name)
        String fileName = randomId.concat(originalFileName.substring(originalFileName.lastIndexOf('.')));
        String filePath = path + File.separator + fileName;

        // check if path exists and create
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdir();
        }

        // Upload to server
        Files.copy(file.getInputStream(), Paths.get(filePath));
        // the received file is supposed to go to the file-path, that particular folder in the server

        // Returning file name
        return fileName;
    }
}
