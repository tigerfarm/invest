/*
    Interactive program to assemble programs into byte code for Processor.ini to run.

    From a command prompt, call various Altair 101 assembly functions.    
    To run:
        $ java -jar cli.jar

    Next,
    + Create Google Spreadsheet for the stock prices, with the option to download CSV file.
    + Create dbResetStockData.java which uses data from the above: CSV file.
    + List investments using the above stock prices.
 */
package invest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class cli {

    private static final String PROGRAMVERSION = "0.90a";

    textFiles fileProcess = new textFiles();
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    private static String sourcedirectory = "tableData";

    private String theResponse;
    private String theRequest;
    dbResetAccount dbAccount = new dbResetAccount();
    dbResetCompany dbCompany = new dbResetCompany();
    dbResetInvestments dbInvestments = new dbResetInvestments();

    dbListAccount listAccount = new dbListAccount();
    dbListCompany listCompany = new dbListCompany();
    dbListInvestments listInvestments = new dbListInvestments();

    private static final String LISTOPTIONS = "<file|bytes>";
    private static final String SETOPTIONS = "<directory|source>";

    // -------------------------------------------------------------------------
    public void run() {
        // asmProcessor doList = new asmProcessor();
        String sourceFile = "p1.asm";
        String byteFile = "p1.bin";
        String fullFilename = sourcedirectory + "/" + sourceFile;
        String cmd;
        String cmdP1;
        String cmdP2;
        String theRest;
        int si = 0;
        int ei = 0;

        String thePrompt = "> ";
        System.out.print("+ Enter 'exit' to exit. 'help' to get a command listing.");
        String consoleInLine = "";
        System.out.println("+ Again, enter 'exit' to end the input loop.");
        while (!(consoleInLine.equals("exit"))) {
            System.out.print(thePrompt);
            try {
                consoleInLine = this.br.readLine().trim();
            } catch (IOException e) {
                System.out.print("--- Error exception." + e.getMessage());
            }
            int c1 = consoleInLine.indexOf(" ", si);
            int c2 = 0;
            cmdP1 = "";
            cmdP2 = "";
            if (c1 < 0) {
                // Get the command word, for example: list,
                //      list
                cmd = consoleInLine.toLowerCase();
                theRest = "";
            } else {
                cmd = consoleInLine.substring(si, c1).toLowerCase();
                theRest = consoleInLine.substring(c1 + 1).trim();
                c2 = theRest.indexOf(" ", si);
                if (c2 < 0) {
                    // Get the first parameter, for example: file,
                    //      list file
                    cmdP1 = theRest.toLowerCase();
                } else {
                    cmdP1 = theRest.substring(0, c2).toLowerCase();
                    // Get the second parameter, for example: 6,
                    //      set ignore 6
                    cmdP2 = theRest.substring(c2 + 1).toLowerCase();
                }
            }
            // System.out.println("+ Parse, cmd:" + cmd + ":" + cmdP1 + ":" + cmdP2 + ":");
            // System.out.println("+ cmd : " + cmd + ":" + theRest + ".");
            switch (cmd) {
                case "dir":
                case "ls":
                    System.out.println("+ -------------------------------------");
                    fileProcess.directoryListing();
                    break;
                case "file":
                    // > file this.asm
                    if (cmdP1.length() > 0) {
                        sourceFile = cmdP1;
                        if (!sourcedirectory.equals("")) {
                            fullFilename = sourcedirectory + "/" + sourceFile;
                        }
                    }
                    System.out.println("+ Program source file name: " + sourceFile);
                    System.out.println("+ Program full file name: " + fullFilename);
                    System.out.println("+ Machine byte code file name: " + byteFile);
                    break;
                // -------------------------------------------------------------
                case "list":
                    switch (cmdP1) {
                        case "a":
                        case "account":
                            System.out.println("+ -------------------------------------");
                            System.out.println("+ List account data.");
                            theRequest = "";
                            theResponse = listAccount.runReport(theRequest);
                            System.out.println("+ listAccount Response <" + theResponse + ">");
                            break;
                        case "c":
                        case "company":
                            System.out.println("+ -------------------------------------");
                            System.out.println("+ List company data.");
                            theRequest = "";
                            theResponse = listCompany.runReport(theRequest);
                            System.out.println("+ listCompany Response <" + theResponse + ">");
                            break;
                        case "i":
                        case "investments":
                            System.out.println("+ -------------------------------------");
                            System.out.println("+ List investments data.");
                            theRequest = "";
                            theResponse = listInvestments.runReport(theRequest);
                            System.out.println("+ listInvestments response #1\n" + theResponse + "\n: end of theResponse.");
                            theRequest = "INVTYPE,codeInvestment";
                            theResponse = listInvestments.runReport(theRequest);
                            System.out.println("+ listInvestments response #2\n" + theResponse + "\n: end of theResponse.");
                            break;
                        case "file":
                            System.out.println("+ -------------------------------------");
                            System.out.println("+ List data source file: " + fullFilename + ":");
                            fileProcess.listlines(fullFilename);
                            break;
                        default:
                            System.out.println("- Invalid list option." + cmdP1);
                            break;
                    }
                    break;
                // -------------------------------------------------------------
                case "load":
                    switch (cmdP1) {
                        case "a":
                        case "account":
                            System.out.println("+ -------------------------------------");
                            System.out.println("+ Load account data.");
                            theResponse = dbAccount.runReset();
                            System.out.println("+ dbAccount Response <" + theResponse + ">");
                            break;
                        case "c":
                        case "company":
                            System.out.println("+ -------------------------------------");
                            System.out.println("+ Load account data.");
                            theResponse = dbCompany.runReset();
                            System.out.println("+ dbCompany theResponse <" + theResponse + ">");
                            break;
                        case "i":
                        case "investements":
                            System.out.println("+ -------------------------------------");
                            System.out.println("+ Load account data.");
                            theResponse = dbInvestments.runReset();
                            System.out.println("+ dbInvestments theResponse <" + theResponse + ">");
                            break;
                        default:
                            System.out.println("- Invalid load option." + cmdP1);
                            break;
                    }
                    break;
                // -------------------------------------------------------------
                case "set":
                    switch (cmdP1) {
                        case "directory":
                            if (cmdP2.length() > 0) {
                                if (cmdP2.equals("\"\"")) {
                                    sourcedirectory = "";
                                } else {
                                    sourcedirectory = cmdP2;
                                }
                            }
                            if (sourcedirectory.equals("")) {
                                System.out.println("+ Program source subdirectoy name not set.");
                            } else {
                                System.out.println("+ Program source subdirectoy name: " + sourcedirectory + ".");
                            }
                            break;
                        default:
                            System.out.println("- Invalid set option: " + theRest);
                            break;
                    }
                    break;

                // -------------------------------------------------------------
                case "clear":
                    // Works from UNIX console.
                    System.out.print("\033[H\033[2J");
                    System.out.flush();
                    System.out.println("Altair 101 8080/8085 assembler, version " + PROGRAMVERSION);
                    break;
                case "exit":
                    System.out.println("+ -------------------------------------");
                    System.out.println("+++ Exit.");
                    break;
                // -------------------------------------------------------------
                // -------------------------------------------------------------
                case "help":
                    System.out.println("---------------------------------------");
                    System.out.println("Investment application, version " + PROGRAMVERSION);
                    System.out.println("");
                    System.out.println("Help document,");
                    System.out.println("----------------------");
                    System.out.println("+ file <source>      : Set the input file name to use in other commands.");
                    if (!sourceFile.equals("")) {
                        System.out.println("+ Program source file name: " + sourceFile);
                        System.out.println("+ Program full file name: " + fullFilename);
                    }
                    if (sourcedirectory.equals("")) {
                        System.out.println("+ Program source subdirectoy name not set.");
                    } else {
                        System.out.println("+ Program source subdirectoy name: " + sourcedirectory + ".");
                    }
                    System.out.println("");
                    System.out.println("----------------------");
                    System.out.println("");
                    System.out.println("+ dir|ls             : List files in the set directory.");
                    System.out.println("");
                    System.out.println("> list " + LISTOPTIONS);
                    System.out.println("+ list                : List the program source file.");
                    System.out.println("");
                    System.out.println("> set " + SETOPTIONS);
                    System.out.println("+ set directory <program-source-directory>");
                    System.out.println("");
                    System.out.println("+ clear     : Clear screen. Should work on UNIX based consoles, not Windows.");
                    System.out.println("+ exit      : Exit this program.");
                    System.out.println("");
                    System.out.println("> list " + LISTOPTIONS);
                    System.out.println("> set " + SETOPTIONS);
                    System.out.println("");
                    break;
                default:
                    if (!cmd.equals("")) {
                        System.out.println("- Invalid command: " + cmd);
                    }
                    break;
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("+++ Start investment CLI, version " + PROGRAMVERSION);
        System.out.println("");
        cli cliProcess = new cli();
        cliProcess.run();
        System.out.println("\n++ Exit.");
    }
}
