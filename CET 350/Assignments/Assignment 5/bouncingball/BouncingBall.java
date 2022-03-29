package bouncingball;

/*
Program 5, CET 350
* 
* Group 5
* @author Robert Krency, kre1188@calu.edu
* @author Kevin Reisch, rei3819@calu.edu
*/

/*Buglist
 * ball resizes over walls when walls are thin
 * releasing mouse on screen after resizing is bad
 */


import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Vector;
import java.util.Random;

public class BouncingBall extends Frame implements WindowListener, ComponentListener, ActionListener, AdjustmentListener, Runnable,
	MouseListener, MouseMotionListener
{
	static final long serialVersionUID = 10L;
	
	//Constants
	private final int BUTTONH = 20;				//button height
	private final int MAXObj = 100;				//maximum bouncy size
	private final int MINObj = 10;				//minimum bouncy size
	private final int SPEED = 50;				//initial speed
	private final int SBVisible = 10;			//visible scroll bar
	private final int SBUnit = 1;				//scroll bar unit step size
	private final int SBBlock = 10;				//scroll bar block step size
	private final int SOBJ = 21;				//initial bouncy width
	
	//Globals
	private Point FrameSize = new Point(640, 400);							//initial frame size
	private Point Screen = new Point(FrameSize.x - 1, FrameSize.y - 1);		//drawing screen size
	private int WinTop = 10;					//top of frame
	private int WinLeft = 10;					//left side of frame
	private int SObj = SOBJ;					//bouncy width
	private int OldSize = 0;					//previous bouncy size
	private int SpeedSBmin = 1;					//speed scroll bar minimum value
	private int SpeedSBmax = 100 + SBVisible;	//speed scroll bar maximum value with visible offset
	private int SpeedSBinit = SPEED;			//speed scroll bar value
	private boolean run;						//for the program loop
	private boolean TimePause;					//run or pause
	private boolean started;					//identify if animation has started
	private boolean checkRelease = false;
	private int delay;							//current time delay

	
	private Insets I;			//insets of frame
	private Objc Ball;			//object to draw
	private Thread theThread;	//thread for timer delay
	
	//Points 	(size points in Globals)
	private Point m1 = new Point(0, 0);			//first mouse point
	private Point m2 = new Point(0, 0);			//second mouse point
	
	//Rectangles
	private Rectangle Perimeter = new Rectangle(0, 0, Screen.x, Screen.y);		//bouncing perimeter
	private Rectangle db = new Rectangle();										//drawing box
	private Rectangle ball = new Rectangle();										//bouncy hitbox
	private Rectangle t = new Rectangle();										//accessed obstacle rectangles
	private Rectangle r = new Rectangle();										//for resizing
	
	//Labels
	private Label SPEEDL = new Label("Speed", Label.CENTER);	//label for speed scroll bar
	private Label SIZEL	= new Label("Size", Label.CENTER);		//label for size scroll bar
	
	//ScrollBars
	private Scrollbar SpeedScrollBar, ObjSizeScrollBar;
	
	//Buttons
	private Button Run;					//unpause
	private Button Pause;				//pause
	private Button Quit;				//exit program
	
	//Panels
	private Panel sheet = new Panel();		//for the drawing object
	private Panel control = new Panel();	//for the control buttons and scrollbars
	
	public static void main(String[] args)
	{
		BouncingBall b = new BouncingBall();

	}

///////////////////////  Constructor  ///////////////////////
	
	public BouncingBall()
	{
		started = false;				//animation hasn't started yet
		
		setLayout(new BorderLayout());	//using border layout
		setVisible(true);
		
		MakeSheet();    				//determines sizes for the sheet
		
		try
		{
			initComponents();   		//try to initialize the components
		}
		catch(Exception e) {e.printStackTrace();}
		start();						//begins animation
	}
	
	//gets the insets and adjusts the sizes of the items
	private void MakeSheet()
	{
		I = getInsets();
		Screen.x = FrameSize.x - I.left - I.right;		//screen is the window width, minus the insets
		Screen.y = FrameSize.y - I.top - I.bottom - 44;
			//screen is the window height, minus 44 because that value works
		
		setSize(FrameSize.x, FrameSize.y);					//set frame size
		setBackground(Color.lightGray);					//set background color
	}
	
	//Initialize components
	public void initComponents() throws Exception, IOException
	{		
		//initialize buttons
		Run = new Button("Run");
		Pause = new Button("Pause");
		Quit = new Button("Quit");
		Pause.setEnabled(false);
		
		//initialize points
		m1.setLocation(0, 0);
		m2.setLocation(0, 0);
		
		//initialize rectangles
		Perimeter.setBounds(0, 0, Screen.x, Screen.y);
		Perimeter.grow(-1, -1);
		
		//initialize scroll bars
		SpeedScrollBar = new Scrollbar(Scrollbar.HORIZONTAL);	//create speed scroll bar
		SpeedScrollBar.setMaximum(SpeedSBmax);					//set max speed
		SpeedScrollBar.setMinimum(SpeedSBmin);					//set min speed
		SpeedScrollBar.setUnitIncrement(SBUnit);				//set unit increment
		SpeedScrollBar.setBlockIncrement(SBBlock); 				//set block increment
		SpeedScrollBar.setValue(SpeedSBinit);					//set initial value
		SpeedScrollBar.setVisibleAmount(SBVisible);				//set visible size
		SpeedScrollBar.setBackground(Color.gray);				//set background color
		
		ObjSizeScrollBar = new Scrollbar(Scrollbar.HORIZONTAL);	//create size scroll bar
		ObjSizeScrollBar.setMaximum(MAXObj);					//set max size
		ObjSizeScrollBar.setMinimum(MINObj);					//set min size
		ObjSizeScrollBar.setUnitIncrement(SBUnit);				//set unit increment
		ObjSizeScrollBar.setBlockIncrement(SBBlock); 			//set block increment
		ObjSizeScrollBar.setValue(SOBJ);						//set initial value
		ObjSizeScrollBar.setVisibleAmount(SBVisible);			//set visible size
		ObjSizeScrollBar.setBackground(Color.gray);				//set background color
		
		//Drawing Object
		Ball = new Objc(SObj, Screen);								//create drawing object
		Ball.setBackground(Color.WHITE);							//set background color
		
		//Initialize sheet
		sheet.setLayout(new BorderLayout(0, 0));				//sheet uses border layout
		sheet.setVisible(true);
		
		//Control panel Grid Bag
		GridBagLayout gbl = new GridBagLayout();			//grid bag layout for control panel
		GridBagConstraints constraints = new GridBagConstraints();
				
		gbl.columnWeights = new double[] {1, 6, 1, 3, 3, 3, 1, 6, 1,};
		gbl.columnWidths = new int[] {1, 6, 1, 3, 3, 3, 1, 6, 1,};
		gbl.rowWeights = new double[] {1, 1};
		gbl.rowHeights = new int[] {1, 1};
				
		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.BOTH;
		
		control.setBackground(Color.lightGray);
		control.setSize(FrameSize.x, 2 * BUTTONH);			//size control
		control.setLayout(gbl);								//set gridbaglayout
		control.setVisible(true);
		
		constraints.gridx = 1;								//add speed scrollbar
		constraints.gridy = 0;
		gbl.setConstraints(SpeedScrollBar, constraints);
		control.add(SpeedScrollBar);
				
		constraints.gridx = 3;								//add run button
		gbl.setConstraints(Run, constraints);
		control.add(Run);
				
		constraints.gridx = 4;								//add pause button
		gbl.setConstraints(Pause, constraints);
		control.add(Pause);
				
		constraints.gridx = 5;								//add quit button
		gbl.setConstraints(Quit, constraints);
		control.add(Quit);
				
		constraints.gridx = 7;								//add size scrollbar
		gbl.setConstraints(ObjSizeScrollBar, constraints);
		control.add(ObjSizeScrollBar);
				
		constraints.gridy = 1;								//add size label
		gbl.setConstraints(SIZEL, constraints);
		control.add(SIZEL);
				
		constraints.gridx = 1;								//add speed label
		gbl.setConstraints(SPEEDL, constraints);
		control.add(SPEEDL);
		
		//add sheets
		add("Center", sheet);					//sheet goes in center of frame
		add("South", control);					//control at bottom
		
		//add drawing object
		sheet.add("Center", Ball);				//add ball canvas to the center of the sheet
		
		//add action listeners
		Run.addActionListener(this);
		Pause.addActionListener(this);
		Quit.addActionListener(this);
		
		//add adjustment listeners
		SpeedScrollBar.addAdjustmentListener(this);
		ObjSizeScrollBar.addAdjustmentListener(this);
		
		//add window and component listeners
		this.addComponentListener(this);
		this.addWindowListener(this);
		
		//add mouse and mouse motion listeners
		Ball.addMouseListener(this);
		Ball.addMouseMotionListener(this);
		
		TimePause = true;								//pause when program starts
		run = true;										//program doesn't quit
		delay = 100 - SpeedScrollBar.getValue();		//complicated, work on later
		
		setBounds(WinLeft, WinTop, FrameSize.x, FrameSize.y);		//set frame position
		setPreferredSize(new Dimension(FrameSize.x, FrameSize.y));	//attempt to make the frame this size
		validate();										//validate the layout
	}
	
	//start the animation
	public void start()
	{
		Ball.repaint();
		if(theThread == null)		//create a thread if it does not exist
		{
			theThread = new Thread(this);	//create a new thread
			theThread.start();				//start the thread
		}
	}
	
	//run the animation
	public void run()
	{
		while(run)
		{
			try									//give the program some time to acknowledge the interrupt
			{									//might not be needed, but might cover some issues
				Thread.sleep(1);
			}catch(InterruptedException e) {}
			
			if(!TimePause)
			{
				started = true;
				try
				{
					Thread.sleep(delay);			//time between frames		
				}
				catch(InterruptedException e){}
				Ball.move();
				Ball.updateSize(SObj); 					//set size
				Ball.repaint();
			}
			
			try										//so that pause has a chance to activate
			{
				Thread.sleep(1);
			}
			catch(InterruptedException e){}
		}
	}
	
	//exit program routine, can be called with Quit button or X button
	public void stop()
	{
		run = false;				//terminate loop
		theThread.interrupt(); 		//interrupt the thread
		
		//remove action listeners
		Run.removeActionListener(this);
		Pause.removeActionListener(this);
		Quit.removeActionListener(this);
		
		//remove adjustment listeners
		SpeedScrollBar.removeAdjustmentListener(this);
		ObjSizeScrollBar.removeAdjustmentListener(this);
		
		//remove component and window listeners
		this.removeComponentListener(this);
		this.removeWindowListener(this);
		
		//remove mouse and mouse motion listeners
		Ball.removeMouseListener(this);
		Ball.removeMouseMotionListener(this);
		
		//dispose and exit
		dispose();
		System.exit(0);
	}
	
	//returns a drag box for the rectangle
	public Rectangle getDragBox(Point mN)
	{
		//this formula calculates the drag box instead of having to have 4 separate formulas
		return new Rectangle(Math.min(m1.x, mN.x), Math.min(m1.y, mN.y), 
		Math.max(m1.x, mN.x) - Math.min(m1.x, mN.x), Math.max(m1.y, mN.y) - Math.min(m1.y, mN.y));
	}
	
	
///////////  ActionListener method   //////////////////////////
	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();
		if(source == Run)
		{
			Run.setEnabled(false);						//switch to pause available
			Pause.setEnabled(true);
			TimePause = false;							//unpause it
			
			theThread.interrupt();						//interrupt the thread		
		}
		
		if(source == Pause)
		{
			Pause.setEnabled(false);					//switch to run available
			Run.setEnabled(true);
			TimePause = true;							//pause it
			
			theThread.interrupt();						//interrupt the thread
		}
		
		if(source == Quit)
		{
			stop();										//exit program
		}
	}
	
