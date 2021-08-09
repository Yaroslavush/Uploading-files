package com.example.uploadingfiles.parser;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import com.example.uploadingfiles.service.StringModel;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface ParsingService {

    void init();

    StringModel parse(MultipartFile file);

    Stream<Path> loadAll();

    Path load(String filename);

    Resource loadAsResource(String filename);

    void deleteAll();

}
