package com.example.uploadingfiles.parser;


import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;
import com.example.uploadingfiles.service.StringModel;
import java.util.StringTokenizer;


@Service
public class FileSystemParsingService implements ParsingService {

    private final Path rootLocation;

    @Autowired
    public FileSystemParsingService(ParsingProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());
    }


    @Override
    public StringModel parse(MultipartFile file) {
        PDFParser parser = new PDFParser();
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        ParseContext context = new ParseContext();
        StringModel model = new StringModel();
        try{
        InputStream inputstream = file.getInputStream();

        parser.parse(inputstream, handler, metadata, context);
        String receivedText = handler.toString();
        model.setReceivedText(receivedText);


        int beginningIndex = receivedText.indexOf("1.");
        int endIndex = receivedText.indexOf("2.");
        String definitions = receivedText.substring(beginningIndex , endIndex);
        model.setDefinitions(definitions);

        } catch (IOException e){

        }catch (SAXException n){

        }catch (TikaException n){

        }
        return model;
    }




    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        }
        catch (IOException e) {
            throw new ParsingException("Failed to read stored files", e);
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
           throw new ParsingException("Could not initialize storage", e);
       }
   }
}
