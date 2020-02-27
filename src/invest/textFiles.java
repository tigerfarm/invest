package invest;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class textFiles {

    private static String SOURCEDIRECTORY = "tableData";

    // -------------------------------------------------------------------------
    public void directoryListing() {
        System.out.println("+ Directory listing for: " + SOURCEDIRECTORY);
        //
        // Need to use the full directory name.
        String currentDirectory = System.getProperty("user.dir");
        // Subdirectory to the current directory.
        String theDirectoryName = currentDirectory + "/" + SOURCEDIRECTORY;
        System.out.println("+ Program Directory = " + theDirectoryName);
        File dir = new File(theDirectoryName);
        if (!dir.isDirectory()) {
            System.out.println("-- Error: " + theDirectoryName + " is not a directory...");
            return;
        }
        if (!dir.exists()) {
            System.out.println("-- Error: " + theDirectoryName + ", directory does not exist...");
            return;
        }
        // Get directory & file info into a list
        String[] children = dir.list();
        List fileDirList = new ArrayList();
        for (int i = 0; i < children.length; i++) {
            String filename = children[i];
            // System.out.println("++ filename: " + filename);
            File theName = new File(theDirectoryName + "/" + filename);
            if (!theName.isFile()) {
                // Process directories
                fileDirList.add(i, "+ Subdirectory: " + filename);
            } else {
                // fileDirList.add(i, "+ File: " + filename + " " + formatter.format(new Date(theName.lastModified())) + ", size: " + theName.length() + " bytes");
                fileDirList.add(i, "++ " + filename);
            }
        }
        // Print List: directories then files
        for (int i = 0; i < fileDirList.size(); i++) {
            String item = (String) fileDirList.get(i);
            if (item.startsWith("+")) {
                System.out.println(item);
            }
        }
        for (int i = 0; i < fileDirList.size(); i++) {
            String item = (String) fileDirList.get(i);
            if (item.startsWith("*")) {
                System.out.println("    " + item);
            }
        }
        // System.out.println("+ End of list.");
    }

    public int listlines(String theReadFilename) {
        int rowCount = 0;
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
            while (theString != null) {
                rowCount++;
                System.out.println("+ " + rowCount + ": " + theString);
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
        System.out.println("+++ Start.");

        String currentDirectory = System.getProperty("user.dir");
        // Subdirectory to the current directory.
        String theDirectoryName = currentDirectory + "/" + SOURCEDIRECTORY;
        String theReadFilename = theDirectoryName + "/" + "tableAccount.txt";

        textFiles TfpInvest = new textFiles();
        System.out.println("--------------------------------------------------------------------------------");
        TfpInvest.directoryListing();
        System.out.println("");
        System.out.println("--------------------------------------------------------------------------------");
        int theResponse = TfpInvest.listlines(theReadFilename);
        System.out.println("");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("+ Rows processed, rowCount = " + theResponse + "");

        System.out.println("+++ Exit.");
    }

}
