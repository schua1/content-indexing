package org.deshang.content.indexing;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class GoogleTranslateQuerier {

    private static final String TERMS_FILENAME = "terms_categorization/terms.txt";
    private static final String GOOGLE_TRANSLATE_BASIC_URL = "http://translate.google.cn/translate_a/single?client=t&sl=zh-CN&tl=en&hl=zh-CN&dt=bd&dt=ex&dt=ld&dt=md&dt=qc&dt=rw&dt=rm&dt=ss&dt=t&dt=at&ie=UTF-8&oe=UTF-8&orig=shangchuan&corrtype=6&prev=tws_spell&ssel=5&tsel=5&tk=518290|1006108";

    public static void main(String[] args) throws IOException, URISyntaxException {
        new GoogleTranslateQuerier().processTermsFile();

    }

    private void processTermsFile() throws FileNotFoundException, IOException, UnsupportedEncodingException,
            MalformedURLException, ProtocolException {
        List<String> terms = readTermsFrmFile(TERMS_FILENAME);

        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
        
        GoogleTranslateJsonParser parser = new GoogleTranslateJsonParser();

        for (int i = 0; i < terms.size(); i++) {
            String term = terms.get(i);
            HttpURLConnection conn = openGoogleTranslateConnection(term);
            handleResponse(conn, parser, new PrintWriter(System.out));

            // pause for 1.5 second per request
            try {
                Thread.sleep(1500L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private List<String> readTermsFrmFile(String fileName) throws FileNotFoundException, IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
        List<String> terms = new ArrayList<String>();
        String line = null;
        while ((line = reader.readLine()) != null) {
            terms.add(line);
        }
        reader.close();
        return terms;
    }

    private HttpURLConnection openGoogleTranslateConnection(String string) throws UnsupportedEncodingException,
            MalformedURLException, IOException, ProtocolException {

        String basicUrlString = GOOGLE_TRANSLATE_BASIC_URL;
        basicUrlString += "&q=" + URLEncoder.encode(string, "UTF-8");

        URL url = new URL(basicUrlString);
        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
        httpConnection.setRequestMethod("GET");
        httpConnection.setRequestProperty("Host", "translate.google.cn");
        httpConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:35.0) Gecko/20100101 Firefox/35.0");
        httpConnection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        httpConnection.setRequestProperty("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
        httpConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");
        httpConnection.setRequestProperty("Referer", "http://translate.google.cn/");
        httpConnection.setRequestProperty("Cookie", "PREF=ID=991f3b0c12fa3789:U=9229a389010ba165:NW=1:TM=1421720413:LM=1421991892:S=oWkjS04je6fwYVeB; NID=67=eP702XiBzw-z0WHS8We4qjku_p5C70XoqzcUeb63X5oETU5Bw7ekb_JQRqT67XTibQYf6q_VZJimojKu9aYn-lJy4wyQ3y1OLlC8i_PzksCWTzD92R1ONLWR09aW4qqC; _ga=GA1.3.333895060.1421720385");
        httpConnection.setRequestProperty("Connection", "keep-alive");
        httpConnection.setDoOutput(true);
        return httpConnection;
    }

    private void handleResponse(HttpURLConnection conn, GoogleTranslateJsonParser parser, PrintWriter writer)
            throws IOException {
        int code = conn.getResponseCode();

        if (code == HttpURLConnection.HTTP_OK) {
            String jsonString = null;
            GZIPInputStream gzipIn = new GZIPInputStream(conn.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(gzipIn));
            while ((jsonString = reader.readLine()) != null) {
                List<Object> elements = parser.parse(jsonString);
                if (elements.size() > 1) {
                    List<Object> originals = (List<Object>) elements.get(0);
                    if (originals != null && originals.size() > 0) {
                        List<Object> original = (List<Object>) originals.get(0);
                        if (original != null && original.size() > 1) {
                            Object word = original.get(1);
                            writer.print("Original Words: " + word + "\ttranslations:");
                        }
                    }

                    if (elements.get(1) instanceof List) {
                        List<Object> translations = (List<Object>) elements.get(1);
                        for (Object translation : translations) {

                            if (translation != null && ((List<Object>) translation).size() > 0) {
                                writer.print(((List<Object>) translation).get(0) + ", ");
                            }
                        }
                    } else {
                        writer.print("Not Classified");
                    }
                    writer.println();

                }
            }
            reader.close();
        }
    }
}
