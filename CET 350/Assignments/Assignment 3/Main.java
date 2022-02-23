/**
 * Program 3, CET 350
 * 
 * Group 5
 * @author Robert Krency, kre1188@calu.edu
 * @author Kevin Reisch, rei3819@calu.edu
 */

import java.io.*;
import java.awt.*;
import java.awt.event.*;

public class Main extends Frame implements WindowListener, ActionListener
{
	static final long serialVersionUID = 1L;
	
	//globals:
	File curDir;
	String sourceFileName, targetFileName, targetFileDirectory;
	Boolean flagSourceFileSet, flagTargetDirectorySet, flagTargetFileSet;
	
	//create components
	//labels
	Label SourceLabel = new Label("Source:");			//don't change
	Label SourceFileLabel = new Label();
	Label FileNameLabel = new Label("File Name:");		//don't change
	Label TargetDirectoryLabel = new Label("Select Target Directory: ");
	Label MessagesLabel = new Label();
	
	//buttons
	Button TargetButton = new Button("Target");
	Button OKButton = new Button("OK");
	
	//textfields
	TextField TargetFileNameTF = new TextField();
	
	//lists
	List FileList = new List();
	
	
	
	/////////////////////////  main   ///////////////////////////
	public static void main(String[] args)
	{

		// If the user supplies a valid directory on the command line,
		// use that as the starting point. Otherwise, use the current
		// working directory as the starting point.
		File directory = new File(new File(System.getProperty("user.dir")).getAbsolutePath());

		if (args.length > 0)
		{
			File inputDirectory = new File(args[0]);
			if (inputDirectory.isDirectory())
				directory = inputDirectory;
		}

		new Main(directory);
	}
	
	
	/////////////////////////////  Constructor   ////////////////////////
	Main(File dir)
	{
		curDir = dir;			//set current directory to starting directory
		
		flagSourceFileSet = false;			//set flags to false
		flagTargetDirectorySet = false;
		flagTargetFileSet = false;
		
		this.sourceFileName = "";
		this.targetFileDirectory = "";
		this.targetFileName = "";

		//update title
		this.setTitle(curDir.getAbsolutePath());
		
		//add action listeners
		TargetButton.addActionListener(this);
		OKButton.addActionListener(this);
		TargetFileNameTF.addActionListener(this);
		FileList.addActionListener(this);
		
		//constraints and layout initialization
		GridBagConstraints constraints = new GridBagConstraints();
		GridBagLayout layout = new GridBagLayout();
		
		layout.columnWeights = new double[] {1, 9, 1};
		layout.rowWeights = new double[] {10, 1, 1, 1, 1};
		layout.rowHeights = new int[] {10, 1, 1, 1, 1};
		layout.columnWidths = new int[] {1, 9, 1};

		this.setLayout(layout);
		FileList.setSize(300, 700);
		
		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.BOTH;
		
		
		//add components to GridBag
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.gridx = 0;
		constraints.gridy = 0;
		layout.setConstraints(FileList, constraints);
		this.add(FileList);
		
		constraints.gridwidth = 1;
		constraints.gridy = 1;
		layout.setConstraints(SourceLabel, constraints);
		this.add(SourceLabel);
		
		constraints.gridy = 2;
		layout.setConstraints(TargetButton, constraints);
		TargetButton.setEnabled(false);
		this.add(TargetButton);
		
		constraints.gridy = 3;
		layout.setConstraints(FileNameLabel, constraints);
		this.add(FileNameLabel);
		
		constraints.gridy = 4;
		constraints.gridwidth = 3;
		layout.setConstraints(MessagesLabel, constraints);
		this.add(MessagesLabel);
		
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		layout.setConstraints(SourceFileLabel, constraints);
		this.add(SourceFileLabel);
		
		constraints.gridy = 2;
		layout.setConstraints(TargetDirectoryLabel, constraints);
		this.add(TargetDirectoryLabel);
		
		constraints.gridy = 3;
		layout.setConstraints(TargetFileNameTF, constraints);
		this.add(TargetFileNameTF);
		
		constraints.gridx = 2;
		layout.setConstraints(OKButton,constraints);
		this.add(OKButton);
		
		
		this.setBounds(0, 0, 900, 500);
		//this.pack();
		//the program resizes correctly without it, so we excluded it


		// Show the window and set up the display
		this.setVisible(true);
		this.addWindowListener(this);
		display(null);
	}
	
	
	////////////////////////    Display Method     //////////////////////
	void display(String name)  //name is a File or Directory
	{
		if(name == null)
		{
			//skip the else statements if null
		}
		else if(name.equals(".."))			// go to parent directory
		{
			curDir = new File(curDir.getParent());
		}
		else			//go to subdirectory or file
		{
			File f = new File(curDir, name);
			if(f.isDirectory())
			{
				curDir = f;
			}
			else if(!flagSourceFileSet || !flagTargetDirectorySet)
			{
				this.setSourceFile(curDir.getAbsolutePath() + "\\" + name);
				TargetButton.setEnabled(true);
			}
			else
			{
				this.setTargetFile(name);
			}
		}
		
		// Update the title
		this.setTitle(curDir.getAbsolutePath());

		// Clear the FileList
		this.FileList.removeAll();

		// Check for a parent directory
		if (this.curDir.getParent() != null)
			this.FileList.add("..");

		// Add all children files and directories
		// 	If a child directory contains subdirectories, add a + to its name in the list
		String directoryModifier;
		if (this.curDir.listFiles() != null)
			for (File child : this.curDir.listFiles()) {
				directoryModifier = "";
				if (child.isDirectory() && !child.isHidden() && child.listFiles() != null) 	//lets you access root directories
					for (File subChild : child.listFiles())
						if (subChild.isDirectory())
							directoryModifier = " +";
				
				this.FileList.add(child.getName() + directoryModifier);
			}		
	}

