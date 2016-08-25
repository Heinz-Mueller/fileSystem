import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.io.IOException;

import javax.swing.JPanel;

import javax.swing.JFrame;



/**
 * Created by Eugen Eberle on 20.08.2016.
 */
public class ClientGUI extends JFrame implements ActionListener
{
    static ClientGUI client;
    private JPanel clientPanel;
    private JTextField portTextFeld;
    private JButton startClientButton;
    private JTextArea clientTextArea;
    private JButton browseButton;
    private JButton seachButton;
    private JButton createDirButton;
    private JButton createFileButton;
    private JButton deleteButton;
    private JButton renameButton;
    private JButton OSInfoButton;

    private FSInterface fsserver;

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


        startClientButton.addActionListener(this);
        browseButton.addActionListener(this);
        seachButton.addActionListener(this);
        createDirButton.addActionListener(this);
        createFileButton.addActionListener(this);
        deleteButton.addActionListener(this);
        renameButton.addActionListener(this);
        OSInfoButton.addActionListener(this);


        /**
         * Buttons deaktivieren, werden erst nach Verbindung aktiviert
         */
        browseButton.setEnabled(false);
        seachButton.setEnabled(false);
        createDirButton.setEnabled(false);
        createFileButton.setEnabled(false);
        deleteButton.setEnabled(false);
        renameButton.setEnabled(false);
        OSInfoButton.setEnabled(false);

    }





    void append(String text)
    {
        clientTextArea.append(text);
        clientTextArea.setCaretPosition(clientTextArea.getText().length() - 1);
    }


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
                this.fsserver = (FSInterface) Naming.lookup("//10.9.41.43:2222/FileSystemServer");
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
            seachButton.setEnabled(true);
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
            String erg;
            String [] dirListe;
            String [] fileListe;

            JFrame eingabe = new JFrame();
            String pfad = JOptionPane.showInputDialog(eingabe, "Welcher Ordner soll untersucht werden?", "Browse", JOptionPane.PLAIN_MESSAGE);
            try
            {
                erg = this.fsserver.browseDirs(pfad);
                dirListe = erg.split("[;]");

                erg = this.fsserver.browseFiles(pfad);
                fileListe = erg.split("[;]");

                client.append("File-Liste:\n");
                client.append("---------------------------------------------------------------\n");
                for(int i=0; i<fileListe.length; i++)
                {
                    client.append( fileListe[i] + "\n");
                }
                client.append("\nDirectory-Liste:\n");
                client.append("---------------------------------------------------------------\n");
                for(int j=0; j<dirListe.length; j++)
                {
                    client.append(dirListe[j] + "\n");
                }
            }
            catch(IOException eBrowse)
            {
                System.out.println("Fehler: " + eBrowse.getMessage());
            }
        }

        if(o == seachButton)
        {
            String erg;
            String [] fileListe;

            JFrame eingabe = new JFrame();
            String pfad = JOptionPane.showInputDialog(eingabe, "Was soll gesucht werden?", "Seach", JOptionPane.PLAIN_MESSAGE);
            String startDir = JOptionPane.showInputDialog(eingabe, "Wo soll gesucht werden?", "Seach", JOptionPane.PLAIN_MESSAGE);
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
