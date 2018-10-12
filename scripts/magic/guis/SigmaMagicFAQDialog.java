package scripts.magic.guis;

import javax.swing.JFrame;

/**
 * @author Nolan
 */
public class SigmaMagicFAQDialog extends javax.swing.JDialog {

    public SigmaMagicFAQDialog(JFrame parent) {
        super(parent, false);
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        basePanel = new javax.swing.JPanel();
        scrollPane = new javax.swing.JScrollPane();
        faqLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sigma Magic FAQ");
        setResizable(false);

        basePanel.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        scrollPane.setBorder(null);

        faqLabel.setText("<html>\n<b>\"I have a question that is not in the FAQ. What do I do?\"</b>\nYou can ask the question on the script thread, pm me on tribot, or add me on skype.\n\n<b>\"The script keeps stopping right after I start it, what gives?\"</b>\nFirst, make sure that you have the correct runes and a high enough magic level to cast the spell you selected.Once you've done that, try again. If it still does not work, restart your tribot client. If restarting your clientdoes not work, make sure to follow instructions for reporting bugs found on the script thread.\n\n<b>\"I'm having trouble with loading my saved templates, what do I do?\"</b>\nIf your templates aren't loading correctly, simply delete them using the <b>\"Delete Current Template\"</b> button.\n</html>");
        faqLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        scrollPane.setViewportView(faqLabel);

        javax.swing.GroupLayout basePanelLayout = new javax.swing.GroupLayout(basePanel);
        basePanel.setLayout(basePanelLayout);
        basePanelLayout.setHorizontalGroup(
            basePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(basePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPane)
                .addContainerGap())
        );
        basePanelLayout.setVerticalGroup(
            basePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(basePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPane)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(basePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(basePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel basePanel;
    private javax.swing.JLabel faqLabel;
    private javax.swing.JScrollPane scrollPane;
    // End of variables declaration//GEN-END:variables

}