/////////////  AdjustmentListener methods  ////////////////////////
	public void adjustmentValueChanged(AdjustmentEvent e)
	{
		int TS;
		Scrollbar sb = (Scrollbar)e.getSource();	//get the scroll bar that triggered the event
		
		if(sb == SpeedScrollBar)
		{
			sb.validate();
			delay = 105-sb.getValue();				//adjust delay, use 105 to make change seem less exponential
			//theThread.interrupt();				//interrupting the thread causes the bouncy to do a burst of movement
		}
		
		if(sb == ObjSizeScrollBar)
		{
			sb.validate();
			OldSize = SObj;							//keep the previous size to let bouncy get smaller
			TS = e.getValue();						//get scroll bar value
			TS = (TS / 2) * 2 + 1;					//make odd to account for the center position
			
			//this boundary is one pixel larger than the ball on every side
			ball.setBounds(Ball.getBall());
			ball.grow(1, 1);
						
			if(ball.equals(Perimeter.intersection(ball)) || TS < OldSize)
			{
				int i = 0;			//index of vector variable
				boolean ok = true;	//flag to see if new size fits
				while((i < Ball.getWallSize()) && ok)
				{
					t = Ball.getOne(i);			//get the ith rectangle
					if(t.intersects(ball))			//check for intersection with obstacle
					{
						ok = false;
					}
					i++;
				}
				
				if(ok || TS < OldSize)
				{
					SObj = TS;					//update local ball size
					Ball.updateSize(TS);			//update size in ball object
				}
			}
			
			//theThread.interrupt();		//accelerates ball, keep out
			sb.setValue(SObj);				//keep accurate scrollbar
			Ball.repaint();					//force a repaint
		}

	}
	
