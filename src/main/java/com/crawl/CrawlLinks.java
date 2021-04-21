package com.crawl;

import com.common.RetrievedLinks;
import com.common.StatementResult;
import com.fileOperations.TextFileWriter;
import com.sentiments.analyzers.SentimentsCompute;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.SQLOutput;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

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
                boolean isFoundAny = false;
                String[] keywords = searchKeyword.split(" ");
                for (String keyword : keywords) {
                    if (StringUtils.containsIgnoreCase(absURL, keyword)) {
                        isFoundAny = true;
                        break;
                    }
                }
                if (isFoundAny
                        && !(absURL.equals(link.baseUri()))
                        && absURL.lastIndexOf(":") == 5
                        && !absURL.contains("#")
                        && !absURL.equals(url)
                        && !absURL.endsWith(".jpeg")) {
                    retlinks.setURL(absURL);
                    retlinks.setDepth(depth + 1);
                    links.add(retlinks);
                    System.out.println("absURL: " + absURL);
                }
            }

            Iterator<RetrievedLinks> iterator = links.iterator();
            //only picking first few links for testing
            List<Callable<Boolean>> tasks = new ArrayList<>(4);
            CompletableFuture cf=null;
            for (int i = 0; i < 3 && i < links.size(); i++) {
                cf=CompletableFuture.runAsync(()->searchForWord(iterator.next(), searchKeyword));
            }
            cf.get();
            return true;
        } catch (IOException ioe) {
            System.out.println("We were not successful in our HTTP request");
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Boolean searchForWord(RetrievedLinks retrievedLinks, String searchKeyword) {
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
        getSentimentsScoreAverageAndWriteToFile(bodyText, retrievedLinks, searchKeyword);
        System.out.println(bodyText);
        return true;
    }

    private void getSentimentsScoreAverageAndWriteToFile(String bodyText, RetrievedLinks retrievedLinks, String searchKeyword) {
        SentimentsCompute sentimentsCompute = new SentimentsCompute();
        try {
            System.out.println("bodyText: " + bodyText);
            System.out.println(":::::::::::::::::::");
            List<StatementResult> statementResults = sentimentsCompute.getSentimentsScore(bodyText, retrievedLinks.getURL(), searchKeyword);
            if (statementResults.size() > 0)
                statementResults.get(0).setRelevantText(bodyText);
            TextFileWriter.writeTofile(statementResults, statementResults.get(0).getHighestPolarityOfPage() + LocalDate.now() + "_" + LocalDateTime.now().getNano() + "_Sentiments_results.txt");
            System.out.println("created file");
            if (!statementResults.get(0).getHighestPolarityOfPage().equalsIgnoreCase("NEGATIVE")) {
                TextFileWriter.writeTofile(statementResults, statementResults.get(0).getHighestPolarityOfPage() + LocalDate.now() + "_" + LocalDateTime.now().getNano() + "_Sentiments_results.txt");
                System.out.println("created file");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {

                }
            }
        } catch (IOException e) {
            System.out.println("Cannot compute sentiments! ");
        }
    }

    public static void main(String[] args) {
        new CrawlLinks().crawl("https://timesofindia.indiatimes.com/india", 0, "covid vaccine");
    }
}
