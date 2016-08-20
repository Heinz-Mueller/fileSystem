import javax.swing.*;

/**
 * Created by Amel on 19.08.2016.
 */
public class ClientGUI
{
    private JTextField clientPortTextField;
    private JButton clientStartenButton;
    private JTextArea clientTextArea1;
    private JButton createButton;
    private JPanel clientPanel;

    //private FSInterface fsserver;
    //enum MENUE { CLOSE, FALSE, BROWSE, SEARCH, CREATE_DIR, CREATE_FILE, DELETE, RENAME, OS_NAME }




    /**
     * Hauptmethode
     * Startet den Client
     * @param //args[] Parameter beim Programm start. Erster Eintrag ist PortNr für Server
     */
    public static void main(String args[])
    {
        //Propertys aus Datei laden
        System.setProperty("java.security.policy","C:\\Program Files\\Java\\jre1.8.0_91\\lib\\security\\java.policy");
        JFrame frame = new JFrame("ClientGUI");
        frame.setContentPane(new ClientGUI().clientPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);


    }
}
