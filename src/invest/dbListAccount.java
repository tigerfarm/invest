package invest;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class dbListAccount extends dbConnection {

    private static final String THECLASSNAME = "dbReportAccount";
    private Connection conn = null;
    private Statement stmt = null;

    public String runReport(String theOrderBy) {
        String theResponse = "";
        if (conn == null) {
            conn = createConnection();
            if (conn == null) {
                // Failed to create a connection.
                return theResponse;
            }
        }
        theResponse = selectRows(theOrderBy);
        return theResponse;
    }

    private String selectRows(String theOrderBy) {

        String tableName = "account";
        String theOrderByClause = " order by ";
        if (theOrderBy.compareTo("") == 0) {
            theOrderByClause += "companyid, owner, accID";
        } else {
            theOrderByClause += theOrderBy;
        }
        String aSelect = "select account.COMPANYID,ACCID,OWNER,ACCNAME,ACCDESCRIPTION,company.COMPINFO"
                + " from " + tableName + ",company"
                + " where account.COMPANYID = company.COMPANYID "
                + theOrderByClause;
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
                        + ", \"owner\":\"" + results.getString("owner") + "\""
                        + ", \"accID\":\"" + results.getString("accID") + "\""
                        + ", \"accName\":\"" + results.getString("accName") + "\""
                        + ", \"accDescription\":\"" + results.getString("accDescription") + "\""
                        + ", \"COMPINFO\":\"" + results.getString("COMPINFO") + "\""
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
        dbListAccount TfpInvest = new dbListAccount();
        String theResponse = TfpInvest.runReport(theRequest);
        System.out.println("+ theResponse:\n" + theResponse + "\n: end of theResponse.");
        System.out.println("+ Order by account id:\n" + TfpInvest.runReport("accid") + "\n: end of theResponse.");

        System.out.println("+++ Exit class: " + THECLASSNAME);
    }

}
