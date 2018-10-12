package scripts.starfox.swing.documents;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * @author Nolan
 */
public class NumberDocument extends PlainDocument {

    @Override
    public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
        if (str == null) {
            return;
        }
        char[] numbers = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        for (char strChar : str.toCharArray()) {
            for (char strChar2 : numbers) {
                if (strChar == strChar2) {
                    super.insertString(offset, str, attr);
                    return;
                }
            }
        }
    }
}
