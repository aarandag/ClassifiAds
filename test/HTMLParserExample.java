/**
 * Example of retrieving information from the web
 */

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author Alberto Aranda García y Cristian Gómez Portes
 */
public class HTMLParserExample {
 public static void main(String[] args) {
  // TODO Auto-generated method stub
  Document doc;
  try {
   // need http protocol
   doc = Jsoup.connect("http://google.com").get();
   
   // get the page title
   String title = doc.title();
   System.out.println("title: " + title);
   
   // get all links
   Elements links = doc.select("a[href]");
   for(Element link : links)
   {
    // get the value from href attribute
    System.out.println("\nlink: " + link.attr("href"));
    System.out.println("text: " + link.text());
   }
  }catch(IOException e)
  {
   e.printStackTrace();
  }
 }

}
