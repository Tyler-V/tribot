package scripts.starfox.swing;

import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.MouseAdapter;
import javax.swing.JTextField;

/**
 * @author Nolan
 */
public class JPromptField extends JTextField {
    
    private String prompt;
    
    public JPromptField(String prompt) {
        super();
        this.prompt = prompt;
        showPrompt();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                clearPrompt();
            }
        });
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (getText() == null || getText().isEmpty()) {
                    showPrompt();
                }
            }
        });
    }
    
    public String getPrompt() {
        return this.prompt;
    }
    
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
    
    public void showPrompt() {
        setForeground(new Color(150, 150, 150, 200));
        setText(getPrompt());
    }
    
    public void clearPrompt() {
        setForeground(null);
        if (getText() != null && getText().equals(prompt)) {
            setText(null);
        }
    }
}