import javax.swing.*;
import java.awt.*;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.Serializable;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.io.IOException;
import java.util.*;

import javax.swing.JPanel;

import javax.swing.JFrame;
import javax.swing.event.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;


import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;


/**
 * Created by Eugen Eberle on 20.08.2016.
 */
public class ClientGUI extends JFrame implements ActionListener, TreeModel, Serializable, Cloneable
{
    static ClientGUI client;
    private JPanel clientPanel;
    private JTextField portTextFeld;
    private JButton startClientButton;
    private JTextArea clientTextArea;
    private JButton browseButton;
    private JButton searchButton;
    private JButton createDirButton;
    private JButton createFileButton;
    private JButton deleteButton;
    private JButton renameButton;
    private JButton OSInfoButton;
    private JTextField searchFeld;
    private JLabel searchLabel;
    private JLabel BackgroundLabel;
    private JLabel imageLabel;

    private FSInterface fsserver;
    private FSInterface fsserver2;


    String pfad = "";
    boolean ersteEingabe = true;

    /**
     * Konstruktor
     */
    public ClientGUI()
    {
        JFrame frame = new JFrame("ClientGUI");
        frame.setContentPane(clientPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(900, 400);
        frame.setResizable(false);
        clientTextArea.append("Hallo \n\n");
        //clientTextArea.setBounds(0,0,800,200);????

        //Animated GIF
        ImageIcon ii = new ImageIcon(this.getClass().getResource("geld00008.gif"));
        imageLabel.setIcon(ii);

        startClientButton.addActionListener(this);
        browseButton.addActionListener(this);
        searchButton.addActionListener(this);
        createDirButton.addActionListener(this);
        createFileButton.addActionListener(this);
        deleteButton.addActionListener(this);
        renameButton.addActionListener(this);
        OSInfoButton.addActionListener(this);


        /**
         * Buttons deaktivieren, werden erst nach Verbindung aktiviert
         */
        browseButton.setEnabled(false);
        searchButton.setEnabled(false);
        createDirButton.setEnabled(false);
        createFileButton.setEnabled(false);
        deleteButton.setEnabled(false);
        renameButton.setEnabled(false);
        OSInfoButton.setEnabled(false);
    }

    protected EventListenerList listeners;

    private static final Object LEAF = new Serializable() { };

    private Map map;


    private File root;


    public ClientGUI(File root)
    {
        this.root = root;

        if (!root.isDirectory())
            map.put(root, LEAF);

        this.listeners = new EventListenerList();

        this.map = new HashMap();
    }


    public Object getRoot()
    {
        return root;
    }

    public boolean isLeaf(Object node)
    {
        return map.get(node) == LEAF;
    }

    public int getChildCount(Object node)
    {
        java.util.List children = children(node);

        if (children == null)
            return 0;

        return children.size();
    }

    public Object getChild(Object parent, int index)
    {
        return children(parent).get(index);
    }

    public int getIndexOfChild(Object parent, Object child)
    {
        return children(parent).indexOf(child);
    }

    protected java.util.List children(Object node)
    {
        File f = (File)node;

        Object value = map.get(f);

        if (value == LEAF)
            return null;

        java.util.List children = (java.util.List)value;

        if (children == null)
        {
            File[] c = f.listFiles();

            if (c != null)
            {
                children = new ArrayList(c.length);

                for (int len = c.length, i = 0; i < len; i++)
                {
                    children.add(c[i]);
                    if (!c[i].isDirectory())
                        map.put(c[i], LEAF);
                }
            }
            else
                children = new ArrayList(0);

            map.put(f, children);
        }

        return children;
    }

    public void valueForPathChanged(TreePath path, Object value)
    {
    }

    public void addTreeModelListener(TreeModelListener l)
    {
        listeners.add(TreeModelListener.class, l);
    }

    public void removeTreeModelListener(TreeModelListener l)
    {
        listeners.remove(TreeModelListener.class, l);
    }

    public Object clone() {
        try {
            ClientGUI clone = (ClientGUI) super.clone();

            clone.listeners = new EventListenerList();

            clone.map = new HashMap(map);

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }


    void append(String text)
    {
        clientTextArea.append(text);
        clientTextArea.setCaretPosition(clientTextArea.getText().length() - 1);
    }

    /** Add nodes from under "dir" into curTop. Highly recursive. */

    /*
    DefaultMutableTreeNode addNodes(DefaultMutableTreeNode curTop, File dir)
    {
        String curPath = dir.getPath();
        DefaultMutableTreeNode curDir = new DefaultMutableTreeNode(curPath);
        if (curTop != null)
        { // should only be null at root
            curTop.add(curDir);
        }
        Vector ol = new Vector();
        String[] tmp = dir.list();
        for (int i = 0; i < tmp.length; i++)
            ol.addElement(tmp[i]);
        Collections.sort(ol, String.CASE_INSENSITIVE_ORDER);
        File f;
        Vector files = new Vector();
        // Make two passes, one for Dirs and one for Files. This is #1.
        for (int i = 0; i < ol.size(); i++)
        {
            String thisObject = (String) ol.elementAt(i);
            String newPath;
            if (curPath.equals(".")) {
                newPath = thisObject;
            }
            else
                newPath = curPath + File.separator + thisObject;
            if ((f = new File(newPath)).isDirectory())
            {
                addNodes(curDir, f);
            }
            else
                files.addElement(thisObject);
        }
        // Pass two: for files.
        for (int fnum = 0; fnum < files.size(); fnum++)
            curDir.add(new DefaultMutableTreeNode(files.elementAt(fnum)));
        return curDir;
    }

*/

    /**
     * Button gedrückt
     */
    public void actionPerformed(ActionEvent e)
    {
        /**
         * Die Quelle des Events finden,
         * d.h. welcher Button wurden geklickt?
         */
        Object o = e.getSource();

        if(o == startClientButton)
        {
            int serverPort;
            try
            {
                serverPort = Integer.parseInt(portTextFeld.getText().trim());
            } catch(Exception er)
            {
                JOptionPane.showMessageDialog(null, "Fehler bei der Port-Eingabe", "Port-Nr", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try
            {
                if (System.getSecurityManager() == null)
                {
                    System.setSecurityManager(new SecurityManager());
                }
                Registry registry = LocateRegistry.getRegistry(serverPort);
                //this.fsserver = (FSInterface) registry.lookup("FileSystemServer");
                clientTextArea.append("Verbunden...\n");
            }
            catch(Exception e2)
            {
                System.out.println( "Fehler: " + e2.toString() );
            }

            try {
                //meine: -> FSInterface server = (FSInterface) Naming.lookup("//10.9.40.229:1500/FileSystemServer");
                //this.fsserver = (FSInterface) Naming.lookup("//10.9.41.43:2222/FileSystemServer");
                //this.fsserver2 = (FSInterface) Naming.lookup("//10.9.40.229:2222/FileSystemServer");
                this.fsserver = (FSInterface) Naming.lookup("//10.9.40.229:1500/FileSystemServer");
                //this.fsserver = (FSInterface) Naming.lookup("//192.168.178.31:1500/FileSystemServer");
            }
            catch (Exception ex)
            {
                System.out.println( "Fehler: " + ex.toString() );
            }

            // Start-Button deaktivieren nach Start
            startClientButton.setEnabled(false);
            // Portfeld deaktivieren nach Start
            portTextFeld.setEditable(false);
            //Buttons aktivieren
            browseButton.setEnabled(true);
            searchButton.setEnabled(true);
            createDirButton.setEnabled(true);
            createFileButton.setEnabled(true);
            deleteButton.setEnabled(true);
            renameButton.setEnabled(true);

            OSInfoButton.setEnabled(true);
        }

        if(o == OSInfoButton)
        {
            try
            {
                client.append(" Verwendetes OS: " + this.fsserver.getOSName() + "\n\n");
                //client.append(" Verwendetes OS: " + this.fsserver2.getOSName() + "\n\n");
            }
            catch(Exception eOS)
            {
                System.out.println("Fehler: " + eOS.getMessage());
            }
        }

        if(o == createDirButton)
        {
            JFrame eingabe = new JFrame();
            String pfad = JOptionPane.showInputDialog(eingabe, "Welcher Ordner soll erstellt werden?", "Create Directory", JOptionPane.PLAIN_MESSAGE);
            try
            {
                if( this.fsserver.createDir(pfad) )
                {
                    client.append("Ordner wurde erstellt!\n");
            }
                else
                {
                    client.append("Ordner konnte NICHT erstellt werden!\n");
                    JOptionPane.showMessageDialog(null, "Ordner konnte NICHT erstellt werden", "Create Directory", JOptionPane.ERROR_MESSAGE);
                }
            }
            catch(IOException eDir)
            {
                System.out.println("Fehler: " + eDir.getMessage());
            }
        }

        if(o == createFileButton)
        {
            JFrame eingabe = new JFrame();
            String pfad = JOptionPane.showInputDialog(eingabe, "Welche Datei soll erstellt werden?", "Create File", JOptionPane.PLAIN_MESSAGE);
            try
            {
                if( this.fsserver.createFile(pfad) )
                {
                    client.append("Datei wurde erstellt!\n");
                }
                else
                {
                    client.append("Datei konnte NICHT erstellt werden!\n");
                    JOptionPane.showMessageDialog(null, "Datei konnte NICHT erstellt werden!n", "Create File", JOptionPane.ERROR_MESSAGE);
                }
            }
            catch(IOException eFile)
            {
                System.out.println("Fehler: " + eFile.getMessage());
            }
        }

        if(o == browseButton)
        {
            /*
            String erg;
            //String erg2; //meins
            String [] dirListe;
            //String [] dirListe2;
            String [] fileListe;
            //String [] fileListe2;

            JFrame eingabe = new JFrame();
            String pfad = JOptionPane.showInputDialog(eingabe, "Welcher Ordner soll untersucht werden?", "Browse", JOptionPane.PLAIN_MESSAGE);

            try
            {
                erg = this.fsserver.browseDirs(pfad);
                dirListe = erg.split("[;]");

                erg = this.fsserver.browseFiles(pfad);
                fileListe = erg.split("[;]");

                //erg2 = this.fsserver2.browseDirs(pfad);
                //dirListe2 = erg.split("[;]");

                //erg2 = this.fsserver2.browseFiles(pfad);
                //fileListe2 = erg.split("[;]");

                client.append("File-Liste1:\n");
                client.append("---------------------------------------------------------------\n");
                for(int i=0; i<fileListe.length; i++)
                {
                    client.append( fileListe[i] + "\n");
                }
                client.append("\n\nDirectory-Liste:\n");
                client.append("---------------------------------------------------------------\n");
                for(int j=0; j<dirListe.length; j++)
                {
                    client.append(dirListe[j] + "\n");
                }
                /*
                client.append("\nMeine File-Liste:\n");
                client.append("---------------------------------------------------------------\n");
                for(int i=0; i<fileListe2.length; i++)
                {
                    client.append( fileListe2[i] + "\n");
                }
                client.append("\nDirectory-Liste:\n");
                client.append("---------------------------------------------------------------\n");
                for(int j=0; j<dirListe2.length; j++)
                {
                    client.append(dirListe2[j] + "\n");
                }
                */
            /*
            }

            catch(IOException eBrowse)
            {
                System.out.println("Fehler: " + eBrowse.getMessage());
            }
            */
            /*
            // Make a tree list with all the nodes, and make it a JTree
            //JTree tree = new JTree(addNodes(null, new File(pfad) ));
            JTree tree = new JTree(addNodes(null, new File("\\") ));

            // Lastly, put the JTree into a JScrollPane.
            JScrollPane scrollpane = new JScrollPane();
            scrollpane.getViewport().add(tree);
            add(BorderLayout.CENTER, scrollpane);


            //Where instance variables are declared:
            */
            File root = new File("\\");

            if (!root.exists())
            {
                System.err.println(root+ ": No such file or directory");
                System.exit(2);
            }

            JTree tree = new JTree(new ClientGUI(root));

            JFrame f = new JFrame(root.toString());

            f.addWindowListener(new WindowAdapter()
            {
                public void windowClosing(WindowEvent e)
                {
                    System.exit(0);
                }
            });

            f.getContentPane().add(new JScrollPane(tree));

            f.pack();
            f.setVisible(true);
            pack();
            setVisible(true);
        }

        if(o == searchButton)
        {

            String erg;
            String [] fileListe;

            //Erste Eingabe: Was suchen Sie?
            //Text im Label ist die Bedingung
            if (ersteEingabe == true)
            {
                pfad = searchFeld.getText();
                searchLabel.setText("Wo soll gesucht werden?");
                searchFeld.setText("");
                ersteEingabe = false;
            }
            else if (ersteEingabe == false)
            {
                String startDir = searchFeld.getText();
                try
                {
                    erg = this.fsserver.search(pfad, startDir);
                    fileListe = erg.split("[;]");
                    client.append("Found-Files: \n");
                    client.append("---------------------------------------------------------------\n");
                    for(int i=0; i<fileListe.length; i++)
                    {
                        client.append(fileListe[i] + "\n");
                    }
                }
                catch(IOException eSeach)
                {
                    System.out.println("Fehler: " + eSeach.getMessage());
                }
                searchLabel.setText("Was soll gesucht werden?");
                searchFeld.setText("");
                ersteEingabe = true;
            }




            //JFrame eingabe = new JFrame();
            //String pfad = JOptionPane.showInputDialog(eingabe, "Was soll gesucht werden?", "Seach", JOptionPane.PLAIN_MESSAGE);
            //String startDir = JOptionPane.showInputDialog(eingabe, "Wo soll gesucht werden?", "Seach", JOptionPane.PLAIN_MESSAGE);

        }

        if(o == deleteButton)
        {
            JFrame eingabe = new JFrame();
            String pfad = JOptionPane.showInputDialog(eingabe, "Was soll gelöscht werden?", "Delete", JOptionPane.PLAIN_MESSAGE);
            try
            {
                if( this.fsserver.delete(pfad) )
                {
                    client.append("Ordner oder Datei wurde geloescht!\n");
                    JOptionPane.showMessageDialog(null, "Ordner oder Datei wurde geloescht!", "Delete", JOptionPane.INFORMATION_MESSAGE);
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "Ordner oder Datei konnte NICHT geloescht werden!", "Delete", JOptionPane.ERROR_MESSAGE);
                    System.out.println("Ordner oder Datei konnte NICHT geloescht werden!");
                }
            }
            catch(IOException eDelete)
            {
                System.out.println("Fehler: " + eDelete.getMessage());
            }
        }

        if(o == renameButton)
        {
            JFrame eingabe = new JFrame();
            String oldName = JOptionPane.showInputDialog(eingabe, "Was soll umbeannt werden?", "Rename", JOptionPane.PLAIN_MESSAGE);
            String newName = JOptionPane.showInputDialog(eingabe, "Wie lautet die neue Bezeichnung?", "Rename", JOptionPane.PLAIN_MESSAGE);
            try
            {
                if( this.fsserver.rename(oldName, newName) )
                {
                    System.out.println("Ordner oder Datei wurde umbenannt!");
                    client.append("Ordner oder Datei wurde umbenannt!\n");
                    JOptionPane.showMessageDialog(null, "Ordner oder Datei wurde umbenannt!", "Rename", JOptionPane.INFORMATION_MESSAGE);
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "Ordner oder Datei konnte NICHT umbenannt werden!", "Rename", JOptionPane.ERROR_MESSAGE);
                    System.out.println("Ordner oder Datei konnte NICHT umbenannt werden!");
                }
            }
            catch(IOException eRename)
            {
                System.out.println("Fehler: " + eRename.getMessage());
            }
        }

    }

    public static void main(String[] args)
    {
        //Propertys aus Datei laden
        System.setProperty("java.security.policy","C:\\Program Files\\Java\\jre1.8.0_91\\lib\\security\\java.policy");
        client = new ClientGUI();

    }
}
