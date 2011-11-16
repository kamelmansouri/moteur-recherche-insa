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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author alexis
 */
public class MySQLAccess {

    private final static String DBNAME = "repco14";
    private final static boolean DEBUG = true;
    
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
    
    public boolean requeteUpdate(String q) throws SQLException {
        Statement stmt = null;
        int result = 0;
        
        try {
            stmt = connection.createStatement();

            result = stmt.executeUpdate(q);
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
        
        return (result != 0);
    }
    
    /**
     * Insère un terme dans la table Terms
     * @param mot le mot à insérer dans la colonne value
     * @param frequence la fréquence de ce mot
     * @return true si l'insertion a réussie, false sinon
     * @throws SQLException 
     */
    public boolean insertTerm(String mot, int frequence) throws SQLException {
     
        String query = "INSERT INTO terms VALUES('', '"+ mot +"', '"+ frequence +"')";
        
        boolean result = requeteUpdate(query);
        
        if(DEBUG) {
            if(!result) System.out.println("[Terms][INS] "+mot+" --> FAIL (check unique constraint)");
            else System.out.println("[Terms][INS] "+mot);
        }
        
        return result;
    }
    
    /**
     * Insère un document dans la table Documents
     * @param nomDoc
     * @return true si l'insertion a réussie, false sinon
     * @throws SQLException 
     */
    public boolean insertDocument(String nomDoc) throws SQLException {
     
        String query = "INSERT INTO documents VALUES('', '"+ nomDoc +"')";
        
        boolean result = requeteUpdate(query);
        
        if(DEBUG) {
            if(!result) System.out.println("[Documents][INS] "+nomDoc+" --> FAIL (check unique constraint)");
            else System.out.println("[Documents][INS] "+nomDoc);
        }
        
        return result;
    }
}
