package edu.escuelaing.arem.ASE.app;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Unit test for simple App.
 */
public class AppTest
        extends TestCase
{
    private HttpConnectionExample apiConnectionToTry;
    private static ExecutorService executorService;

    /**
     * Create the test case
     *
     * @param httpConnectionExample name of the test case
     */
    public AppTest( String httpConnectionExample )
    {
        super( httpConnectionExample );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    protected void setUp() {
        apiConnectionToTry = new HttpConnectionExample();
        executorService = Executors.newSingleThreadExecutor();
    }

    protected void tearDown() {
        executorService.shutdownNow();
    }

    public void testNonExistentMovie()
    {
        StringBuffer wrongRequest;
        try {
            wrongRequest = apiConnectionToTry.getMovieInfo("");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertEquals("{\"Response\":\"False\",\"Error\":\"Incorrect IMDb ID.\"}", wrongRequest.toString());
    }

    public void testExistentMovie()
    {
        StringBuffer correctRequest;
        try {
            correctRequest = apiConnectionToTry.getMovieInfo("Betty la fea");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertEquals("{\"Title\":\"Yo soy Betty, la fea\",\"Year\":\"1999–2001\",\"Rated\":\"TV-PG\",\"Released\":\"25 Oct 1999\",\"Runtime\":\"30 min\",\"Genre\":\"Comedy, Drama, Romance\",\"Director\":\"N/A\",\"Writer\":\"N/A\",\"Actors\":\"Ana María Orozco, Jorge Enrique Abello, Natalia Ramírez\",\"Plot\":\"An outcast in a prominent fashion company, a sweet-hearted and unattractive assistant falls hopelessly in love with her boss.\",\"Language\":\"Spanish\",\"Country\":\"Colombia\",\"Awards\":\"5 wins\",\"Poster\":\"https://m.media-amazon.com/images/M/MV5BM2U4YzM5ZTItNGFmZi00MDRhLTgyMjUtZDJjMTI2ZmU5ZjNlXkEyXkFqcGdeQXVyMTcxNTYyMjM@._V1_SX300.jpg\",\"Ratings\":[{\"Source\":\"Internet Movie Database\",\"Value\":\"8.3/10\"}],\"Metascore\":\"N/A\",\"imdbRating\":\"8.3\",\"imdbVotes\":\"3,775\",\"imdbID\":\"tt0233127\",\"Type\":\"series\",\"totalSeasons\":\"1\",\"Response\":\"True\"}", correctRequest.toString());
    }

    public void testHttpFileSearcher() throws IOException {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    MyWebServices.main(new String[]{});
                } catch (IOException | URISyntaxException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        String USER_AGENT = "Mozilla/5.0";
        String GET_URL = "http://localhost:35000/index.html";
        String finalUrl = GET_URL;
        URL obj = new URL(finalUrl);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);

        //The following invocation perform the connection implicitly before getting the code
        int responseCode = con.getResponseCode();
        System.out.println("GET Response Code :: " + responseCode);

        //Answer
        StringBuffer response = new StringBuffer();
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        }

        //Return result
        assertTrue(response.toString().contains("<!DOCTYPE html><html><head>"));
        executorService.shutdownNow();
    }

}
