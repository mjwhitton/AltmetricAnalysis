
/**
 * Write a description of TestGetAltmetrics here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.io.FileReader;
import java.io.Reader;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import org.json.JSONException;
import org.json.JSONObject;
public class TestGetAltmetrics {
    
public void testBadURL()throws FileNotFoundException,IOException, JSONException {
String uri = "10.5258/SOTON/195959";
String type = "doi";  
GetAltmetrics ga = new GetAltmetrics();
PrintWriter pw = new PrintWriter(new File("test.csv"));
StringBuilder sb = ga.getAltmetricsOne(uri, type);
String output = sb.toString();
System.out.println(output);
}

public void altmetricOne () throws IOException, JSONException {
String uri = "10.1371/journal.pone.0081648";
String type = "doi";
GetAltmetrics ga = new GetAltmetrics(true);
try {
  PrintWriter output = new PrintWriter(new File("output.csv"));
  StringBuilder sb = ga.getAltmetricsOne(uri, type);
  output.write(sb.toString());
  output.close();}
catch (FileNotFoundException e) {System.err.println("Caught FileNotFoundException (URL): " + e.getMessage());}
}

public void altmetricMany() throws FileNotFoundException, IOException, JSONException {
File file = new File("input.csv");
PrintWriter output = null;
try {
  output = new PrintWriter(new File("output.csv"));
  GetAltmetrics ga = new GetAltmetrics(true);
  StringBuilder sb = ga.getAltmetricsCSV(file);
  output.write(sb.toString());
  output.close();}
catch (FileNotFoundException e) {System.err.println("Caught FileNotFoundException (URL): " + e.getMessage());}
} 

public void testReadJsonFromUrl() throws IOException, JSONException {
    GetAltmetrics ga = new GetAltmetrics();
    JSONObject json = ga.readJsonFromUrl("http://api.altmetric.com/v1/doi/10.1371/journal.pone.0081648");
    System.out.println(json.toString());
    System.out.println(json.get("title"));
    System.out.println(json.get("details_url"));
    System.out.println(json.getDouble("score"));
  }
 
public void testReadJsonArray() throws IOException, JSONException {
    GetAltmetrics ga = new GetAltmetrics();
    JSONObject json = ga.readJsonFromUrl("http://api.altmetric.com/v1/doi/10.1371/journal.pone.0081648");
    JSONObject citman = json.getJSONObject("readers");
    int citeulike = citman.getInt("citeulike");
    int mendeley = citman.getInt("mendeley");
    int connotea = citman.getInt("connotea");
    System.out.println("Number of mendeley viewers is " + mendeley);
    
  }
  
}
