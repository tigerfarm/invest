package tfpinvest;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class readAccountTextFile {

    private final String SOURCEDIRECTORY = "tableData";
    private final String SEPARATOR = "|";
    //
    static Format formatter = new SimpleDateFormat("YYYY-MM-dd");
    private static String theDateToday;

    public int runReset() {
        int rowCount = 0;
        String currentDirectory = System.getProperty("user.dir");
        // Subdirectory to the current directory.
        String theDirectoryName = currentDirectory + "/" + SOURCEDIRECTORY;
        String theReadFilename = theDirectoryName + "/" + "tableAccount.txt";
        try {
            // Read row data from a text file.
            File readFile = new File(theReadFilename);
            if (!readFile.exists()) {
                System.out.println("-- ** ERROR, theReadFilename does not exist.");
                return rowCount;
            }
            FileInputStream fin = new FileInputStream(readFile);
            DataInputStream pin = new DataInputStream(fin);
            String theString = pin.readLine();
            // Data starts after the line: "++ start"
            while (!theString.startsWith("+ Start")) {
                theString = pin.readLine();
            }
            System.out.println("++ Process inventments .");
            while (theString != null) {
                rowCount++;
                if (!(theString.startsWith("--") || theString.startsWith("+ ") || (theString.trim().compareTo("") == 0))) {
                    // System.out.println("+ theString :" + theString + ":");
                    // ETRADE          |ETRADE    |STACY     |Trading             |Old Netscape account
                    String[] theFields = theString.split("\\|");
                    int i = 0;
                    // System.out.print("+ split > ");
                    // while (i < theFields.length) {
                    //     System.out.print(" " + i + " :" + theFields[i++].trim() + ":" );
                    // }
                    // System.out.println("");
                    String theLast;
                    if (theFields.length < 5) {
                        theLast = "";
                    } else {
                        theLast = theFields[4].trim();
                    }
                    i = 0;
                    System.out.println(
                            " " + theFields[i++].trim() 
                                    + SEPARATOR + theFields[i++].trim() 
                                    + SEPARATOR + theFields[i++].trim() 
                                    + SEPARATOR + theFields[i++].trim() 
                                    + SEPARATOR + theLast
                    );
                }
                theString = pin.readLine();
            }
            pin.close();
        } catch (IOException ioe) {
            System.out.print("--- IOException: ");
            System.out.println(ioe.toString());
        }

        return rowCount;
    }

    public static void main(String[] args) {
        theDateToday = formatter.format(new Date());
        System.out.println("+++ Start, Date today <" + theDateToday + ">");

        readAccountTextFile TfpInvest = new readAccountTextFile();
        int theResponse = TfpInvest.runReset();
        System.out.println("+ Rows processed, rowCount = " + theResponse + "");

        System.out.println("+++ Exit.");
    }

}
