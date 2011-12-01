package moteurrecherche.Database;

import com.mysql.jdbc.Connection;
import java.io.File;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import moteurrecherche.ParserChaine.TermeCollection;
import moteurrecherche.ParserChaine.TermeDansNoeud;
import moteurrecherche.ParserChaine.TermePosition;
import moteurrecherche.ParserXML.NoeudText;

public class MySQLAccess {

    private final static boolean DEBUG = true;
    private final static int MAX_QUERIES = 227;
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
            stmt.close();
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
    public boolean insertTerm(HashMap<String, TermeCollection> listeTermesCollection) throws SQLException {
        int idTerme, freq, cpt = 0;
        boolean result = false;

        String query = "INSERT INTO terms (id, value, frequency) VALUES ";

        for (Entry<String, TermeCollection> entry :
                listeTermesCollection.entrySet()) {

            String mot = entry.getKey();
            TermeCollection termeCollection = entry.getValue();
            idTerme = termeCollection.getIdTerme();
            freq = termeCollection.getFrequence();

            if (cpt > 0) {
                query += ", ";
            }
            query += "('" + idTerme + "', '" + mot + "', '" + freq + "')";

            cpt++;

            if (cpt == MAX_QUERIES) {
                result = requeteUpdate(query); //executer
                cpt = 0;
                query = "INSERT INTO terms (id, value, frequency) VALUES ";
            }
        }

        if (DEBUG) {
            System.out.println("Insertion des termes : OK\n");
        }

        return result;
    }

    /**
     * Insère un document dans la table Documents
     * @param nomDoc
     * @return true si l'insertion a réussie, false sinon
     * @throws SQLException 
     */
    public boolean insertDocument(File[] files) throws SQLException {
        int cpt = 0;
        String query = "INSERT INTO documents (id, name) VALUES ";

        for (File doc : files) {
            if (cpt > 0) {
                query += ", ";
            }
            query += "('', '" + doc.getName() + "')";

            cpt++;
        }

        boolean result = requeteUpdate(query);

        if (DEBUG) {
            System.out.println("Insertion des documents : OK\n");
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
    public boolean insertNode(ArrayList<NoeudText> listeNoeuds) throws SQLException {
        boolean result = false;
        int cpt = 0;

        String query = "INSERT INTO nodes (id, doc_id, label, parent_id) VALUES ";
        
        for (NoeudText noeud : listeNoeuds) {
            if (noeud != null) {
                if (cpt > 0) {
                    query += ", ";
                }

                query += "('" + noeud.getId() + "', '" + noeud.getIdDoc()
                        + "', '" + noeud.getLabel() + "', '" + noeud.getIdParent() + "')";

                cpt++;

                if (cpt == MAX_QUERIES || cpt == listeNoeuds.size()) {
                    result = requeteUpdate(query); //executer
                    cpt = 0;
                    query = "INSERT INTO nodes (id, doc_id, label, parent_id) VALUES ";
                }
            }
        }

        if (DEBUG) {
            System.out.println("Insertion des noeuds : OK\n");
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
    public boolean insertTermInNode(ArrayList<TermeDansNoeud> listeTermesDansNoeud)
            throws SQLException {

        boolean result = false;
        int cpt = 0;

        String query = "INSERT INTO term_in_node (term_id, node_id, frequency) VALUES ";
        
        for (TermeDansNoeud terme : listeTermesDansNoeud) {

                if (cpt > 0) {
                    query += ", ";
                }

                query += "('" + terme.getIdTerme() + "', '" + terme.getIdNoeud()
                        + "', '" + terme.getFreq() + "')";

                cpt++;

                if (cpt == MAX_QUERIES || cpt == listeTermesDansNoeud.size()) {
                    result = requeteUpdate(query); //executer
                    cpt = 0;
                    query = "INSERT INTO term_in_node (term_id, node_id, frequency) VALUES ";
                }

        }

        if (DEBUG) {
            System.out.println("Insertion des termes dans noeud : OK\n");
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
    public boolean insertTermPos(ArrayList<TermePosition> listeTermesPosition) 
            throws SQLException {

        boolean result = false;
        int cpt = 0;

        String query = "INSERT INTO term_pos (term_id, node_id, pos) VALUES ";
        
        for (TermePosition terme : listeTermesPosition) {

                if (cpt > 0) {
                    query += ", ";
                }

                query += "('" + terme.getIdTerme() + "', '" + terme.getIdNoeud()
                        + "', '" + terme.getPos() + "')";

                cpt++;

                if (cpt == MAX_QUERIES || cpt == listeTermesPosition.size()) {
                    result = requeteUpdate(query); //executer
                    cpt = 0;
                    query = "INSERT INTO term_pos (term_id, node_id, pos) VALUES ";
                }

        }

        if (DEBUG) {
            System.out.println("Insertion des termes position : OK\n");
        }

        return result;
    }

    /**
     * Cherche l'id d'un terme à partir de sa valeur littérale
     * @param term_value la valeur du terme
     * @return l'id associé à la valeur du terme cherché
     * @throws SQLException
     */
    public int getTermIdByTermValue(String term_value) throws SQLException {
        int term_id = -1;

        String query = "SELECT id FROM terms "+
                "WHERE value='"+ term_value +"'";

        ResultSet rs = requeteSelect(query);

        if(rs.first());
            term_id = rs.getInt("id");

        rs.close();

        return term_id;
    }

    /**
     * Cherche tous les triplets <term_id, node_id, frequency> pour un term_id donné
     * @param term_id la clé de l'ensemble à chercher
     * @return une liste de triplets dont la clé est l'id du terme
     * @throws SQLException
     */
    public ArrayList<TermInNode> getTermInNodeByTermId(int term_id) throws SQLException {
        ArrayList<TermInNode> termInNodeList = new ArrayList<TermInNode>();

        String query = "SELECT * FROM term_in_node "+
                "WHERE term_id='"+ term_id +"'";

        ResultSet rs = requeteSelect(query);

        while(rs.next()) {
            termInNodeList.add(new TermInNode(
                    rs.getInt("term_id"),
                    rs.getInt("node_id"),
                    rs.getInt("frequency")
                    ));
        }

        rs.close();

        return termInNodeList;
    }
}
