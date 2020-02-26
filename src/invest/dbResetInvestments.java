package invest;

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

public class dbResetInvestments extends dbConnection {

    private static final String THECLASSNAME = "dbResetInvestments";
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
                // if failed to create a connection,
                return theResponse;
            }
        }
        dbDropTable();
        dbCreateTable();

        int rowCount = 0;
        String theReadFilename = getDbTextDIR() + "tableUnitInvestment.txt";
        try {
            // Read inventments from a text file.
            File readFile = new File(theReadFilename);
            if (!readFile.exists()) {
                System.out.println("-- ** ERROR, theReadFilename does not exist.");
                return theResponse;
            }
            FileInputStream fin = new FileInputStream(readFile);
            DataInputStream pin = new DataInputStream(fin);

            System.out.println("++ Process inventments.");
            // Data starts after the line: "ij> select"
            String theString = "";
            while (!theString.startsWith("ij> select")) {
                theString = pin.readLine();
            }
            pin.readLine();
            theString = pin.readLine(); // move to the next, as the rest will be handle following:
            while (theString != null && !theString.contains("rows selected")) {
                if (!(theString.startsWith("--") || theString.startsWith("+ ") || (theString.trim().compareTo("") == 0))) {
                    System.out.println("+ Text file row :" + theString + ":");
                    String[] theFields = theString.split("\\|");
                    int i = 0;
                    rowCount += dbInsertRow(theFields[i++].trim(), theFields[i++].trim(), theFields[i++].trim(), theFields[i++].trim(), theFields[i++].trim(), theFields[i++].trim(), theFields[i++].trim());
                }
                theString = pin.readLine();
            }
            pin.close();
        } catch (IOException ioe) {
            System.out.print("--- IOException: ");
            System.out.println(ioe.toString());
        }

        return "+ Inventments processed, rowCount = " + rowCount;
    }

    private void dbDropTable() {
        try {
            String anSqlStatement = "DROP TABLE unitInvestment";
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
            // DECIMAL(8,2) max: 123,456.78
            String anSqlStatement = "CREATE TABLE unitInvestment ("
                    + "accID           VARCHAR(10)    NOT NULL,"
                    + "codeInvestment  VARCHAR(10)    NOT NULL,"
                    + "quantity        DECIMAL(8,2)   NOT NULL,"
                    + "transType       INTEGER        NOT NULL,"
                    + "transDate       DATE           NOT NULL,"
                    + "transCost       DECIMAL(8,2)   NOT NULL,"
                    + "TRANSPRICE      DECIMAL(8,2)   NOT NULL"
                    + ")";
            // transType       INTEGER        NOT NULL,      -- 1:Buy, 2:Buy-Sold, 6:Sell
            // transDate       DATE           NOT NULL,      -- Date: buy/sold/current
            // transCost       DECIMAL        NOT NULL,      -- Cost to buy or sell
            // TRANSPRICE      DECIMAL        NOT NULL       -- Cost per unit when purchased/sold/current
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

    private int dbInsertRow(String theAccID, String codeInvestment, String quantity, String transType, String transDate, String transCost, String transPrice) {
        int rowCount = 0;
        try {
            String anSqlStatement = "insert into unitInvestment"
                    + " (accID, codeInvestment, quantity, transType, transDate, transCost, TRANSPRICE)"
                    + " values ("
                    + "'" + theAccID + "'"
                    + ",'" + codeInvestment + "'"
                    + "," + quantity
                    + "," + transType
                    + ",'" + transDate + "'"
                    + "," + transCost
                    + "," + transPrice
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

        dbResetInvestments TfpInvest = new dbResetInvestments();
        String theResponse = TfpInvest.runReset();
        System.out.println("+ theResponse <" + theResponse + ">");

        System.out.println("+++ Exit class: " + THECLASSNAME);
    }

}
