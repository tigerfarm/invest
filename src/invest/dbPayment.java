/*
    Payment data.
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

public class dbPayment extends dbConnection {

    private static Connection conn = null;
    private static Statement stmt = null;
    //
    static Format formatter = new SimpleDateFormat("YYYY-MM-dd");
    private static String theDateToday;

    // -------------------------------------------------------------------------
    private int rowCount = 0;
    private final static List<String> aList = new ArrayList<>();
    private final String SEPARATOR = "|";

    public int dbPayment() {
        // System.out.println("+ Initialize Payment data.");
        selectRows("");
        if (rowCount == 0) {
            runReset();
        }
        // System.out.println("+ Payment data rows: " + rowCount);
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
        String tableName = "payment";
        String theOrderByClause = " order by ";
        if (theOrderBy.compareTo("") == 0) {
            theOrderByClause += "paymentID";
        } else {
            theOrderByClause += theOrderBy;
        }
        String aSelect = "select *"
                + " from " + tableName
                // + " where account.COMPANYID = company.COMPANYID "
                + theOrderByClause;
        // System.out.println("++ select statement <" + aSelect + ">");
        try {
            stmt = conn.createStatement();
            try (ResultSet results = stmt.executeQuery(aSelect)) {
                while (results.next()) {
                    rowCount++;
                    String dataRow = results.getString("paymentID")
                            + SEPARATOR + results.getString("PAYMENTTYPE")
                            + SEPARATOR + results.getString("FREQUENCY")
                            + SEPARATOR + results.getString("DUEDAY")
                            + SEPARATOR + results.getString("PAYDAY")
                            + SEPARATOR + results.getString("PAID")
                            + SEPARATOR + results.getString("BALANCE")
                            + SEPARATOR + results.getString("COMPANYNAME")
                            + SEPARATOR + results.getString("PHONE")
                            + SEPARATOR + results.getString("NOTE");
                    aList.add(dataRow);
                }
                // System.out.println("+ rows selected: " + rowCount);
            }
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
        String theReadFilename = getDbTextDIR() + "tablePayment.txt";
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
                    // System.out.print("+ split > ");
                    // while (i < theFields.length) {
                    //     System.out.print(" " + i + " :" + theFields[i++].trim() + ":" );
                    // }
                    // System.out.println("");
                    String theLast = "";
                    if (theFields.length < 10) {
                        theLast = "";
                    } else {
                        theLast = theFields[9].trim();
                    }
                    i = 0;
                    rowCount += dbInsertRow(theFields[i++].trim(), theFields[i++].trim(), theFields[i++].trim(), theFields[i++].trim(), theFields[i++].trim(), theFields[i++].trim(), theFields[i++].trim(), theFields[i++].trim(), theFields[i++].trim(), theLast);
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
            String anSqlStatement = "DROP TABLE payment";
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
            // 1234567890123456|12345678901|123456789|123456|123456|12345678|12345678|12345678901234567890123456789012345678|1234567890123456|1234567890...
            // PAYMENTID       |PAYMENTTYPE|FREQUENCY|DUEDAY|PAYDAY|PAID    |BALANCE |COMPANYNAME                           |PHONE           ||NOTE
            String anSqlStatement = "CREATE TABLE payment ("
                    + "PAYMENTID    VARCHAR(16)    NOT NULL"
                    + ",PAYMENTTYPE  VARCHAR(12)    NOT NULL"
                    + ",FREQUENCY    VARCHAR( 9)    NOT NULL"
                    + ",DUEDAY       VARCHAR( 6)    NOT NULL"
                    + ",PAYDAY       VARCHAR( 6)"
                    + ",PAID         VARCHAR( 8)"
                    + ",BALANCE      VARCHAR( 8)"
                    + ",COMPANYNAME  VARCHAR(38)    NOT NULL"
                    + ",PHONE        VARCHAR(16)"
                    + ",NOTE         VARCHAR(80)"
                    + ")";
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

    private int dbInsertRow(String f1, String f2, String f3, String f4, String f5, String f6, String f7, String f8, String f9, String f10) {
        int stmtCount = 0;
        try {
            String anSqlStatement = "insert into payment"
                    + " (PAYMENTID,PAYMENTTYPE,FREQUENCY,DUEDAY,PAYDAY,PAID,BALANCE,COMPANYNAME,PHONE,NOTE)"
                    + " values ("
                    + "'" + f1 + "'"
                    + ",'" + f2 + "'"
                    + ",'" + f3 + "'"
                    + ",'" + f4 + "'"
                    + ",'" + f5 + "'"
                    + ",'" + f6 + "'"
                    + ",'" + f7 + "'"
                    + ",'" + f8 + "'"
                    + ",'" + f9 + "'"
                    + ",'" + f10 + "'"
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

        dbPayment TfpInvest = new dbPayment();
        if (TfpInvest.dbPayment()>0) {
            TfpInvest.listRows();
        }

        System.out.println("+++ Exit.");
    }

}
