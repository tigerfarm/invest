package invest;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class dbListCompany extends dbConnection {

    private static final String THECLASSNAME = "dbReportCompany";
    private Connection conn = null;
    private Statement stmt = null;

    public String runReport(String theRequest) {
        String theResponse = "";
        if (conn == null) {
            conn = createConnection();
            if (conn == null) {
                // Failed to create a connection.
                return theResponse;
            }
        }
        theResponse = selectRows();
        return theResponse;
    }

    private String selectRows() {

        String tableName = "company";
        String aSelect = "select * from " + tableName + " order by companyType, companyid";
        System.out.println("++ select statement <" + aSelect + ">");
        String theResponse = "{ \"" + tableName + "\": [\r\n";
        try {
            stmt = conn.createStatement();
            ResultSet results = stmt.executeQuery(aSelect);
            int rowCounter = 0;
            while (results.next()) {
                if (rowCounter > 0) {
                    theResponse += ",\r\n"; // rows end with a comma, except the last row.
                }
                rowCounter++;
                theResponse += "   {\"companyID\":\"" + results.getString("companyID") + "\""
                        + ", \"companyType\":\"" + results.getString("companyType") + "\""
                        + ", \"compDescription\":\"" + results.getString("compDescription") + "\""
                        + ", \"compUrl\":\"" + results.getString("compUrl") + "\""
                        + ", \"userid\":\"" + results.getString("userid") + "\""
                        + ", \"compInfo\":\"" + results.getString("compInfo") + "\""
                        + "}";
            }
            theResponse += "\r\n";          // No comma on the last row.
            theResponse += "] }\r\n";
            // System.out.println(theResponse);
            System.out.println("+ rows selected: " + rowCounter);
            results.close();
            stmt.close();
        } catch (SQLException sqlExcept) {
            sqlExcept.printStackTrace();
        }
        return theResponse;
    }

    public static void main(String[] args) {
        System.out.println("+++ Start class: " + THECLASSNAME);

        String theRequest = "";
        dbListCompany TfpInvest = new dbListCompany();
        String theResponse = TfpInvest.runReport(theRequest);
        System.out.println("+ theResponse:\n" + theResponse + "\n: end of theResponse.");

        System.out.println("+++ Exit class: " + THECLASSNAME);
    }

}
