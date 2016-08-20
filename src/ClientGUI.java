import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Amel on 19.08.2016.
 */
public class ClientGUI implements ActionListener
{
    private JTextField clientPortTextField;
    private JButton clientStartenButton;
    private JTextArea clientTextArea1;
    private JButton createDirButton;
    private JPanel clientPanel;

    private FSInterface fsserver;
    enum MENUE { CLOSE, FALSE, BROWSE, SEARCH, CREATE_DIR, CREATE_FILE, DELETE, RENAME, OS_NAME }

    FileSystemClient fsc = null;
    int serverPort = 0;
    int eingabe = -1;
    FileSystemClient.MENUE meue_eingabe = FileSystemClient.MENUE.FALSE;

    public ClientGUI()
    {
        clientStartenButton.addActionListener(this);
        createDirButton.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e)
    {
        Object o = e.getSource();
        if(o == clientStartenButton)
        {

                clientTextArea1.append("Starte Client wurde gedückt\n");
            try
            {
                serverPort = Integer.parseInt(clientPortTextField.getText().trim());

                fsc = new FileSystemClient(serverPort);

            }
            catch(Exception ee)
            {
                clientTextArea1.append("Fehler bei der Port-Eingabe\n");
                return;
            }

        }
        if (o == createDirButton)
        {

        }
    }

    private void createDir()
    {

    }

    /**
     * Hauptmethode
     * Startet den Client
     * @param //args[] Parameter beim Programm start. Erster Eintrag ist PortNr für Server
     */
    public static void main(String args[])
    {
        System.setProperty("java.security.policy","C:\\Program Files\\Java\\jre1.8.0_91\\lib\\security\\java.policy");
        JFrame frame = new JFrame("ClientGUI");
        frame.setContentPane(new ClientGUI().clientPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(600, 400);

    }
}
