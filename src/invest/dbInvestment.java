/*
    Investment Unit data.
*/
package invest;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class dbInvestment extends dbConnection {

    private static Connection conn = null;
    private static Statement stmt = null;
    //
    static Format formatter = new SimpleDateFormat("YYYY-MM-dd");
    private static String theDateToday;

    // -------------------------------------------------------------------------
    private int rowCount = 0;
    private final static List<String> aList = new ArrayList<>();
    private final String SEPARATOR = "|";

    public int dbInvestment() {
        // System.out.println("+ Initialize Investment Unit data.");
        selectRows("");
        if (rowCount == 0) {
            runReset();
        }
        // System.out.println("+ Investment Unit data rows: " + rowCount);
        return rowCount;
    }

    public int runReset() {
        if (loadTable() > 0) {
            // Force reloading the list in selectRows with an empty aList.
            rowCount = 0;
            aList.clear();
            selectRows("");
        }
        return rowCount;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void listRows() {
        System.out.println("+ List the row data.");
        int i = 0;
        for (Iterator<String> it = aList.iterator(); it.hasNext();) {
            String theValue = it.next();
            i++;
            System.out.println("++ " + i + " " + theValue);
            // String[] rowData = theValue.split(SEPARATOR);
            // Update list data: aList.set(i, theValue + SEPARATOR + lb)
        }
        System.out.println("+ End of list.");
    }

    private int selectRows(String theOrderBy) {
        if (rowCount > 0) {
            // Only load once.
            return rowCount;
        }
        if (conn == null) {
            conn = createConnection();
            if (conn == null) {
                // Failed to create a connection.
                return rowCount;
            }
        }
        String tableName = "unitInvestment";
        String theOrderByClause = " order by ";
        if (theOrderBy.compareTo("") == 0) {
            theOrderByClause += "codeInvestment";
        } else {
            theOrderByClause += theOrderBy;
        }
        String aSelect = "select "
                + "unitInvestment.accID,unitInvestment.codeInvestment,unitInvestment.quantity,unitInvestment.TRANSTYPE,"
                + "COMPANYID"
                + " from " + tableName + ",account"
                + " where unitInvestment.ACCID = account.ACCID"
                + " and unitInvestment.TRANSTYPE = 1"
                + theOrderByClause;
        System.out.println("++ select statement <" + aSelect + ">");
        try {
            stmt = conn.createStatement();
            ResultSet results = stmt.executeQuery(aSelect);
            while (results.next()) {
                rowCount++;
                String dataRow = results.getString("accID")
                        + SEPARATOR + results.getString("COMPANYID")
                        + SEPARATOR + results.getString("codeInvestment")
                        + SEPARATOR + results.getString("quantity");
                aList.add(dataRow);
            }
            // System.out.println("+ rows selected: " + rowCount);
            results.close();
            stmt.close();
        } catch (SQLException sqlExcept) {
            System.out.println("- sqlExcept: " + sqlExcept.getMessage());
            // sqlExcept.printStackTrace();
        }
        return rowCount;
    }

    // -------------------------------------------------------------------------
    // Load a database table with data from a text file.
    private int loadTable() {
        rowCount = 0;
        if (conn == null) {
            conn = createConnection();
            if (conn == null) {
                // Failed to create a connection.
                return rowCount;
            }
        }
        String theReadFilename = getDbTextDIR() + "tableUnitInvestment.txt";
        try {
            dbDropTable();
            dbCreateTable();

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
                if (!(theString.startsWith("--") || theString.startsWith("+ ") || (theString.trim().compareTo("") == 0))) {
                    System.out.println("+ theString :" + theString + ":");
                    String[] theFields = theString.split("\\|");
                    int i = 0;
                    rowCount += dbInsertRow(theFields[i++].trim(), theFields[i++].trim(), theFields[i++].trim(), theFields[i++].trim(), theFields[i++].trim(), theFields[i++].trim(), theFields[i++].trim());
                }
                theString = pin.readLine();
            }
            pin.close();
        } catch (IOException ioe) {
            System.out.println("- sqlExcept: " + ioe.getMessage());
            // sqlExcept.printStackTrace();
        }

        return rowCount;
    }

    private void dbDropTable() {
        try {
            String anSqlStatement = "DROP TABLE unitInvestment";
            System.out.println("++ dbDropTable <" + anSqlStatement + ">");
            stmt = conn.createStatement();
            int stmtCount = stmt.executeUpdate(anSqlStatement);
            System.out.println("+ Number of rows effected: " + rowCount);
            stmt.close();
        } catch (SQLException sqlExcept) {
            System.out.println("- sqlExcept: " + sqlExcept.getMessage());
            // sqlExcept.printStackTrace();
        }
    }

    private int dbCreateTable() {
        int stmtCount = 0;
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
            stmtCount = stmt.executeUpdate(anSqlStatement);
            System.out.println("+ Number of rows effected: " + rowCount);
            stmt.close();
        } catch (SQLException sqlExcept) {
            System.out.println("- sqlExcept: " + sqlExcept.getMessage());
            // sqlExcept.printStackTrace();
        }
        return stmtCount;
    }

    private int dbInsertRow(String f1, String f2, String f3, String f4, String f5, String f6, String f7) {
        int stmtCount = 0;
        try {
            String anSqlStatement = "insert into unitInvestment"
                    + " (accID, codeInvestment, quantity, transType, transDate, transCost, TRANSPRICE)"
                    + " values ("
                    + "'" + f1 + "'"
                    + ",'" + f2 + "'"
                    + "," + f3
                    + "," + f4
                    + ",'" + f5 + "'"
                    + "," + f6
                    + "," + f7
                    + ")";
            System.out.println("++ dbInsertRow <" + anSqlStatement + ">");
            stmt = conn.createStatement();
            stmtCount = stmt.executeUpdate(anSqlStatement);
            System.out.println("+ Number of rows effected: " + rowCount);
            stmt.close();
        } catch (SQLException sqlExcept) {
            System.out.println("- sqlExcept: " + sqlExcept.getMessage());
            // sqlExcept.printStackTrace();
        }
        return stmtCount;
    }

    // -------------------------------------------------------------------------
    public static void main(String[] args) {
        theDateToday = formatter.format(new Date());
        System.out.println("+++ Start, Date today <" + theDateToday + ">");

        dbInvestment TfpInvest = new dbInvestment();
        if (TfpInvest.dbInvestment()>0) {
            TfpInvest.listRows();
        }
        TfpInvest.runReset();
        
        System.out.println("+++ Exit.");
    }

}
