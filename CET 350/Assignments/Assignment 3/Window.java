package GUIFileCopy;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class Window extends Frame implements WindowListener
{
	private static final long serialVersionUID = 1L;
	Label SourceLabel = new Label("Source:");
	Label SourceFileLabel = new Label();
	Label FileNameLabel = new Label("File Name:");
	Label TargetDirectoryLabel = new Label("Select Target Directory: ");
	Label MessagesLabel = new Label();
	
	Button TargetButton = new Button("Target");
	Button OKButton = new Button("OK");
	
	TextField TargetFileNameTF = new TextField();
	
	List FileList = new List();
	
	Window()
	{
		this.setTitle("File Path");
		GridBagConstraints c = new GridBagConstraints();
		GridBagLayout displ = new GridBagLayout();
		
		double colWeight[] = {1, 9, 1};
		double rowWeight[] = {10, 1, 1, 1, 1};
		int colWidth[] = {1, 9, 1};
		int rowHeight[]= {10, 1, 1, 1, 1};
		
		displ.columnWeights = colWeight;
		displ.rowWeights = rowWeight;
		displ.rowHeights = rowHeight;
		displ.columnWidths = colWidth;
		
		this.setBounds(20, 20, 900, 500);
		this.setLayout(displ);
		
		c.anchor = GridBagConstraints.NORTHWEST;
		c.weightx = 1;
		c.weighty = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.BOTH;
		
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 0;
		c.gridy = 0;
		displ.setConstraints(FileList, c);
		this.add(FileList);
		
		c.gridwidth = 1;
		c.gridy = 1;
		displ.setConstraints(SourceLabel, c);
		this.add(SourceLabel);
		
		c.gridy = 2;
		displ.setConstraints(TargetButton, c);
		this.add(TargetButton);
		
		c.gridy = 3;
		displ.setConstraints(FileNameLabel, c);
		this.add(FileNameLabel);
		
		c.gridy = 4;
		displ.setConstraints(MessagesLabel, c);
		this.add(MessagesLabel);
		
		c.gridx = 1;
		c.gridy = 1;
		displ.setConstraints(SourceFileLabel, c);
		this.add(SourceFileLabel);
		
		c.gridy = 2;
		displ.setConstraints(TargetDirectoryLabel, c);
		this.add(TargetDirectoryLabel);
		
		c.gridy = 3;
		displ.setConstraints(TargetFileNameTF, c);
		this.add(TargetFileNameTF);
		
		c.gridx = 2;
		displ.setConstraints(OKButton,c);
		this.add(OKButton);
		
		this.setVisible(true);
		this.addWindowListener(this);
	}
	
	public void windowClosing(WindowEvent e)
	{
		this.removeWindowListener(this);
		this.dispose();
	}
	
	public void windowClosed(WindowEvent e)
	{
		
	}
	
	public void windowOpened(WindowEvent e)
	{
		
	}
	
	public void windowActivated(WindowEvent e)
	{
		
	}
	
	public void windowDeactivated(WindowEvent e)
	{
		
	}
	
	public void windowIconified(WindowEvent e)
	{
		
	}
	
	public void windowDeiconified(WindowEvent e)
	{
		
	}
}
