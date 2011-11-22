
package moteurrecherche.Database;

import com.mysql.jdbc.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class MySQLAccess {

    private final static boolean DEBUG = true;
    
    private Connection connection;

    /**
     * Initialise une connexion à la base de données
     * @throws ClassNotFoundException 
     */
    public MySQLAccess() throws ClassNotFoundException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = (Connection) DriverManager.getConnection("jdbc:mysql://blneige/repco14", "repco14", "repco14");
            System.out.println("Connexion à la base de données : OK");
        } catch (SQLException ex) {
            System.out.println("Connexion à la base de données : ECHEC.\n" + ex.getMessage());
        }
    }

    /**
     * Traite les requêtes de sélection
     * @param q la requête à exécuter
     * @return null si le résultat de la requête est vide, ResultSet sinon
     * @throws SQLException 
     */
    public ResultSet requeteSelect(String q) throws SQLException {
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = connection.createStatement();
            rs = stmt.executeQuery(q);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
        
        return rs;
    }
    
    /**
     * Traite les requêtes d'insertion, update et suppression
     * @param q la requête à exécuter
     * @return true si la requête a renvoyé quelque chose, false sinon
     * @throws SQLException 
     */
    private boolean requeteUpdate(String q) throws SQLException {
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
    public boolean insertTerm(int id, String mot, int frequence) throws SQLException {
     
        String query = "INSERT INTO terms VALUES('"+ id +"', '"+ mot +"', '"+ frequence +"')";
        
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
    
    /**
     * Insère un noeud dans la table Nodes
     * @param id l'id du noeud
     * @param doc_id l'id du document dans lequel se trouve le noeud
     * @param label l'intitulé du noeud
     * @param parent_id l'id du noeud parent
     * @return true si l'insertion a réussie, false sinon
     * @throws SQLException 
     */  
    public boolean insertNode(int id, int doc_id, String label, int parent_id) throws SQLException {
     
        String query = "INSERT INTO nodes VALUES('"+ id +"', '"+ doc_id +
                "', '"+ label +"', '"+ parent_id +"')";
        
        boolean result = requeteUpdate(query);
        
        if(DEBUG) {
            if(!result) System.out.println("[Nodes][INS] "+id+"/"+label
                    +" --> FAIL (check unique constraint)");
            else System.out.println("[Nodes][INS] "+id+"/"+label);
        }
        
        return result;
    }
    
    /**
     * Insère un couple terme/noeud dans la table Term_in_node
     * @param term_id l'id du terme à insérer
     * @param node_id l'id du noeud contenant le terme à insérer
     * @param freq la fréquence du terme dans ce noeud
     * @return true si l'insertion a réussie, false sinon
     * @throws SQLException 
     */
    public boolean insertTermInNode(int term_id, int node_id, int freq) throws SQLException {
     
        String query = "INSERT INTO term_in_node VALUES('"+ term_id +"', '"
                + node_id +"', '"+ freq +"')";
        
        boolean result = requeteUpdate(query);
        
        if(DEBUG) {
            if(!result) System.out.println("[Term_in_Node][INS] "+term_id+"/"+node_id
                    +" --> FAIL (check unique constraint)");
            else System.out.println("[Term_in_Node][INS] "+term_id+"/"+node_id);
        }
        
        return result;
    }
    
    /**
     * Insère un couple terme/noeud avec la position du terme dans ce noeud
     * @param term_id l'id du terme à insérer
     * @param node_id l'id du noeud contenant le terme à insérer
     * @param pos la position du terme dans ce noeud
     * @return true si l'insertion a réussie, false sinon
     * @throws SQLException 
     */
    public boolean insertTermPos(int term_id, int node_id, int pos) throws SQLException {
     
        String query = "INSERT INTO term_pos VALUES('"+ term_id +"', '"
                + node_id +"', '"+ pos +"')";
        
        boolean result = requeteUpdate(query);
        
        if(DEBUG) {
            if(!result) System.out.println("[Term_pos][INS] "+term_id+"/"+node_id
                    +" --> FAIL (check unique constraint)");
            else System.out.println("[Term_pos][INS] "+term_id+"/"+node_id);
        }
        
        return result;
    }
}
