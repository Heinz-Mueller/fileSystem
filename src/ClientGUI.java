import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.io.*;
import java.util.*;
import java.nio.*;
import java.nio.file.*;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

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
    enum MENUE { CLOSE, FALSE, BROWSE, SEARCH, CREATE_DIR, CREATE_FILE, DELETE, RENAME, OS_NAME };

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
        frame.setSize(800, 400);
        clientTextArea.append("Hallo \n\n");
        startClientButton.addActionListener(this);
        browseButton.addActionListener(this);
        seachButton.addActionListener(this);
        createDirButton.addActionListener(this);
        createFileButton.addActionListener(this);
        deleteButton.addActionListener(this);
        renameButton.addActionListener(this);
        OSInfoButton.addActionListener(this);
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

        Object o = e.getSource();

        if(o == startClientButton)
        {
            clientTextArea.append("Starte Server wurde gedückt\n");
            int serverPort;

            try
            {
                serverPort = Integer.parseInt(portTextFeld.getText().trim());
            } catch(Exception er)
            {
                clientTextArea.append("Fehler bei der Port-Eingabe\n");
                return;
            }

            try
            {
                if (System.getSecurityManager() == null)
                {
                    System.setSecurityManager(new SecurityManager());
                }
                Registry registry = LocateRegistry.getRegistry(serverPort);
                this.fsserver = (FSInterface) registry.lookup("FileSystemServer");
            }
            catch(Exception e2)
            {
                System.out.println( "Fehler: " + e2.toString() );
            }
            // Button deaktivieren nach Start
            startClientButton.setEnabled(false);
            // Portfeld deaktivieren nach Start
            portTextFeld.setEditable(false);
        }

        if(o == OSInfoButton)
        {
            try
            {
                client.append("|-------------------------------------------------\n");
                client.append("| Verwendetes OS: " + this.fsserver.getOSName() + "\n");
                client.append("|-------------------------------------------------\n");
                //clientTextArea.setCaretPosition(clientTextArea.getText().length() - 1);
                System.out.println("|-------------------------------------------------");
                System.out.println("| Verwendetes OS: " + this.fsserver.getOSName());
                System.out.println("|-------------------------------------------------");
            }
            catch(Exception eOS)
            {
                System.out.println("Fehler: " + eOS.getMessage());
            }
        }

        if(o == createDirButton)
        {
            JFrame eingabe2 = new JFrame();
            String pfad = JOptionPane.showInputDialog(eingabe2, "Welcher Ordner soll erstellt werden?", "Create Directory", JOptionPane.PLAIN_MESSAGE);
            try
            {
                if( this.fsserver.createDir(pfad) )
                {
                    client.append("Ordner wurde erstellt!\n");
                    System.out.println("Ordner wurde erstellt!");
                }
                else
                {
                    client.append("Ordner konnte NICHT erstellt werden!\n");
                    System.out.println("Ordner konnte NICHT erstellt werden!");
                }
            }
            catch(IOException eDir)
            {
                System.out.println("Fehler: " + eDir.getMessage());
            }
        }

    }

    public static void main(String[] args)
    {
        //Propertys aus Datei laden
        System.setProperty("java.security.policy","C:\\Program Files (x86)\\Java\\jre1.8.0_101\\lib\\security\\java.policy");
        client = new ClientGUI();
    }
}