////////////  WindowListener methods  //////////////////////////
	public void windowClosing(WindowEvent e)
	{
		stop();
	}
	
	//these are not used
	public void windowClosed(WindowEvent e){}
	public void windowOpened(WindowEvent e){}
	public void windowActivated(WindowEvent e){}
	public void windowDeactivated(WindowEvent e){}
	public void windowIconified(WindowEvent e){}
	public void windowDeiconified(WindowEvent e){}
	
////////////// ComponentListener methods  ////////////////////
//resize frame, stop resizing if you hit an object
	public void componentResized(ComponentEvent e)
	{
		int EXPANDX = 19, EXPANDY = 85;
		r.setBounds(Ball.getOne(0));	//get 0th rectangle
		int mr = r.x + r.width;			//initialize max right
		int mb = r.y + r.height;		//initialize max bottom
		
		for(int i = 1; i < Ball.getWallSize(); i++)		//process obstacles
		{
			r.setBounds(Ball.getOne(i));		//get ith rectangle
			mr = Math.max((r.x + r.width), mr);	//keep max right
			mb = Math.max((r.y + r.height), mb);	//keep max bottom
		}
		
		r.setBounds(Ball.getBall());			//process ball
		mr = Math.max((r.x + r.width), mr);
		mb = Math.max((r.y + r.height), mb);
		
		FrameSize.x = getWidth();			//set frame size
		FrameSize.y = getHeight();
		
		Screen.setLocation(sheet.getWidth() - 1, sheet.getHeight() - 1);		//update screen point
		
		if(mr + EXPANDX > FrameSize.x || mb + EXPANDY > FrameSize.y)				//set new size to include mr and mb
		{
			setSize(Math.max((mr + EXPANDX), FrameSize.x), Math.max((mb + EXPANDY), FrameSize.y));
			setExtendedState(ICONIFIED);		//force a refresh to this new size
			setExtendedState(NORMAL);
		}
		
		FrameSize.x = getWidth();		//this is in here twice to update the values before and after setting size
		FrameSize.y = getHeight();
		
		Screen.setLocation(sheet.getWidth() - 1, sheet.getHeight() - 1);		//update screen point
		Perimeter.setBounds(0, 0, Screen.x, Screen.y);				//update perimeter rectangle
		Perimeter.grow(-1, -1);
		
		Ball.reSize(Screen.x, Screen.y);			//resize the ball screen
		Ball.repaint();
		
		MakeSheet();								//remake the sheet
	}
	//not used
	public void componentHidden(ComponentEvent e){}
	public void componentShown(ComponentEvent e){}
	public void componentMoved(ComponentEvent e){}
	
