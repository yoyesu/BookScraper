package com.ms.bookscraper;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;

@Service
public class Scraper implements BooksRepository{

//        @Value("#{'${website.urls}'.split(',')}")
//        List<String> urls;

        HtmlPage page;
        WebClient client = new WebClient();
        String bookTitle = "";

        private HtmlPage getWebPage(String url) throws IOException {
            client.getOptions().setCssEnabled(false);
            client.getOptions().setJavaScriptEnabled(false);
            return client.getPage(url);
        }

        public Book getBookByUrl(String url) {
            url = "https://www.epub.pub/book/" + url;
            String finalStructure = getFinalUrlStructure(url);
            //https://asset.epub.pub/epub/stunich-34.epub/text/part0034.html
            Book responseDTO = new Book();
            //Traversing through the urls
            for(int i = 1; i == i; i++){
                String part = i < 10 ? "0" + i + ".html" : "" + i + ".html";
                String changingUrl = finalStructure + part;
                if(extractDataFromTheSun(responseDTO,changingUrl) == 400){
                    break;
                }

            }


            generateFileFromBookDTO(responseDTO);
            return responseDTO;
        }

    private String getFinalUrlStructure(String url) {
        String secondUrl = generateSecondUrl(url);
        String finalUrlStructure = "";
        try {
            page = getWebPage(secondUrl);
            List<DomElement> listOfInputs = page.getElementsByTagName("input");
            for(DomElement input : listOfInputs){
                if(input.getAttribute("name").equals("assetUrl")){
                    String urlToTrim = input.getAttribute("value");
                    System.out.println("urlToTrim = " + urlToTrim);
                    int indexOfSubstring = urlToTrim.indexOf("content.opf");
                    finalUrlStructure = urlToTrim.substring(0,indexOfSubstring) + "text/part00";
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("final structure = " + finalUrlStructure);
        return finalUrlStructure;
    }

    private String generateSecondUrl(String url) {
        String secondUrl = "";
        try {
            page = getWebPage(url);
            bookTitle = page.getElementById("article-title").getVisibleText();
            List<HtmlAnchor> listOfAnchors = page.getAnchors();
            for(HtmlAnchor anchor : listOfAnchors){
                if(anchor.hasAttribute("data-readid")){
                    secondUrl = anchor.getAttribute("data-domain") + "/epub/" + anchor.getAttribute("data-readid");
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return secondUrl;
    }

    private void generateFileFromBookDTO(Book responseDTO) {
        try
        {
            PrintWriter pr = new PrintWriter(bookTitle + ".txt");

            Object[] listToArray = responseDTO.getSentences().toArray();
            for (int i = 0; i< responseDTO.getSentences().size() ; i++)
            {
                pr.println(listToArray[i]);
            }
            pr.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("No such file exists.");
        }
    }

    private int extractDataFromTheSun(Book responseDTO, String url) {

            try {
                //loading the HTML to a Document Object
                page = getWebPage(url);

                //Selecting the element which contains the ad list
                List<DomElement> textBoxes = page.getElementsByTagName("p");


                //traversing through the elements
                for (DomElement p: textBoxes) {
                    responseDTO.getSentences().add(p.getVisibleText());
                }

            } catch (IOException | FailingHttpStatusCodeException ex) {
                return 400;
            }
        return 200;
        }

}
