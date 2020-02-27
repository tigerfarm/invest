package invest;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class rptCompanyInvestments extends dbConnection {

    static String THECLASSNAME = "dbReportPortfolio";
    private Connection conn = null;
    private Statement stmt = null;

    public String runReport(String theOrderBy) {
        String theResponse = "";
        if (conn == null) {
            conn = createConnection();
            if (conn == null) {
                // if failed to create a connection,
                return theResponse;
            }
        }
        theResponse = selectRows(theOrderBy);
        return theResponse;
    }

    private String selectRows(String theOrderBy) {

        // String pattern = "MMM-dd-yyyy";     // String pattern = "MM/dd/yyyy";
        // SimpleDateFormat format = new SimpleDateFormat(pattern);
        String theOrderByClause = " order by ";
        if (theOrderBy.compareTo("") == 0) {
            theOrderByClause += "companyid,OWNER,accID,codeInvestment";
        } else {
            theOrderByClause += theOrderBy;
        }
        String aSelect = "select"
                + " company.COMPANYID, company.COMPDESCRIPTION, account.ACCID, account.OWNER,"
                + " unitInvestment.CODEINVESTMENT, unitInvestment.QUANTITY, tickerData.UNITPRICE"
                + " from company, account, unitInvestment, tickerData"
                + " where company.COMPANYID = account.COMPANYID"
                + " and account.ACCID = unitInvestment.ACCID"
                + " and unitInvestment.CODEINVESTMENT = tickerData.CODEINVESTMENT"
                + " and unitInvestment.TRANSTYPE = 1"
                + theOrderByClause;
        System.out.println("++ select statement <" + aSelect + ">");
        String theResponse = "{ \"company\": [\r\n";
        try {
            stmt = conn.createStatement();
            ResultSet results = stmt.executeQuery(aSelect);
            int rowCounter = 0;
            while (results.next()) {
                if (rowCounter > 0) {
                    theResponse += ",\r\n"; // rows end with a comma, except the last row.
                }
                rowCounter++;
                theResponse += "   {\"COMPANYID\":\"" + results.getString("COMPANYID") + "\""
                        + ", \"COMPDESCRIPTION\":\"" + results.getString("COMPDESCRIPTION") + "\""
                        + ", \"ACCID\":\"" + results.getString("ACCID") + "\""
                        + ", \"OWNER\":\"" + results.getString("OWNER") + "\""
                        + ", \"CODEINVESTMENT\":\"" + results.getString("CODEINVESTMENT") + "\""
                        + ", \"UNITPRICE\":" + results.getDouble("UNITPRICE")
                        + ", \"QUANTITY\":" + results.getDouble("QUANTITY")
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
        rptCompanyInvestments TfpInvest = new rptCompanyInvestments();
        String theResponse = TfpInvest.runReport(theRequest);
        System.out.println("+ theResponse:\n" + theResponse + "\n: end of theResponse.");

        System.out.println("+++ Exit class: " + THECLASSNAME);
    }

}
