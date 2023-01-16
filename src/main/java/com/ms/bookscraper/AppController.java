package com.ms.bookscraper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
public class AppController {

    @Autowired
    BooksRepository booksRepository;

    @GetMapping("/")
    public String showWelcomeMessage(){
        return "Introduce url to download ebook";
    }

    @GetMapping(path = "/{url}")
    public Book getBook(@PathVariable String url) {
        return  booksRepository.getBookByUrl(url);
    }
}
