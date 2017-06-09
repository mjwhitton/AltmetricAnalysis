
/**
 * Write a description of Altmetric here.
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

public class GetAltmetrics {
//Declaration of private variables - see constructor & buildMaps for details   
private JSONObject error;
private PrintStream errorLog;
private StringBuilder outputSB;
private String[] titleList;
private String[] nameList;
private String[] typeList;


public GetAltmetrics () throws FileNotFoundException,IOException, JSONException, NullPointerException {
//Create an JSONObject to pass if uri is not found
error = new JSONObject("{ \"Error\":\"Bad URL\" }");
// Set Error and System logs to go to Errors.txt
errorLog = new PrintStream("Errors.txt");  
System.setOut(errorLog);
System.setErr(errorLog);  
//Create the StringBuilder that will become the output file
outputSB = new StringBuilder();
//Import config files into Arrays
try {buildMaps();}
catch (FileNotFoundException e) {System.err.println("Caught FileNotFoundException (URL): " + e.getMessage());}
}

public GetAltmetrics (boolean isTesting) throws FileNotFoundException,IOException, JSONException, NullPointerException {
//Create an JSONObject to pass if uri is not found
error = new JSONObject("{ \"Error\":\"Bad URL\" }");
// Set Error and System logs to go to Errors.txt
if (isTesting==false)
  {errorLog = new PrintStream("Errors.txt");  
  System.setOut(errorLog);
  System.setErr(errorLog);}
//Create the StringBuilder that will become the output file
outputSB = new StringBuilder();
//Import config files into Arrays
try {buildMaps();}
catch (FileNotFoundException e) {System.err.println("Caught FileNotFoundException (URL): " + e.getMessage());}
}

private void buildMaps() throws FileNotFoundException, IOException {
//Parse config file to find out how many rows are needed.
int numHeaders = 0;
//Create a variable to track errors
boolean isError = true;
CSVParser parser = new CSVParser(
  new FileReader("config/config.csv"), 
  CSVFormat.DEFAULT.withHeader());
    for (CSVRecord record : parser) {
      //Check required header are there
      if (record.isMapped("name") && record.isMapped("title") && record.isMapped("type"))
        {isError = false;
        String name = record.get("name");}
      numHeaders +=1;
      }
  parser.close(); 

if (isError == false) {
//If no error Create three arrays to store the configuration information
titleList = new String[numHeaders];
typeList = new String[numHeaders];
nameList = new String[numHeaders];
//Re-parse the config file and populate the arrays
CSVParser parser2 = new CSVParser(
  new FileReader("config/config.csv"), 
  CSVFormat.DEFAULT.withHeader());
  int i=0;
    for (CSVRecord record : parser2) {
      String title = record.get("title");
      String name = record.get("name");
      String type = record.get("type");

      titleList[i] = title;
      nameList[i] = name;
      typeList[i] = type;
      i+=1;
      }
   parser2.close();
}
else {
//If an error is present add a message to the arrays
titleList = new String[]{"error"};
typeList = new String[]{"error"};
nameList = new String[]{"error"};}
}

public StringBuilder getAltmetricsOne (String uri, String type) throws FileNotFoundException,IOException, JSONException, NullPointerException {
//Make sure Output StringBuilder is empty
outputSB = new StringBuilder();
//Make sure type is lowercase
type = type.toLowerCase();
//Add headings to Output StringBuilder 
addHeader();
//If no error
if (nameList.equals("error")) {
// Extract metrics
extractJson(uri, type);}
//If an error add a message
else {outputSB.append("Error - required column headings in config file are missing");}
// Return Output StringBuilder
return outputSB;   
}

public StringBuilder getAltmetricsCSV (File file) throws FileNotFoundException,IOException, JSONException, NullPointerException {
//Make sure Output StringBuilder is empty
outputSB = new StringBuilder();
//Create variable to track errors
int isError = 0;
//Add headings to Output StringBuilder 
addHeader();
//Parse input Spreadsheet to get uri and type values
CSVParser parser = new CSVParser(
  new FileReader(file), 
  CSVFormat.DEFAULT.withHeader());
    for (CSVRecord record : parser) {
      //Check required column headings are present
      if (nameList[0].equals("error")) {isError = 1;}
      else if (record.isMapped("uri") && record.isMapped("type")) {
        String uri = record.get("uri");
        //Make sure type is lowercase
        String type = record.get("type").toLowerCase();
        // Extract metrics
        extractJson(uri, type);}
      //If not set error variable to true
      else {isError = 2;}
      }
  parser.close();
//If required column headings are missing add the error message
if (isError==1) {outputSB.append("Error - required column headings in config file are missing");}
if (isError==2) {outputSB.append("Error - required column headings of uri and type are missing");}
// Return Output StringBuilder
return outputSB;   
}

private void addHeader() throws FileNotFoundException,IOException, JSONException, NullPointerException {
//Add the headers for the two input values
outputSB.append("uri,");
outputSB.append("type,");
//Iterate over the title list
for (int i=0; i < titleList.length; i++)
  {String title = titleList[i];
  //Add title and a comma
  outputSB.append(title+",");}
//Add an end of row
outputSB.append("\n");   
}

private String readAll(Reader rd) throws IOException {
//Create a temporary Stringbuilder Object
StringBuilder sb = new StringBuilder();
int cp;
//Read in one character at a time
while ((cp = rd.read()) != -1) {
  //Append to the StringBuilder
  sb.append((char) cp);
  }
//Convert to a string and return
return sb.toString();
  }

public JSONObject readJsonFromUrl(String url) throws FileNotFoundException,IOException, JSONException, NullPointerException {
//Create null version of key objects
InputStream is = null;
JSONObject json = null;
//Open url, if this is not found print the error message
try{
  is = new URL(url).openStream();}
catch (FileNotFoundException e) {System.err.println("Caught FileNotFoundException (URL): " + e.getMessage());}
//If no url found set json to the error object
if (is==null) {json = error;}
else {      
  try {
    //Read in the url
    BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
    //Call readAll to convert to a string
    String jsonText = readAll(rd);
    //Convert String into a new Json objec
    json = new JSONObject(jsonText);
    //Stop reading the url
    is.close();}
  //If there is an error print the message
  catch (NullPointerException e) {System.err.println("Caught: NullPointerException" + e.getMessage());}
}    
//Return the json object
return json;        
    }
    
private String appendDQ(String str) {
//Add double quotes to the input string
return "\"" + str + "\"";
}
  
private void extractNestedJson(JSONObject json, String name)
throws IOException, JSONException
{
//Extract the sub-json object
JSONObject subJson = json.getJSONObject(name);
//iterate over the nameList array
for (int i = 0; i < nameList.length; i++)
  {//Get the corrosponding type and name
  String curtype = typeList[i];
  String CurName = nameList[i];
  //Append int value and a comma, only if it is part of the sub-object. These values should have a curtype of [subobject name].type
  if (subJson.has(CurName) && curtype.equals(name+".int")) {outputSB.append(subJson.getInt(CurName)+",");}
  //Append n/a and a comma for any missing values that should be in the sub-object
  else if (curtype.startsWith(name)) {outputSB.append("n/a,");}    
  }
   }

private void extractJson(String uri, String type)
throws IOException, JSONException
{
 //Create the uri to pass to the api
 String url = "http://api.altmetric.com/v1/" + type + "/" + uri;
 // Extract the resulting json
 JSONObject json = readJsonFromUrl(url);
 //Append to two imput values to the output StringBuilder
 outputSB.append(uri +",");
 outputSB.append(type +",");
 //Iterate over the nameList array of all the metrics to look for
 for (int i=0; i < nameList.length; i++)
   {//Get the corrosponding type and name
   String curtype = typeList[i];
   String curName = nameList[i];
   //Get string values and append them with double quotes, and add a comma
   if (json.has(curName) && curtype.equals("string")) {outputSB.append(appendDQ(json.getString(curName))+",");}
   // If url was not found (error json recieved) append Error and a comma
   else if (json.has("Error")) {outputSB.append("Error"+",");}
   //Get double values and append them and a comma
   else if (json.has(curName) && curtype.equals("double")) {outputSB.append(json.getDouble(curName)+",");}
   //Get integer values and append them and a comma
   else if (json.has(curName) && curtype.equals("int")) {outputSB.append(json.getInt(curName)+",");}
   //Append n/a and a comma if that metric was not found. Unless it's part of the readers sub-object
   else if (!curtype.startsWith("readers")) {outputSB.append("n/a,");}
   }
//Extract readers sub-object
if(json.has("readers")){extractNestedJson(json, "readers");}
//Add an end of row
outputSB.append('\n');        
  }
  
}