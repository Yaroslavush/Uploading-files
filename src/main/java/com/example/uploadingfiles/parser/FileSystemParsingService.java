package com.example.uploadingfiles.storage;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

@Service
public class FileSystemParsingService implements StorageService {

    private final Path rootLocation;

    @Autowired
    public FileSystemParsingService(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());
    }

   // @Override
   // public void store(MultipartFile file) {
   //     try {
   //         if (file.isEmpty()) {
   //             throw new StorageException("Failed to store empty file.");
   //         }
   //         Path destinationFile = this.rootLocation.resolve(
   //                 Paths.get(file.getOriginalFilename()))
   //                 .normalize().toAbsolutePath();
   //         if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
   //             // This is a security check
   //             throw new StorageException(
   //                     "Cannot store file outside current directory.");
   //         }
   //         try (InputStream inputStream = file.getInputStream()) {
   //             Files.copy(inputStream, destinationFile,
   //                     StandardCopyOption.REPLACE_EXISTING);
   //         }
   //     }
   //     catch (IOException e) {
   //         throw new StorageException("Failed to store file.", e);
   //     }
   // }

    @Override
    public void parse(MultipartFile file) {
        PDFParser parser = new PDFParser();
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        ParseContext context = new ParseContext();
        try{
        InputStream inputstream = file.getInputStream();

        parser.parse(inputstream, handler, metadata, context);
       // System.out.println("File content : " + handler.toString());
        } catch (IOException e){

        }catch (SAXException n){

        }catch (TikaException n){

        }
    }




    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        }
        catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }

    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new StorageFileNotFoundException(
                        "Could not read file: " + filename);

            }
        }
        catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

   @Override
   public void deleteAll() {
       FileSystemUtils.deleteRecursively(rootLocation.toFile());
   }

   @Override
   public void init() {
       try {
           Files.createDirectories(rootLocation);
       }
       catch (IOException e) {
           throw new StorageException("Could not initialize storage", e);
       }
   }
}
