/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package moteurrecherche;

import com.mysql.jdbc.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author alexis
 */
public class MySQLAccess {

    private final static String DBNAME = "repco14";
    private Connection connection;

    public MySQLAccess() throws ClassNotFoundException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = (Connection) DriverManager.getConnection("jdbc:mysql://blneige/repco14", "repco14", "repco14");
            System.out.println("Connexion à la base de données : OK");
        } catch (SQLException ex) {
            System.out.println("Connexion à la base de données : ECHEC.\n" + ex.getMessage());
        }
    }

    public void viewDocument() throws SQLException {
        Statement stmt = null;
        String query = "select * from documents";

        try {
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                System.out.println(id + " > " + name);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }
}
