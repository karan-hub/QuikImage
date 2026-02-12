package com.image.quickimage.image.infrastructure;

import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class StorageService {
    public  boolean exists(String filename , Path targetPath) {
        if (Files.exists(targetPath)){
            System.out.println("Cache Hit! ");
             return  true;
        }else {
            return  false;
        }
    }


// Path Handling
    public  Path getTargetPath(String filename , String location) throws FileNotFoundException {
        try{
            Path path = Paths.get(location);
            Files.createDirectories(path);
             return path.resolve(filename);

        }catch (Exception e){
            throw  new FileNotFoundException("File not Found");
        }
    }

//    read(String filename)
//    and save(String filename, byte[] content)
}
