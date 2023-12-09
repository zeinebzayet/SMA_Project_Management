import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class MessageDisplay extends JFrame {
    private JTextPane messagePane;

    public MessageDisplay() {
        setTitle("Agent Messaging Display");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        messagePane = new JTextPane();
        messagePane.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(messagePane);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
    }

    public void appendMessage(String message, Icon icon) {
        StyledDocument doc = messagePane.getStyledDocument();
        SimpleAttributeSet style = new SimpleAttributeSet();


        try {
            StyleConstants.setComponent(style, new JLabel(message, icon, SwingConstants.LEADING));
            doc.insertString(doc.getLength(), "Ignored Text", style); // Text is ignored, just a placeholder
            doc.insertString(doc.getLength(), "\n", style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }



}
