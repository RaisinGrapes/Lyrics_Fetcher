package web_scraper;

import java.lang.String;
import java.util.Scanner;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

public class Web_Scraper {
    
    // creates the second half of the url which displays the search results of the song
    
    public static String toSearch(String s){
        String alphanumeric = "QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm0123456789";
        String search = "";
        for (int i = 0; i != s.length(); i += 1){
            
            char ele = s.charAt(i);
            String string_char = String.valueOf( ele );
            
            if ( alphanumeric.contains( string_char ) ){  // is the character is alphanumeric, just add it to the string
                search += string_char;
            }
            else if (ele == ' '){  // add '+' whenever there is a space
                search += "+";
            }
            else{  // for non-alphanumeric characters, append '%' + the hexadecimal ascii of the character
                // we find the decimal value, use toHexString to find the hexadecimal value and finally upper case the hex value
                String ascii_hexvalue = Integer.toHexString( (int)ele ).toUpperCase();
                search += "%" + ascii_hexvalue;
            }
            
        }
        return search;
    }
    
    // method to change raw html to remove tags etc and acknowledge <br> & <p> tags, by appending or prepending '\n'
    public static String removeTags(String html) {
        Document doc = Jsoup.parse(html);
        doc.outputSettings(new Document.OutputSettings().prettyPrint(false)); // makes the html() preserve line breaks
        doc.select("br").append("\\n");  // adds '\\n' to the end of <br>, meaning new line
//        doc.select("p").prepend("\\n\\n");  // adds '\\n\\n' to the end of <p>, meaning starting from the next paragraph 
        String s = doc.html().replaceAll("\\\\n", "\n");
        return Jsoup.clean(s, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));  // cleans the html of all html tags and stuff, leaving behind only the text and new lines
    }
    
    

    public static void main(String[] args) {
        
        Scanner sc = new Scanner(System.in);
        
        System.out.print("Please enter the name of the song: ");
        String song = sc.nextLine();
        System.out.print("Please enter the name of the artist: ");
        String artist = sc.nextLine();
        // the url of the page which shows the results of the song
        String search = "https://search.azlyrics.com/search.php?q=" + toSearch(song + " " + artist);        
        
        try{
            
            Document doc_search = Jsoup.connect(search).get(); // creates a document of the search result webpage            
            String url_lyrics = "";
            for (Element row: doc_search.select("table.table.table-condensed tr")){  // looping through the list of lyrics links
                if (row.select("b").text().toLowerCase().equals((song + " " + artist).toLowerCase())){
                    url_lyrics = row.select("a").attr("href");  // extracting the link belonging to the attribute "href" which is in the tag <a>
                    break;
                }
             }
            // actual webpage of the lyrics url
            Document doc_lyrics = Jsoup.connect(url_lyrics).get();  // document of the actual lyrics webpage 
            System.out.println(removeTags(String.valueOf(doc_lyrics.select("div:nth-of-type(5)"))));  // the element containing the lyrics of the doc selected, formatted, and printed
            
        }
        catch( Exception e ) { System.err.printf("Could not find %s by %s\nPlease check the song title and artist once more."); }
    }
    
    
}
