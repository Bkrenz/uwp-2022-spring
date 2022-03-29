/*
Program 4, CET 350
* 
* Group 5
* @author Robert Krency, kre1188@calu.edu
* @author Kevin Reisch, rei3819@calu.edu
*/


import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class Bounce extends Frame implements WindowListener, ComponentListener, ActionListener, AdjustmentListener, Runnable
{
	static final long serialVersionUID = 10L;
	
	//Constants
	private final int WIDTH = 640;				//initial frame width
	private final int HEIGHT = 400;				//initial frame height
	private final int BUTTONH = 20;				//button height
	private final int BUTTONHS = 5;				//button height spacing
	private final int MAXObj = 100;				//maximum bouncy size
	private final int MINObj = 10;				//minimum bouncy size
	private final int SPEED = 50;				//initial speed
	private final int SBVisible = 10;			//visible scroll bar
	private final int SBUnit = 1;				//scroll bar unit step size
	private final int SBBlock = 10;				//scroll bar block step size
	private final int SCROLLBARH = BUTTONH;		//scroll bar height
	private final int SOBJ = 21;				//initial bouncy width
	private final int DELAY = 100;				//timer delay constant                      FIND APPROPRIATE VALUE
	
	//Globals
	private int WinWidth = WIDTH;				//frame width
	private int WinHeight = HEIGHT;				//frame height
	private int ScreenWidth;					//drawing screen width
	private int ScreenHeight;					//drawing screen height
	private int WinTop = 10;					//top of frame
	private int WinLeft = 10;					//left side of frame
	private int BUTTONW = 50;					//initial button width
	private int CENTER = WIDTH / 2;				//initial screen center
	private int BUTTONS = BUTTONW / 4;			//initial button spacing
	private int SObj = SOBJ;					//bouncy width
	private int OldSize = 0;					//previous bouncy size
	private int SpeedSBmin = 1;					//speed scroll bar minimum value
	private int SpeedSBmax = 100 + SBVisible;	//speed scroll bar maximum value with visible offset
	private int SpeedSBinit = SPEED;			//speed scroll bar value
	private int ScrollBarW;						//scroll bar width
	private boolean run;						//for the program loop
	private boolean TimePause;					//run or pause
	private boolean started;					//identify if animation has started
	private int speed;							//scroll bar speed
	private int delay;							//current time delay

	
	private Insets I;			//insets of frame
	private Objc Obj;			//object to draw
	private Thread theThread;	//thread for timer delay
	
	//Labels
	private Label SPEEDL = new Label("Speed", Label.CENTER);	//label for speed scroll bar
	private Label SIZEL	= new Label("Size", Label.CENTER);		//label for size scroll bar
	
	//ScrollBars
	Scrollbar SpeedScrollBar, ObjSizeScrollBar;
	
	//Buttons
	Button Start;				//run/pause
	Button Shape;				//bouncy is circle/square
	Button Tail;				//gives bouncy a tail of previous bouncy's
	Button Clear;				//clears bouncy
	Button Quit;				//exit program

	public static void main(String[] args)
	{
		Bounce b = new Bounce();

	}

///////////////////////  Constructor  ///////////////////////
	
	public Bounce()
	{
		started = false;				//animation hasn't started yet
		
		setLayout(null);				//using null layout
		setVisible(true);
		
		MakeSheet();    				//determines sizes for the sheet
		
		try
		{
			initComponents();   		//try to initialize the components
		}
		catch(Exception e) {e.printStackTrace();}
		SizeScreen();					//sizes the items on the screen
		start();						//begins animation
	}
	
	//gets the insets and adjusts the sizes of the items
	private void MakeSheet()
	{
		I = getInsets();
		ScreenWidth = WinWidth - I.left - I.right;		//screen is the window width, minus the insets
		ScreenHeight = WinHeight - I.top - 2* (BUTTONH + BUTTONHS) - I.bottom;
			//screen is the window height, minus insets and 2 button spaces
		
		setSize(WinWidth, WinHeight);					//set frame size
		CENTER = ScreenWidth / 2;						//determine center of the screen
		BUTTONW = ScreenWidth / 11;						//determine button width (11 units)
		BUTTONS = BUTTONW / 4;							//determine button spacing
		setBackground(Color.lightGray);					//set background color
		ScrollBarW = 2*BUTTONW;							//determine scroll bar width
	}
	
	//Initialize components
	public void initComponents() throws Exception, IOException
	{
		//initialize buttons
		Start = new Button("Run");
		Shape = new Button("Circle");
		Tail = new Button("No Tail");
		Clear = new Button("Clear");
		Quit = new Button("Quit");
		
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
		Obj = new Objc(SObj, ScreenWidth, ScreenHeight);		//create drawing object
		Obj.setBackground(Color.WHITE);							//set background color
		
		//add labels
		add(SPEEDL);
		add(SIZEL);
		
		//add buttons
		add("Center", Start);
		add("Center", Shape);
		add("Center", Tail);
		add("Center", Clear);
		add("Center", Quit);
		
		//add scroll bars
		add(SpeedScrollBar);
		add(ObjSizeScrollBar);
		
		//add drawing object
		add(Obj);
		
		//add action listeners
		Start.addActionListener(this);
		Shape.addActionListener(this);
		Tail.addActionListener(this);
		Clear.addActionListener(this);
		Quit.addActionListener(this);
		
		//add adjustment listeners
		SpeedScrollBar.addAdjustmentListener(this);
		ObjSizeScrollBar.addAdjustmentListener(this);
		
		//add window and component listeners
		this.addComponentListener(this);
		this.addWindowListener(this);
		
		TimePause = true;								//pause when program starts
		run = true;										//program doesn't quit
		delay = 100 - SpeedScrollBar.getValue();		//complicated, work on later
		
		setBounds(WinLeft, WinTop, WIDTH, HEIGHT);		//set frame position
		setPreferredSize(new Dimension(WIDTH, HEIGHT));	//attempt to make the frame this size
		setMinimumSize(getPreferredSize());				//frame can't get smaller than this
		validate();										//validate the layout
	}
	
	//sets locations and sizes
	private void SizeScreen()
	{
		//positions of labels
		SPEEDL.setLocation(I.left + BUTTONS, ScreenHeight + BUTTONHS + BUTTONH + I.top);
		SIZEL.setLocation(WinWidth - ScrollBarW - I.right, ScreenHeight + BUTTONHS + BUTTONH + I.top);
			
		//positions of buttons
		Start.setLocation(CENTER-2*(BUTTONW+BUTTONS)-BUTTONW/2, ScreenHeight+BUTTONHS+I.top);
		Shape.setLocation(CENTER-BUTTONW-BUTTONS-BUTTONW/2, ScreenHeight+BUTTONHS+I.top);
		Tail.setLocation(CENTER-BUTTONW/2, ScreenHeight+BUTTONHS+I.top);
		Clear.setLocation(CENTER+BUTTONS+BUTTONW/2, ScreenHeight+BUTTONHS+I.top);
		Quit.setLocation(CENTER+BUTTONW+2*BUTTONS+BUTTONW/2, ScreenHeight+BUTTONHS+I.top);
		
		//positions of scroll bars
		SpeedScrollBar.setLocation(I.left + BUTTONS, ScreenHeight + BUTTONHS + I.top);
		ObjSizeScrollBar.setLocation(WinWidth - ScrollBarW - I.right - BUTTONS, ScreenHeight + BUTTONHS + I.top);
		
		//size of labels
		SPEEDL.setSize(ScrollBarW, BUTTONH);
		SIZEL.setSize(ScrollBarW, SCROLLBARH);
		
		//size the buttons
		Start.setSize(BUTTONW, BUTTONH);
		Shape.setSize(BUTTONW, BUTTONH);
		Tail.setSize(BUTTONW, BUTTONH);
		Clear.setSize(BUTTONW, BUTTONH);
		Quit.setSize(BUTTONW, BUTTONH);
		
		//size of scroll bars
		SpeedScrollBar.setSize(ScrollBarW, SCROLLBARH);
		ObjSizeScrollBar.setSize(ScrollBarW, SCROLLBARH);
		
		//set drawing object bounds
		Obj.setBounds(I.left, I.top, ScreenWidth, ScreenHeight);
	}
	
	//start the animation
	public void start()
	{
		Obj.repaint();
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
			if(!TimePause)
			{
				started = true;
				try
				{
					Thread.sleep(delay);			//time between frames		
				}
				catch(InterruptedException e){}
				Obj.move();
				Obj.update(SObj); 					//set size
				Obj.repaint();
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
		Start.removeActionListener(this);
		Shape.removeActionListener(this);
		Tail.removeActionListener(this);
		Clear.removeActionListener(this);
		Quit.removeActionListener(this);
		
		//remove adjustment listeners
		SpeedScrollBar.removeAdjustmentListener(this);
		ObjSizeScrollBar.removeAdjustmentListener(this);
		
		//remove component and window listeners
		this.removeComponentListener(this);
		this.removeWindowListener(this);
		
		//dispose and exit
		dispose();
		System.exit(0);
	}
	
	
///////////  ActionListener method   //////////////////////////
	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();
		if(source == Start)
		{
			if(Start.getLabel().equals("Pause"))		//update text and TimePause flag
			{
				Start.setLabel("Run");
				TimePause = true;
			}
			else
			{
				Start.setLabel("Pause");
				TimePause = false;
			}
			
			theThread.interrupt();						//interrupt the thread
			
			
		}
		
		if(source == Shape)
		{
			if(Shape.getLabel().equals("Circle"))		//update text and shape
			{
				Shape.setLabel("Square");
				Obj.rectangle(false);
			}
			else
			{
				Shape.setLabel("Circle");
				Obj.rectangle(true);
			}
			
			if(!started)								//do not let circle appear inside square
			{
				Obj.clear();
			}
			Obj.repaint();
		}
		
		if(source == Tail)
		{
			if(Tail.getLabel().equals("Tail"))			//update text and tail flag
			{
				Tail.setLabel("No Tail");
				Obj.setTail(true);
			}
			else
			{
				Tail.setLabel("Tail");
				Obj.setTail(false);
			}
		}
		
		if(source == Clear)
		{
			Obj.clear();								//clear screen
			Obj.repaint();
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
			
			if(TS < OldSize || Obj.keepSize()) 		//make sure bouncy stays in the border, it always will if it's shrinking
			{
				Obj.update(TS);							//change the size of the drawing object
			}
			else
			{
				TS = Obj.getSObj();
				sb.setValue(SObj);
			}
			
			if(!Obj.getTail())						//causes tails to get cleared, but keep in anyways because it looks bad without it
			{
				Obj.clear();
			}
			SObj = TS;
			
			Obj.repaint();								//force a repaint, do not use for speed to avoid acceleration
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
	
//resize frame and objects
	public void componentResized(ComponentEvent e)
	{
		WinWidth = getWidth();
		WinHeight = getHeight();
		
		MakeSheet();
		SizeScreen();
		Obj.checkSize();
		Obj.reSize(ScreenWidth, ScreenHeight);
	}
	//not used
	public void componentHidden(ComponentEvent e){}
	public void componentShown(ComponentEvent e){}
	public void componentMoved(ComponentEvent e){}
	
}



/////////////////////////////////////////////////////////////////////
///////////////////////////  Objc Class /////////////////////////////
class Objc extends Canvas
{
	private static final long SerialVersionUID = 11L;
	private int ScreenWidth;
	private int ScreenHeight;
	private int SObj;					//bouncy size
	
	private int x, y;					//bouncy coordinates
	private boolean rect = true;
	private boolean clear = false;
	private boolean tail = true;
	
	private int ymin;					//bounds for bounce
	private int ymax;
	private int xmin;
	private int xmax;
	
	private int yold;					//previous position
	private int xold;
	
	private boolean Right;				//direction flags
	private boolean Down;
	
	//constructor
	public Objc(int SB, int w, int h)
	{
		ScreenWidth = w;
		ScreenHeight = h;
		SObj = SB;
		rect = true;					//start as square
		clear = false;
		tail = true;
		x = (SObj-1)/2 + 1;			//position object in the upper left of the screen
		y = (SObj-2)/2 + 2;
		
		Right = true;					//object starts in this direction
		Down = true;
		
		calcMinMax();
	}
	
	//mutators
	//change shape flag
	public void rectangle(boolean r)
	{
		rect = r;
	}
	
	//updates the bouncy's size
	public void update(int NS)
	{
		SObj = NS;
	}
	
	//set x coordinate
	public void setX(int x)
	{
		this.x = x;
	}
	
	//set y coordinate
	public void setY(int y)
	{
		this.y = y;
	}
	
	//resizes the screen
	public void reSize(int w, int h)
	{
		ScreenWidth = w;
		ScreenHeight = h;
		calcMinMax();
	}
	
	//set clear flag
	public void clear()
	{
		clear = true;
	}
	
	//change tail flag
	public void setTail(boolean t)
	{
		tail = t;
	}
	
	//accessors
	//get tail
	public boolean getTail()
	{
		return tail;
	}
	
	//get x coordinate
	public int getX()
	{
		return x;
	}
	
	//get y coordinate
	public int getY()
	{
		return y;
	}
	
	//get size
	public int getSObj()
	{
		return SObj;
	}
	
	//drawing methods
	//draw red border around the screen and paint
	public void paint(Graphics g)
	{
		g.setColor(Color.RED);
		g.drawRect(0, 1, ScreenWidth - 1, ScreenHeight - 2);  //top border wasn't showing up with 0,0
		update(g);
	}
	
	//performs the drawings
	public void update(Graphics g)
	{
		if(clear)
		{
			super.paint(g);
			clear = false;
			g.setColor(Color.RED);
			g.drawRect(0, 1, ScreenWidth - 1, ScreenHeight - 2);
		}
		
		if(!tail)
		{
			g.setColor(getBackground());		//clear tails by creating tails with the background color
			if(rect)
			{
				g.fillRect(xold - (SObj-1)/2, yold - (SObj - 1)/2, SObj + 1, SObj + 1);
			}
			else
			{
				g.fillOval(xold - (SObj-1)/2 - 2, yold - (SObj-1)/2 - 2, SObj + 3, SObj + 3);
			}
		}
		
		if(rect)		//draw a rectangle
		{
			g.setColor(Color.lightGray);		//gray rectangle
			g.fillRect(x - (SObj - 1)/2, y - (SObj - 1)/2, SObj, SObj);
			g.setColor(Color.BLACK);			//with black border
			g.drawRect(x - (SObj - 1)/2, y - (SObj - 1)/2, SObj - 1, SObj - 1);
		}
		else			//draw a circle
		{
			g.setColor(Color.lightGray);		//gray circle
			g.fillOval(x - (SObj - 1)/2, y - (SObj - 1)/2, SObj, SObj);
			g.setColor(Color.BLACK); 			//with black border
			g.drawOval(x - (SObj - 1)/2, y - (SObj - 1)/2, SObj - 1, SObj - 1);
		}
		
		xold = x;
		yold = y;
	}
	
	//keep the box in a resized screen
	public void checkSize()
	{
		if(x > xmax)					//reposition x, and change direction
		{
			x = ScreenWidth - (SObj - 1)/2 - 3;
			Right = false;
		}
		else if(x < xmin)
		{
			x = (SObj - 1)/2 + 1;
			Right = true;
		}
		
		if(y > ymax)					//reposition y, and change direction
		{
			y = ScreenHeight - (SObj - 1)/2 - 3;					
			Down = false;
		}
		else if(y < ymin)
		{
			y = (SObj - 1)/2 + 2;
			Down = true;
		}
	}
	
	//Checks and makes sure that the box can't grow past the bounds
	public boolean keepSize()
	{
		boolean fits = true;
		
		if(x >= xmax)					//update size and direction
		{
			fits = false;
			SObj = 2*(ScreenWidth - x) - 5;
			Right = false;
		}
		else if(x < xmin)
		{
			fits = false;
			SObj = 2*x - 3;
			Right = true;
		}
		
		if(y > ymax)					//update size and direction
		{
			fits = false;
			SObj = 2*(ScreenHeight - y) - 5;
			Down = false;
		}
		else if(y < ymin)
		{
			fits = false;
			SObj = 2*y - 3;
			Down = true;
		}
		
		return fits;
	}
	
	private void calcMinMax()						//calculate min and max x and y values
	{
		xmin = (SObj - 1)/2 + 2;					//+ - to not overwrite border
		xmax = ScreenWidth - (SObj - 1)/2 - 4;
		ymin = (SObj - 1)/2 + 3;
		ymax = ScreenHeight - (SObj - 1)/2 - 4;
	}
	
	public void move()
	{		
		if(Down)									//move up/down
		{
			y++;
		}
		else
		{
			y--;
		}
		
		if(Right)									//move left/right
		{
			x++;
		}
		else
		{
			x--;
		}
		
		if(y <= ymin)								//bounce vertical
		{
			Down = true;
		}
		else if(y >= ymax)
		{
			Down = false;
		}
		
		if(x <= xmin)					//bounce horizontal 
		{
			Right = true;
		}
		else if(x >= xmax)
		{
			Right = false;
		}
	}
}