//////////////// MouseListener methods  ////////////////////
	public void mousePressed(MouseEvent e)
	{
		m1.setLocation(e.getPoint());			//get starting mouse location
		checkRelease = true;					//say that the mouse has been pressed
	}
	public void mouseReleased(MouseEvent e)
	{

		Rectangle b = new Rectangle(Ball.getBall());
		b.grow(1, 1);
		
		boolean drawit = true;
		if(db.intersects(b))											//don't draw if touching ball
		{
			drawit = false;
		}
		
		if(!db.equals(db.intersection(Perimeter)))						//don't exceed bounds
		{
			db = db.intersection(Perimeter);
			db.grow(-1, -1);
		}
		
		for(int i = 0; i < Ball.getWallSize(); i++)						//don't draw if covered
		{
			if(db.intersection(Ball.getOne(i)).equals(db))
			{
				drawit = false;
			}
		}
		
		for(int i = 0; i < Ball.getWallSize(); i++)						//remove covered rectangles
		{
			if(db.intersection(Ball.getOne(i)).equals(Ball.getOne(i)) && drawit)	//only remove if drawing rectangle
			{
				Ball.removeOne(i);
				i--;					//need to update i for dynamic array
			}
		}
		
		if(drawit && checkRelease)
		{
			Ball.addOne(db); 				//Draw the rectangle
			checkRelease = false;
		}
		
		db.setBounds(0, 0, 0, 0);
		Ball.repaint();
	}
	public void mouseClicked(MouseEvent e)
	{
		Point p = new Point(e.getPoint());		//point of mouse
		boolean end = false;	//loop control
		int i = 0;				//wall index
		
		while(!end)
		{
			r = Ball.getOne(i);					//get ith rectangle
			if(r.contains(p))					//if ith rectangle contains p, delete it
			{
				Ball.removeOne(i);
			}
			else
			{
				i++;
			}
			
			if(i > Ball.getWallSize())			//if at end of walls, end loop
			{
				end = true;
			}
		}	
		
		Ball.setDragBox(new Rectangle(0, 0, 0, 0));
		Ball.repaint();							//update image
	}
	public void mouseEntered(MouseEvent e)
	{
		Ball.repaint(); 				//just to refresh screen
	}
	public void mouseExited(MouseEvent e) {}
	
