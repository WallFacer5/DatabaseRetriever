import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;
import javax.swing.*;

public class Connector {
    static Scanner reader = new Scanner(System.in);
    static Statement stat = null;

    public Connector() {
        final String DB_URL = "jdbc:mariadb://172.17.134.126:3306/TEST";
        final String USER = "remote";
        final String PWD = "123456";
        try{
            Class.forName("org.mariadb.jdbc.Driver");

            System.out.println("Connecting to a selected databse...");
            Connection con = DriverManager.getConnection(DB_URL, USER, PWD);
            System.out.println("Database connected.");

            stat = con.createStatement();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static ResultSet query(String command){
        try{
            System.out.println(command);
            ResultSet result = stat.executeQuery(command);
            return result;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /*
    public static String getCommand(String colNames, String tableNames, String condition){
        // Generate the query command
        final String select = "SELECT";
        final String from = "FROM";
        final String where = "WHERE";
        if(condition == null)
            return select + ' ' + colNames + ' ' + from + ' ' + tableNames;
        else
            return select + ' ' + colNames + ' ' + from + ' ' + tableNames + ' '+ where + ' ' + condition;
    }
     */

    public static void printResult(ResultSet res){
        // Function to send result to from end
        try {
            int col_len = res.getMetaData().getColumnCount();
            while (res.next()) {
                for (int i = 1; i <= col_len; i++) {
                    String format_out = String.format("|%-15s", res.getString(i));
                    System.out.print(format_out);
                }
                System.out.println('|');
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}
