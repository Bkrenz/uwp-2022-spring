import java.awt.*;
import java.awt.event.*;

public class Program3 {

    public static void main(String[] args) {

    }
    
}


class FileCopyGUI extends Frame implements ActionListener, WindowListener{

    private File currentDirectory;
    private List fileList;


    public FileCopyGUI(String directory) {
        this.currentDirectory = directory;
    }

    public void initialize() {
        // Setup the window
        this.setTitle(this.currentDirectory);
        this.setBounds(20, 20, 900, 500);

        // Setup the layout
        GridBagLayout layout = new GridBagLayout();
        this.setLayout(layout);

        layout.columnWeights = new double[] {1, 9, 1};
        layout.columnWidths = new int[] {1, 9, 1};
        layout.rowWeights = new double[] {10, 1, 1, 1, 1};
        layout.rowWidths = new int[] {10, 1, 1, 1, 1};
        
        // Setup the Constraints
        GridBagConstraints constraints = new GridBagConstraints();
        
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.BOTH;

        // Add the File List
        c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 0;
		c.gridy = 0;
		displ.setConstraints(FileList, c);
		this.add(FileList);

        // Add the Source Label
        c.gridwidth = 1;
        c.gridy = 1;
        Label sourceLabel = new SourceLabel("Source: ");
        sourceLabel.addActionListener(this);
        layout.setConstraints(sourceLabel, constraints);
        this.add(sourceLabel);

        // Add the 

    }

}