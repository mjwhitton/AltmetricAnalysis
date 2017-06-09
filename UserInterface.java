
/**
 * Write a description of main here.
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
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

public class UserInterface extends JPanel {
 
private JFileChooser fc;
private JFrame frame;
boolean testMode;

public UserInterface() throws FileNotFoundException, IOException, JSONException {
testMode = false;
createFrame();
            }

public UserInterface(boolean isTesting) throws FileNotFoundException, IOException, JSONException {
if (isTesting == true) {testMode = true;} else {testMode = false;}
createFrame();
            }
            
private void createFrame() {
//Create a file chooser
fc = new JFileChooser();
//Add the CSV File Filter
FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV files", "csv");
fc.addChoosableFileFilter(filter);
fc.setAcceptAllFileFilterUsed(false);
//Create a JPanel
JPanel buttonPanel = new JPanel(); //use FlowLayout   
}
            
public void openFile() throws FileNotFoundException, IOException, JSONException {
   
int returnVal = fc.showOpenDialog(UserInterface.this);
File file = null;

if (returnVal == JFileChooser.APPROVE_OPTION) {
      file = fc.getSelectedFile();
      System.out.println("Opening: " + file.getName() + "." + "\n");}
    else {System.out.println("Open command cancelled by user." + "\n");}

PrintWriter output = null;
try {
  output = new PrintWriter(new File("output.csv"));
  GetAltmetrics ga = new GetAltmetrics(testMode);
  StringBuilder sb = ga.getAltmetricsCSV(file);
  output.write(sb.toString());
  output.close();}
catch (FileNotFoundException e) {System.err.println("Caught FileNotFoundException (URL): " + e.getMessage());} 
   
    }

}