	///////////////////////////////  Copy File  ////////////////////////////////////////////
	void CopyFile()
	{
		if(flagSourceFileSet && flagTargetDirectorySet && flagTargetFileSet)
		{
			int c;
			
			try
			{
				//create input and output files
				File inputFile = new File(this.sourceFileName);
				File outputFile = new File(this.targetFileDirectory, this.targetFileName);
				
				//premptive success label, program will catch error of it won't copy
				if(outputFile.exists())
				{
					this.setMessageLabel("Output file exists, overwriting...");
				}
				else
				{
					this.setMessageLabel("File copied successfully.");
				}

				//open files
				BufferedReader infile = new BufferedReader(new FileReader(inputFile));
				PrintWriter outfile = new PrintWriter(new FileWriter(outputFile));
				
				//copy file
				while((c = infile.read()) != -1)
				{
					outfile.write(c);
				}
				
				//close files
				outfile.close();
				infile.close();

				// Reset the state, keeping the current directory
				this.setSourceFile("");
				this.setTargetDirectory("Select Target Directory:");
				this.setTargetFile("");
				this.TargetButton.setEnabled(false);

				//reset flags
				this.flagSourceFileSet = false;
				this.flagTargetDirectorySet = false;
				this.flagTargetFileSet = false;
				
			}
			catch(FileNotFoundException e)
			{
				MessagesLabel.setText("Error opening file.");
			}
			catch(IOException e)
			{
				MessagesLabel.setText("An IO Error occurred.");
			}
			
			//update the list to show new files
			display(null);
		}
	}
	
	
	
	
	/////////////////   Action Listener    //////////////////////
	public void actionPerformed(ActionEvent e)
	{
		String filename;
		Object source = e.getSource();
		
		//pressing enter and ok do the same thing
		//activates file copy
		if(source == TargetFileNameTF || source == OKButton)
		{
			this.clearMessageLabel();
			filename = TargetFileNameTF.getText();
			
			if(SourceFileLabel.getText().length() == 0 || flagTargetDirectorySet == false)
			{
				this.setMessageLabel("Source file not specified.");
			}
			else if(filename.length() != 0)
			{
				this.setTargetFile(filename);
				CopyFile();
			}
			else
			{
				this.setMessageLabel("Target file not specified.");
			}
		}
		
		//set to target mode, and update target path
		else if(source == TargetButton)
		{
			this.clearMessageLabel();
			this.setTargetDirectory(this.curDir.getAbsolutePath());
		}
		
		//update the file list
		else if(source == FileList)
		{
			this.clearMessageLabel();
			String item = FileList.getSelectedItem();   //get the item that was selected
			if(item != null)
			{
				// Trim the " +" off of the directory name
				if(item.endsWith(" +"))			
					item = item.substring(0, item.length()-2);

				// Display the new directory
				display(item);
			}
		}
	}

////////////////////////// Other Methods  //////////////////////////////////////////
	private void setSourceFile(String name) {
		this.sourceFileName = name;
		this.SourceFileLabel.setText(name);
		this.flagSourceFileSet = true;
	}

	//sets target file name and flag
	//target and source file can't be the same
	private void setTargetFile(String name) {
		if (!((new File(this.targetFileDirectory, name).getAbsolutePath()).equals(this.sourceFileName))) {
			this.targetFileName = name;
			this.TargetFileNameTF.setText(name);
			this.flagTargetFileSet = true;
		}
		else
			this.setMessageLabel("Target file cannot be the same as Source File.");
	}

	//updates target directory and flag
	private void setTargetDirectory(String name) {
		this.targetFileDirectory = name;
		this.TargetDirectoryLabel.setText(name);
		this.flagTargetDirectorySet = true;
	}

	//sets the text of the message label
	private void setMessageLabel(String message) {
		this.MessagesLabel.setText(message);
	}

	//clears the message label
	private void clearMessageLabel() {
		this.setMessageLabel("");
	}
		
	
	
	////////////// WindowListener methods //////////////////////////
	public void windowClosing(WindowEvent e)
	{
		this.removeWindowListener(this);
		
		this.FileList.removeActionListener(this);
		this.TargetButton.removeActionListener(this);
		this.OKButton.removeActionListener(this);
		this.TargetFileNameTF.removeActionListener(this);
		
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