//MouseMotionListener
	public void mouseDragged(MouseEvent e)
	{
		db.setBounds(getDragBox(e.getPoint()));		//set bounds of drag box
		if(Perimeter.contains(db))
		{
			Ball.setDragBox(db);				//draw drag box
			Ball.repaint();
		}
	}
	public void mouseMoved(MouseEvent e) {}
	
}



////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////  Objc Class ////////////////////////////////////////////////////////////////////////
class Objc extends Canvas
{
	private static final long SerialVersionUID = 11L;
	
	private Point Screen = new Point(0, 0);		//screen size
	private int SObj;					//bouncy size
	
	private Vector<Rectangle> Walls = new Vector<Rectangle>();				//rectangle vector
	private Vector<Rectangle> Edges = new Vector<Rectangle>();
	private static final Rectangle ZERO = new Rectangle(0, 0, 0, 0);		//Zero rectangle
	private Rectangle db = new Rectangle(0, 0, 0, 0);						//drag box
	private Rectangle ball = new Rectangle();								//ball rectangle, not the actual hitbox, just stores data
	
	private boolean Right;				//direction flags
	private boolean Down;
	
	private Random rand = new Random();
	
	private Image buffer;
	private Graphics g;
	
	//constructor
	public Objc(int SB, Point s)
	{
		Screen = s;						//screen size
		SObj = SB;
		ball.x = 2 + rand.nextInt(s.x - SObj/2 - 4) + SObj/2;			//position object random spot
		ball.y = 2 + rand.nextInt(s.y - SObj/2 - 4) + SObj/2;
		ball.width = SB;
		ball.height = SB;
		
		Right = true;					//object starts in this direction
		Down = true;
	}
	
	//mutators
	//add a rectangle to the vector
	public void addOne(Rectangle r)
	{
		Walls.addElement(new Rectangle(r));
		Edges.addElement(new Rectangle(r.x, r.y - 1, r.width, 1));				//add edges: up, right, down, left
		Edges.addElement(new Rectangle(r.x + r.width, r.y, 1, r.height));
		Edges.addElement(new Rectangle(r.x, r.y + r.height, r.width, 1));
		Edges.addElement(new Rectangle(r.x - 1, r.y, 1, r.height));
		
		db.setBounds(0, 0, 0, 0); 		//remove drag box
	}
	
	//remove a rectangle from the vector
	public void removeOne(int i)
	{
		Walls.removeElementAt(i);					//remove rectangle
		for(int c = 0; c < 4; c++)
		{
			Edges.removeElementAt(4*i);					//remove all 4 edges
		}
	}
	
	//updates the bouncy's size
	public void updateSize(int NS)
	{
		SObj = NS;
		ball.width = NS;
		ball.height = NS;
	}
	
