package com.crawl;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class CrawlLinks {
    // We'll use a fake USER_AGENT so the web server thinks the robot is a normal web browser.
    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:47.0) Gecko/20100101 Firefox/47.0";

    private HashSet<RetrievedLinks> links = new LinkedHashSet<>(); // Just a list of URLs
    private Document htmlDocument;

    public boolean crawl(String url, int depth, String searchKeyword) {
        try {
            Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);
            Document html = connection.get();
            this.htmlDocument = html;

            if (connection.response().statusCode() == 200) // 200 is the HTTP OK status code indicating that everything is great.
            {
                System.out.println("\n**Visiting** Received web page at " + url);
            }
            if (!connection.response().contentType().contains("text/html")) {
                System.out.println("**Failure** Retrieved something other than HTML");
                return false;
            }
            //selecting all links of given url
            Elements linksOnPage = html.select("a[href]");
            System.out.println("Found (" + linksOnPage.size() + ") links");
            // System.out.println(linksOnPage);
            System.out.println(":::::::::::::::::::::::::::::::::::::::");
            //iterating each links
            for (Element link : linksOnPage) {
                RetrievedLinks retlinks = new RetrievedLinks();
                String absURL = link.absUrl("href");
                if (absURL.contains("kashmir") || absURL.contains("jammu") || absURL.contains("Kashmir") || absURL.contains("Jammu")
                        && !(absURL.equals(link.baseUri()))
                        && absURL.lastIndexOf(":") == 5
                        && !absURL.contains("#")
                        && !absURL.equals(url)
                        && !absURL.endsWith(".jpeg")) {
                    retlinks.setURL(absURL);
                    retlinks.setDepth(depth + 1);
                    links.add(retlinks);
                }
            }
            for (RetrievedLinks link : links) {
                searchForWord(link, searchKeyword);
            }
            return true;
        } catch (IOException ioe) {
            System.out.println("We were not successful in our HTTP request");
            return false;
        }
    }

    public boolean searchForWord(RetrievedLinks retrievedLinks, String searchKeyword) {
        Connection connection1 = Jsoup.connect(retrievedLinks.getURL()).userAgent(USER_AGENT);
        Document html1 = null;
        try {
            html1 = connection1.get();
        } catch (IOException e) {
            System.out.println("page text not retrieved !!");
        }
        if (html1 == null) {
            System.out.println("ERROR! Call crawl() before performing analysis on the document");
            return false;
        }
        System.out.println("Searching for the word " + retrievedLinks.getURL() + "...");
        String bodyText = html1.body().text();
        String[] bodyTextLines = bodyText.split("/./ ");
        System.out.println(bodyTextLines[0]);
        return true;
    }

    public static void main(String[] args) {
        new CrawlLinks().crawl("https://www.hindustantimes.com/india-news", 0, "kashmir");
    }
}
