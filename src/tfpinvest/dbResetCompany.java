package tfpinvest;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class dbResetCompany extends dbConnection {

    static String THECLASSNAME = "dbResetCompany";
    private static Connection conn = null;
    private static Statement stmt = null;
    //
    static Format formatter = new SimpleDateFormat("YYYY-MM-dd");
    private static String theDateToday;

    public String runReset() {

        String theResponse = "";
        if (conn == null) {
            conn = createConnection();
            if (conn == null) {
                // Failed to create a connection.
                return theResponse;
            }
        }
        dbDropTable();
        dbCreateTable();

        int rowCount = 0;
        String theReadFilename = getDbTextDIR() + "tableCompany.txt";
        try {
            // Read row data from a text file.
            File readFile = new File(theReadFilename);
            if (!readFile.exists()) {
                System.out.println("-- ** ERROR, theReadFilename does not exist.");
                return theResponse;
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
                if (!(theString.startsWith("--") || theString.startsWith("+ ") || (theString.trim().compareTo("") == 0))) {
                    System.out.println("+ theString :" + theString + ":");
                    String[] theFields = theString.split("\\|");
                    int i = 0;
                    // System.out.print("+ split > ");
                    // while (i < theFields.length) {
                    //     System.out.print(" " + i + " :" + theFields[i++].trim() + ":" );
                    // }
                    // System.out.println("");
                    String theLast;
                    int theLastColumn = 6;
                    if (theFields.length < theLastColumn) {
                        theLast = "";
                    } else {
                        theLast = theFields[theLastColumn-1].trim();
                    }
                    i = 0;
                    rowCount += dbInsertRow(theFields[i++].trim(), theFields[i++].trim(), theFields[i++].trim(), theFields[i++].trim(), theFields[i++].trim(), theLast);
                }
                theString = pin.readLine();
            }
            pin.close();
        } catch (IOException ioe) {
            System.out.print("--- IOException: ");
            System.out.println(ioe.toString());
        }

        return "+ Rows processed, rowCount = " + rowCount;
    }

    private void dbDropTable() {
        try {
            String anSqlStatement = "DROP TABLE company";
            System.out.println("++ dbDropTable <" + anSqlStatement + ">");
            stmt = conn.createStatement();
            int rowCount = stmt.executeUpdate(anSqlStatement);
            System.out.println("+ Number of rows effected: " + rowCount);
            stmt.close();
        } catch (SQLException sqlExcept) {
            System.out.println("- sqlExcept: ");
            sqlExcept.printStackTrace();
        }
    }

    private void dbCreateTable() {
        try {
            String anSqlStatement = "CREATE TABLE company ("
                    + "companyID       VARCHAR(16)    PRIMARY KEY,"
                    + "companyType     INTEGER        NOT NULL,"
                    + "COMPDESCRIPTION VARCHAR(30)    NOT NULL,"
                    + "COMPURL         VARCHAR(56)    NOT NULL,"
                    + "USERID          VARCHAR(40)    NOT NULL,"
                    + "COMPINFO        LONG VARCHAR   NOT NULL"
                    + ")";
            System.out.println("++ dbCreateTable <" + anSqlStatement + ">");
            stmt = conn.createStatement();
            int rowCount = stmt.executeUpdate(anSqlStatement);
            System.out.println("+ Number of rows effected: " + rowCount);
            stmt.close();
        } catch (SQLException sqlExcept) {
            System.out.println("- sqlExcept: ");
            sqlExcept.printStackTrace();
        }
    }

    private int dbInsertRow(String f1, String f2, String f3, String f4, String f5, String f6) {
        int rowCount = 0;
        try {
            String anSqlStatement = "insert into company"
                    + " (companyID,companyType,compDescription,compUrl,userid,compInfo)"
                    + " values ("
                    + " '" + f1 + "'"
                    + "," + f2
                    + ",'" + f3 + "'"
                    + ",'" + f4 + "'"
                    + ",'" + f5 + "'"
                    + ",'" + f6 + "'"
                    + ")";
            System.out.println("++ dbInsertRow <" + anSqlStatement + ">");
            stmt = conn.createStatement();
            rowCount = stmt.executeUpdate(anSqlStatement);
            System.out.println("+ Number of rows effected: " + rowCount);
            stmt.close();
        } catch (SQLException sqlExcept) {
            System.out.println("- sqlExcept: ");
            sqlExcept.printStackTrace();
        }
        return rowCount;
    }

    public static void main(String[] args) {
        theDateToday = formatter.format(new Date());
        System.out.println("+++ Start class: " + THECLASSNAME + ", Date today <" + theDateToday + ">");

        dbResetCompany TfpInvest = new dbResetCompany();
        String theResponse = TfpInvest.runReset();
        System.out.println("+ theResponse <" + theResponse + ">");

        System.out.println("+++ Exit class: " + THECLASSNAME);
    }

}