	//set set drag box bounds
	public void setDragBox(Rectangle r)
	{
		db = r;
	}
	
	//resizes the screen
	public void reSize(int w, int h)
	{
		Screen.x = w;
		Screen.y = h;
	}
	
	//accessors
	//get size of wall vector
	public int getWallSize()
	{
		return Walls.size();
	}
	
	//get rectangle at index
	public Rectangle getOne(int i)
	{
		Rectangle r = new Rectangle(0, 0, 0, 0);
		if(Walls.size() > i)				//avoid out of bounds error
		{
			r = Walls.elementAt(i);
		}
		return r;
	}
	
	//returns ball dimensions
	public Rectangle getBall()
	{
		return new Rectangle(ball.x - SObj/2, ball.y - SObj/2, SObj, SObj);
	}
	
	//drawing methods	
	//performs the drawings
	public void paint(Graphics cg)
	{
		buffer = createImage(Screen.x, Screen.y);		//create the image
		if(g != null)											//if graphics g exists, get rid of it
		{
			g.dispose();
		}
		
		g = buffer.getGraphics();								//get new graphics
		
		//draw border
		g.setColor(Color.blue);
		g.drawRect(0, 1, Screen.x - 1, Screen.y - 2);
		
		//draw a circle
		g.setColor(Color.red);				//red circle
		g.fillOval(ball.x - SObj/2, ball.y - SObj/2, SObj, SObj);
		g.setColor(Color.BLACK); 			//with black border
		g.drawOval(ball.x - SObj/2, ball.y - SObj/2, SObj, SObj);
		
		//draw drag box
		g.drawRect(db.x, db.y, db.width, db.height);
		
		//draw rectangles
		Rectangle temp;
		for(int i = 0; i < Walls.size(); i++)
		{
			temp = Walls.elementAt(i);
			g.fillRect(temp.x, temp.y, temp.width, temp.height);
		}		
		
		cg.drawImage(buffer, 0, 0, null);		//switch graphics
	}
	
	public void update(Graphics cg)
	{
		paint(cg);
	}
	
	//keep the box in a resized screen
	public Rectangle touching()
	{
		Rectangle r = new Rectangle(ZERO);		//set r to zero initially
		Rectangle b = new Rectangle(ball);		//copy of ball rectangle, one size larger
		b.grow(1, 1);
		
		boolean ok = true;
		int i = 0;
		while((i < Walls.size()) && ok)			//find the rectangle touching the ball
		{
			r = Walls.elementAt(i);
			if(r.intersects(b))
			{
				ok = false;
			}
			else
			{
				i++;
			}
		}
		
		if(ok)				//if no rectangle found, return zero rectangle
		{
			r = ZERO;
		}
		
		return r;
	}
	
	public void move()
	{		
		if(Down)									//move up/down
		{
			ball.y++;
		}
		else
		{
			ball.y--;
		}
		
		if(Right)									//move left/right
		{
			ball.x++;
		}
		else
		{
			ball.x--;
		}
		
		Rectangle b = new Rectangle(ball.x - SObj/2, ball.y - SObj/2, SObj, SObj);		//ball hitbox
		b.grow(1, 1);
		
		for(int i = 0; i < Edges.size(); i += 4)		//bounce on rectangles
		{
			if(b.intersects(Edges.elementAt(i)))				//hit top, bounce down
			{
				Down = false;
			}
			else if(b.intersects(Edges.elementAt(i + 2)))	//hit bottom, bounce up
			{
				Down = true;
			}
			
			if(b.intersects(Edges.elementAt(i + 1)))			//hit left, bounce right
			{
				Right = true;
			}
			else if(b.intersects(Edges.elementAt(i + 3)))	//hit right, bounce left
			{
				Right = false;
			}			
		}
		
		
		Rectangle Perimeter = new Rectangle(1, 1, Screen.x - 2, Screen.y - 2);	//bounce on border
		if(b.y <= Perimeter.y)					//bounce up/down
		{
			Down = true;
		}
		else if(b.y + b.height >= Perimeter.y + Perimeter.height)
		{
			Down = false;
		}
		
		if(b.x <= Perimeter.x)					//bounce left/right
		{
			Right = true;
		}
		else if(b.x + b.width > Perimeter.x + Perimeter.width)
		{
			Right = false;
		}
	}
}


