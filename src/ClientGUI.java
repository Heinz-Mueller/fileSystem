import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.io.*;
import java.nio.file.*;
import java.rmi.*;

/**
 * Created by Amel on 19.08.2016.
 */
//public class ClientGUI extends JFrame {
public class ClientGUI {
    private JTextField clientPortTextField;
    private JButton clientStartenButton;
    private JTextArea clientTextArea1;
    private JButton createButton;
    private JPanel clientPanel;

    private FSInterface fsserver;
    //enum MENUE { CLOSE, FALSE, BROWSE, SEARCH, CREATE_DIR, CREATE_FILE, DELETE, RENAME, OS_NAME }

    public ClientGUI()
    {
        JFrame frame = new JFrame("ClientGUI");
        frame.setContentPane(clientPanel);
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(600, 400);
        clientTextArea1.append("Hallo \n\n");
        //clientStartenButton.addActionListener(this);
    }

    /**
     * Hauptmethode
     * Startet den Client
     * @param //args[] Parameter beim Programm start. Erster Eintrag ist PortNr für Server
     */
    public static void main(String args[])
    {
        //Propertys aus Datei laden
        System.setProperty("java.security.policy","C:\\Program Files\\Java\\jre1.8.0_91\\lib\\security\\java.policy");

    }
}
