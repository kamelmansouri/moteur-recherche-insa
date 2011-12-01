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
import moteurrecherche.ParserXML.Noeud;

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
    public boolean insertNode(ArrayList<Noeud> listeNoeuds) throws SQLException {
        boolean result = false;
        int cpt = 0;

        String query = "INSERT INTO nodes (id, doc_id, label, parent_id, words) VALUES ";
        
        for (Noeud noeud : listeNoeuds) {
            if (noeud != null) {
                if (cpt > 0) {
                    query += ", ";
                }

                query += "('" + noeud.getId() + "', '" + noeud.getIdDoc()
                        + "', '" + noeud.getLabel() + "', '" + noeud.getIdParent() 
                        + "', '" + noeud.getNbMots() + "')";

                cpt++;

                if (cpt == MAX_QUERIES || cpt == listeNoeuds.size()) {
                    result = requeteUpdate(query); //executer
                    cpt = 0;
                    query = "INSERT INTO nodes (id, doc_id, label, parent_id, words) VALUES ";
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
     * Charge un objet Term à partir d'un mot
     * @param term_value la valeur du terme
     * @return l'objet term associé à la valeur du terme
     * @throws SQLException
     */
    public Term getTermByTermValue(String term_value) throws SQLException {
        Term term = null;

        String query = "SELECT * FROM terms "+
                "WHERE value='"+ term_value +"'";

        ResultSet rs = requeteSelect(query);

        if(rs.first());
            term = new Term(rs.getInt("id"), term_value, rs.getInt("frequency"));

        rs.close();

        return term;
    }

    /**
     * Charge toutes les informations d'un noeud donné en mémoire
     * @param id l'id du noeud à chargé
     * @return un objet de type Noeud contenant les infos pour id noeud
     * @throws SQLException
     */
    public Noeud getNodeByNodeId(int id) throws SQLException {
        Noeud node = null;

        String query = "SELECT * FROM nodes WHERE id='"+ id +"'";

        ResultSet rs = requeteSelect(query);

        if(rs.first()) {
            node = new Noeud(
                    rs.getInt("id"),
                    rs.getInt("doc_id"),
                    rs.getString("label"),
                    rs.getInt("parent_id"),
                    rs.getInt("words"));
        }

        rs.close();

        return node;
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

    /**
     *
     * @return
     * @throws SQLException
     */
    public int getNumNodes() throws SQLException{
        String query = "SELECT COUNT(id) AS num_nodes FROM nodes"+
                " WHERE label='P' OR label='TITRE' OR label='SOUS-TITRE' OR label='AUTEUR'";

        ResultSet result = requeteSelect(query);
        int nodes = 0;

        if(result.first()){
            nodes = Integer.parseInt(result.getString("num_nodes"));
        }

        result.close();

        return nodes;
    }

    /**
     * Calcule le nombre de paragraphes qui contiennent un mot donné
     * @param term_id l'id du mot à chercher
     * @return le nombre de noeuds (paragraphes, etc) contenant le mot donné
     * @throws SQLException
     */
    public int getNbOfNodesWithTermId(int term_id) throws SQLException{
        String query = "SELECT COUNT(node_id) AS num_nodes FROM term_in_node "+
                "WHERE term_id='" + term_id + "'";

        ResultSet result = this.requeteSelect(query);
        int num_nodes = 0;

        if(result.first()){
            num_nodes = Integer.parseInt(result.getString("num_nodes"));
        }

        result.close();

        return num_nodes;
    }

    /**
     * Renvoie le nom d'un document suivant l'id donné
     * @param doc_id l'id du document
     * @return le nom du document correspondant à doc_id
     * @throws SQLException
     */
    public String getDocNameById(int doc_id) throws SQLException {
        String retour = null;
        String query = "SELECT name FROM documents WHERE id='" + doc_id + "'";

        ResultSet result = this.requeteSelect(query);

        if(result.first()){
            retour = result.getString("name");
        }

        result.close();

        return retour;
    }
}
