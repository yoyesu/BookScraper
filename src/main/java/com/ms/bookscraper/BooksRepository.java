package com.ms.bookscraper;

import java.util.Set;

public interface BooksRepository {

    Book getBookByUrl(String url);
}
