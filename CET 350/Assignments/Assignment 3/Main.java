/* Known Bug list:
 * 
 * going too far back into the parent directory breaks the program, i think we aren't supposed to be able to go back that far
 * copying files still doesn't work at all
 * my .txt's aren't showing up in the list
 * selecting target removes removes part of the directory (might be intentional, ill have to look at what this is supposed to do)
 * the pack function shrinks the screen, it is currently commented out
 * I don't know if this works with args[], I haven't tested it yet, not sure how you do that on eclipse
 * 
 * NOT A BUG: the +'s seem buggy, but I actually think they are working how he wants them
 */



import java.io.*;
import java.awt.*;
import java.awt.event.*;

public class Main extends Frame implements WindowListener, ActionListener
{
	static final long serialVersionUID = 1L;
	
	//globals:
	File curDir;
	String filenames[] = new String[100];			//also size 100? don't know yet
	Boolean Source, Target, OutFile;
	
	//create components
	//labels
	Label SourceLabel = new Label("Source:");			//don't change
	Label SourceFileLabel = new Label();
	Label FileNameLabel = new Label("File Name:");		//don't change
	Label TargetDirectoryLabel = new Label("Select Target Directory: ");	//don't change
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
		
		
		if(args.length > 0)
		{
			File dir = new File(args[0]);
			if(!dir.isDirectory())
			{
				System.out.println("File is not a directory.");
			}
			else
			{
				new Main(new File(dir.getAbsolutePath()));
			}
		}
		else
		{
			new Main(new File(new File(System.getProperty("user.dir")).getAbsolutePath()));
		}
	}
	
	
	/////////////////////////////  Constructor   ////////////////////////
	Main(File dir)
	{
		curDir = dir;			//set current directory to starting directory
		Source = false;			//set flags to false
		Target = false;
		OutFile = false;
		
		
		//update title
		this.setTitle(curDir.getAbsolutePath());
		
		//add action listeners
		TargetButton.addActionListener(this);
		OKButton.addActionListener(this);
		TargetFileNameTF.addActionListener(this);
		FileList.addActionListener(this);
		
		
		
		
		//constraints and layout initialization
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
		
		
		//add components to GridBag
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
		TargetButton.setEnabled(false);
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
		
		
		//this.pack();			//he wants this in there, but it makes the window start out small
		this.setVisible(true);
		this.addWindowListener(this);
		display(null);		//still needs created
	}
	
	
	////////////////////////    Display Method     //////////////////////
	void display(String name)  //name is a File or Directory
	{
		if(name == null)
		{
			//skip the else statements if null
		}
		else if(name.equals(".."))			// this is part is a bit confusing right now, we can go back to this later
		{
			curDir = new File(curDir.getParent());
		}
		else
		{
			File f = new File(curDir, name);
			if(f.isDirectory())
			{
				curDir = new File(curDir, name);
			}
			else if(!Source || !Target)
			{
				SourceFileLabel.setText(curDir.getAbsolutePath() + "\\" + name);
				Source = true;
				TargetButton.setEnabled(true);
			}
			else
			{
				TargetFileNameTF.setText(name);
				OutFile = true;
			}
		}
		
		filenames = curDir.list();
		this.setTitle(curDir.getAbsolutePath());
		
		for(int i = 0; i < filenames.length; i++)
		{
			File f = new File(curDir, filenames[i]);
			if(f.isDirectory())
			{
				String[] children = f.list();
				for(int ii = 0; ii < children.length; ii++)
				{
					if(new File(f, children[ii]).isDirectory())
					{
						filenames[i] += " +";
						ii = children.length;
					}
				}
			}
		}
		
		FileList.removeAll();
		if(curDir.getParent() != null)
		{
			FileList.add("..");
		}
		for(int i = 0; i < filenames.length; i++)
		{
			FileList.add(filenames[i]);
		}
		
	}

	///////////////////////////////  Copy File  ////////////////////////////////////////////
	void CopyFile()
	{
		if(Source && Target && OutFile)
		{
			int c;
			
			try
			{
				BufferedReader infile = new BufferedReader(new FileReader(SourceFileLabel.getText()));
				PrintWriter outfile = new PrintWriter(new FileWriter(TargetFileNameTF.getText()));
				
				//test to see if file will be overwritten, and provide an appropriate message if so
				while((c = infile.read()) != -1)
				{
					outfile.write(c);
				}
			}
			catch(IOException e)
			{
				MessagesLabel.setText("IOException");
			}

		}
	}
	
	
	
	
	
	/////////////////   Action Listener    //////////////////////
	public void actionPerformed(ActionEvent e)
	{
		String filename;
		Object source = e.getSource();
		
		if(source == TargetFileNameTF)
		{
			MessagesLabel.setText("");
			filename = TargetFileNameTF.getText();
			
			if(filename.length() != 0)
			{
				OutFile = true;
				CopyFile();
			}
			else
			{
				MessagesLabel.setText(MessagesLabel.getText() + " Target file not specified.");
			}
		}
		
		if(source == OKButton)
		{
			MessagesLabel.setText("");
			filename = TargetFileNameTF.getText();
			
			if(filename.length() != 0)
			{
				OutFile = true;
				CopyFile();
			}
			else
			{
				MessagesLabel.setText(MessagesLabel.getText() + " Target file not specified.");
			}
		}
		
		if(source == TargetButton)
		{
			MessagesLabel.setText("");
			SourceFileLabel.setText(curDir.getAbsolutePath());
			Target = true;
		}
		
		if(source == FileList)
		{
			MessagesLabel.setText("");
			String item = FileList.getSelectedItem();   //get the item that was selected
			if(item != null)
			{
				if(item.endsWith(" +"))			//trim the " +" off of the directory name
				{
					item = item.substring(0, item.length()-2);
				}
				display(item);					//display the new directory
			}
		}
	}
	
	
	
	
	//////////////WindowListener methods  //////////////////////////
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
