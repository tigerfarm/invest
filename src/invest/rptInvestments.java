package invest;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class rptInvestments extends dbConnection {

    private static final String THECLASSNAME = "dbInvestmentReport";
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

        String tableName = "unitInvestment";
        String theOrderByClause = " order by ";
        if (theOrderBy.compareTo("") == 0) {
            theOrderByClause += "codeInvestment";
        } else {
            theOrderByClause += theOrderBy;
        }
        String aSelect = "select "
                + "unitInvestment.accID,unitInvestment.codeInvestment,unitInvestment.quantity,unitInvestment.TRANSTYPE,"
                + "tickerData.CODEMARKET,tickerData.codeCurrency,tickerData.CODEEXCHANGE,tickerData.INVTYPE,tickerData.unitPrice,INVDESCRIPTION,"
                + "COMPANYID"
                + " from " + tableName + ",tickerData,account"
                + " where unitInvestment.codeInvestment = tickerData.codeInvestment and unitInvestment.ACCID = account.ACCID"
                + " and unitInvestment.TRANSTYPE = 1"
                + theOrderByClause;
        // Note, accID report would be different order by, same data is fine.
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
                theResponse += "   {\"accID\":\"" + results.getString("accID") + "\""
                        + ", \"COMPANYID\":\"" + results.getString("COMPANYID") + "\""
                        + ", \"codeInvestment\":\"" + results.getString("codeInvestment") + "\""
                        + ", \"quantity\":\"" + results.getString("quantity") + "\""
                        + ", \"CODEMARKET\":\"" + results.getString("CODEMARKET") + "\""
                        + ", \"codeCurrency\":\"" + results.getString("codeCurrency") + "\""
                        + ", \"codeExchange\":\"" + results.getString("codeExchange") + "\""
                        + ", \"INVTYPE\":\"" + results.getString("INVTYPE") + "\""
                        + ", \"unitPrice\":\"" + results.getString("unitPrice") + "\""
                        + ", \"INVDESCRIPTION\":\"" + results.getString("INVDESCRIPTION") + "\""
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

        rptInvestments TfpInvest = new rptInvestments();

        String theRequest = "";
        System.out.println("+ theResponse #1:\n" + TfpInvest.runReport(theRequest) + "\n: end of theResponse.");
        theRequest = "codeCurrency,CODEMARKET,INVTYPE,codeInvestment";
        System.out.println("+ theResponse #2:\n" + TfpInvest.runReport(theRequest) + "\n: end of theResponse.");

        System.out.println("+++ Exit class: " + THECLASSNAME);
    }

}
