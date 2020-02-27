package invest;
/*
Need to update this to use: GOOGLEFINANCE,
    https://support.google.com/docs/answer/3093281

Note, IBD information link:
    https://research.investors.com/stock-quotes/nyse-twilio-inc-cl-a-twlo.htm?fromsearch=1

*/

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
import java.util.Date;

public class dbResetTickerData extends dbConnection {

    private static final String THECLASSNAME = "dbResetTickerData";
    private static final String DBTABLE = "tickerData";
    private Connection conn = null;
    private Statement stmt = null;
    //
    private final Format formatter = new SimpleDateFormat("YYYY-MM-dd");
    private double exRateCADtoUSD = 0.0;

    public String runReset() {
        String theResponse = "";
        if (conn == null) {
            conn = createConnection();
            if (conn == null) {
                // if failed to create a connection,
                return theResponse;
            }
        }
        dropTable();
        createTable();
        exRateCADtoUSD = selectExchangeRateDouble("CAD");
        insertTextFileData();
        theResponse = setPrice();
        return theResponse;
    }

    private String insertTextFileData() {
        String theResponse = "";
        int rowCount = 0;
        String theReadFilename = getDbTextDIR() + "tableTickerData.txt";
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

            // Data starts after the select statement line: "ij> select"
            while (!theString.startsWith("ij> select")) {
                theString = pin.readLine();
            }
            while (!theString.startsWith("-------")) {
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
                    int theLastNum = 8;
                    if (theFields.length < theLastNum) {
                        // if the last field is empty.
                        theLast = "";
                    } else {
                        theLast = theFields[theLastNum - 1].trim();
                    }
                    i = 0;
                    rowCount += dbInsertRow(
                            theFields[i++].trim(),
                            theFields[i++].trim(),
                            theFields[i++].trim(),
                            theFields[i++].trim(),
                            theFields[i++].trim(),
                            theFields[i++].trim(),
                            theFields[i++].trim(),
                            theLast
                    );
                }
                theString = pin.readLine();
                if (theString.indexOf("rows selected") > 0) {
                    break;
                }
            }
            pin.close();
        } catch (IOException ioe) {
            System.out.print("--- IOException: ");
            System.out.println(ioe.toString());
        }

        return "+ Rows processed, rowCount = " + rowCount;
    }

    private void dropTable() {
        try {
            String anSqlStatement = "DROP TABLE " + DBTABLE;
            System.out.println("++ dropTable <" + anSqlStatement + ">");
            stmt = conn.createStatement();
            int rowCount = stmt.executeUpdate(anSqlStatement);
            System.out.println("+ Number of rows effected: " + rowCount);
            stmt.close();
        } catch (SQLException sqlExcept) {
            System.out.println("- sqlExcept: ");
            sqlExcept.printStackTrace();
        }
    }

    private void createTable() {
        try {
            String anSqlStatement = "CREATE TABLE " + DBTABLE + " ("
                    + "CODEINVESTMENT  VARCHAR(10)    PRIMARY KEY,"
                    + "codeExchange    VARCHAR(10)    NOT NULL,"
                    + "codeCurrency    VARCHAR(3)     NOT NULL,"
                    + "unitPrice       DECIMAL(8,4)   NOT NULL,"
                    + "priceDate       DATE           NOT NULL,"
                    + "INVTYPE         VARCHAR(12)    NOT NULL,"
                    + "CODEMARKET      VARCHAR(6)     NOT NULL,"
                    + "INVDESCRIPTION  VARCHAR(60)    NOT NULL,"
                    + "INVURL          LONG VARCHAR   NOT NULL,"
                    + "IFPRICE         DECIMAL(8,4)   NOT NULL,"
                    + "IFDATE          DATE           NOT NULL,"
                    + "INVINFO         LONG VARCHAR   NOT NULL"
                    + ")";
            System.out.println("++ createTable <" + anSqlStatement + ">");
            stmt = conn.createStatement();
            int rowCount = stmt.executeUpdate(anSqlStatement);
            System.out.println("+ Number of rows effected: " + rowCount);
            stmt.close();
        } catch (SQLException sqlExcept) {
            System.out.println("- sqlExcept: ");
            sqlExcept.printStackTrace();
        }
    }

    private int dbInsertRow(String f1, String f2, String f3, String f4, String f5, String f6, String f7, String f8) {
        int rowCount = 0;
        try {
            // + " (codeInvestment,invType,CODEEXCHANGE,codeMarket,codeCurrency,invDescription,invInfo,INVURL"
            // + " (CODEINVESTMENT,CODEMARKET,CODECURRENCY, CODEEXCHANGE,INVTYPE, INVDESCRIPTION, INVINFO ,INVURL"
            String anSqlStatement = "insert into " + DBTABLE
                    + " (CODEINVESTMENT,CODEMARKET,CODECURRENCY, CODEEXCHANGE,INVTYPE, INVDESCRIPTION, INVINFO ,INVURL"
                    + " ,unitPrice,priceDate,IFPRICE,IFDATE)"
                    + " values ("
                    + "'" + f1 + "'"
                    + ",'" + f2 + "'"
                    + ",'" + f3 + "'"
                    + ",'" + f4 + "'"
                    + ",'" + f5 + "'"
                    + ",'" + f6 + "'"
                    + ",'" + f7 + "'"
                    + ",'" + f8 + "'"
                    + ",0.0"
                    + ",'" + formatter.format(new Date()) + "'"
                    + ",0.0"
                    + ",'" + formatter.format(new Date()) + "'"
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

    private String setPrice() {

        int rowCounter = 0;
        String aSelect = "select codeInvestment,codeExchange,codeCurrency,INVTYPE,INVURL from " + DBTABLE
                + " order by codeExchange";
        System.out.println("++ setPrice <" + aSelect + ">");
        try {
            conn.setAutoCommit(true);
            stmt = conn.createStatement();
            ResultSet results = stmt.executeQuery(aSelect);
            String curExchange = "";
            String theTickers = "";
            while (results.next()) {

                String theTicker = results.getString("codeInvestment");
                String theExchange = results.getString("codeExchange");
                String theCurrency = results.getString("codeCurrency");
                String theInvType = results.getString("invType");
                String theINVURL = results.getString("INVURL");
                System.out.println("+ Row data <" + theExchange + ":" + theTicker + ":" + theCurrency + ":" + theInvType + ":" + theINVURL + ">");

                if (theInvType.compareTo("CASH") == 0) {
                    // If cash, use the exchange rate to USD.
                    updateUNITPRICE(theTicker, selectExchangeRate(theCurrency));
                } else {
                    // Stock or Mutual Fund
                    if (curExchange.compareTo("") == 0) {
                        theTickers += theExchange + ":" + theTicker;
                    } else {
                        theTickers += "," + theExchange + ":" + theTicker;
                    }
                    curExchange = theExchange;
                }
                rowCounter++;
            }
            updatePrices(theTickers);

            // System.out.println(theResponse);
            System.out.println("+ rows selected: " + rowCounter);
            results.close();
            stmt.close();
        } catch (SQLException sqlExcept) {
            sqlExcept.printStackTrace();
        }
        return "Number of rows updated = " + rowCounter;
    }

    private String selectExchangeRate(String theCurrency) {
        String theExRate = "" + selectExchangeRateDouble(theCurrency);
        return theExRate;
    }

    private double selectExchangeRateDouble(String theCurrency) {

        String aSelect = "select exRate from currencyExchangeRates where codeCurrency = '" + theCurrency + "'";
        System.out.println("++ selectExchangeRate <" + aSelect + ">");
        double exRate = 0.0;
        try {
            stmt = conn.createStatement();
            ResultSet results = stmt.executeQuery(aSelect);
            results.next();
            exRate = 1 / results.getDouble("exRate");
            // System.out.println(theResponse);
            System.out.println("+ theExRate: " + exRate);
            results.close();
            stmt.close();
        } catch (SQLException sqlExcept) {
            sqlExcept.printStackTrace();
        }
        return exRate;
    }

    private String updatePrices(String theTickers) {
        System.out.println("+ theTickers <" + theTickers + ">");
        // The prices are grouped by the exchange code: TSE, NYSE, NASDAQ, MUTF, MUTF_CA
        // String thePriceData = "VBMPX:10.97,VIIIX:195.04,VFINX:197.11,VEMPX:166.01,VWUSX:29.88,LZEMX:16.48";

        String[] theTickersData = theTickers.split(":");
        String theExCode = theTickersData[0];
        String thisValue = "";
        String[] tickers = thisValue.split(",");
        for (String ticker : tickers) {
            String[] tickerData = ticker.split(":");
            String theTicker = tickerData[0];
            String theUSDPrice = tickerData[1];
            if (((theExCode.compareTo("MUTF_CA") == 0) || (theExCode.compareTo("TSE") == 0)) && !(theTicker.compareTo("DIA") == 0)) {
                // If Canadian mutual fund, the price is in Canadian dollars and needs to be converted to USD.
                double thePriceDouble = exRateCADtoUSD * Double.parseDouble(theUSDPrice);
                theUSDPrice = "" + thePriceDouble;
            }
            updateUNITPRICE(theTicker, theUSDPrice);
        }

        return "";
    }

    private void updateUNITPRICE(String theTicker, String thePrice) {
        try {
            String anSqlStatement = "update " + DBTABLE + " set"
                    + " unitPrice = " + thePrice
                    + " where codeInvestment = '" + theTicker + "'";
            System.out.println("++ updateUNITPRICE <" + anSqlStatement + ">");
            stmt = conn.createStatement();
            int rowCount = stmt.executeUpdate(anSqlStatement);
            System.out.println("+ Number of rows effected: " + rowCount);
            stmt.close();
        } catch (SQLException sqlExcept) {
            System.out.println("- sqlExcept: ");
            sqlExcept.printStackTrace();
        }
    }

    private void updateINVURL(String theTicker, String INVURL) {
        try {
            String anSqlStatement = "update " + DBTABLE + " set"
                    + " INVURL = '" + INVURL + "'"
                    + " where codeInvestment = '" + theTicker + "'";
            System.out.println("++ updateINVURL <" + anSqlStatement + ">");
            stmt = conn.createStatement();
            int rowCount = stmt.executeUpdate(anSqlStatement);
            System.out.println("+ Number of rows effected: " + rowCount);
            stmt.close();
        } catch (SQLException sqlExcept) {
            System.out.println("- sqlExcept: ");
            sqlExcept.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println("+++ Start class: " + THECLASSNAME);

        dbResetTickerData TfpInvest = new dbResetTickerData();
        String theResponse = TfpInvest.runReset();
        System.out.println("+ theResponse <" + theResponse + ">");

        System.out.println("+++ Exit class: " + THECLASSNAME);
    }

}
