package cannon;

/**
 * Cannon vs Ball
 * CET 350 Assignment 6
 * 
 * @author Robert Krency, kre1188@calu.edu
 * 
 */

import java.awt.*;
import java.awt.event.*;

import java.util.ArrayList;
import java.util.Vector;
import java.util.HashMap;
import java.util.Random;

public class CannonVSBall
        implements ActionListener, WindowListener, ItemListener, ComponentListener, AdjustmentListener,
            Runnable, MouseListener, MouseMotionListener {
    /* UID */
    private static final long SerialVersionUID = 10L;

    public static void main(String[] args) {
        new CannonVSBall();
    }

    /* Frame Constants */
    private final int SCREEN_WIDTH = 640;
    private final int SCREEN_HEIGHT = 400;

    private final int BUTTON_HEIGHT = 20;

    /* Scrollbar Constants */
    private final int SLIDER_SIZE = 2;
    private final int SCROLLBAR_UNIT = 1;
    private final int SCROLLBAR_BLOCK_STEP = 10;
    private final int VELOCITY_MAX = SLIDER_SIZE + 15;
    private final int VELOCITY_MIN = 1;
    private final int VELOCITY_INIT = 5;
    private final int ANGLE_MAX = SLIDER_SIZE + 90;
    private final int ANGLE_MIN = 0;
    private final int ANGLE_INIT = 45;

    /* Frame Components */
    private Frame windowFrame;
    private Point frameSize, canvasSize;
    private Rectangle dragBox;

    /* Menu Bar Components */
    private MenuBar menuBar;
    private Menu controlMenu, environmentMenu, parametersMenu;
    private Menu sizeMenu, speedMenu;
    private MenuItem pauseMenuItem, runMenuItem, restartMenuItem, quitMenuItem;
    private ArrayList<CheckboxMenuItem> targetSizes, targetSpeeds, gravityAmounts;
    private HashMap<String, Double> gravityValues;

    /* Panel Components */
    private Panel drawingPanel, controlPanel;

    /* Scrollbar Components */
    private Scrollbar velocityScrollBar, angleScrollBar;

    /* Label Components */
    private Label ballScoreLabel, playerScoreLabel, timeLabel, angleLabel, boundsLabel, velocityLabel;

    /* Thread Constants */
    private final int THREAD_DELAY = 5;

    /* Thread Values */
    Thread gameThread;
    private int clock;

    /* Game Canvas Values */
    private GameCanvas gameCanvas;

    /* Game Values */
    private int ballScore, playerScore, time, velocity, angle;
    private double gravity;
    private String ballSize, ballSpeed;
    private boolean run, runGame, inBounds;

    /* Mouse Values */
    private Point mouseLocation1, mouseLocation2;
    private boolean mousePressed;

    /**
     * General constructor. Sets up the window and components.
     */
    public CannonVSBall() {

        this.setupWindow();
        this.restart();
        this.start();

    }

    /**
     * Sets up the Window Frame and its components, in the following order:
     * 1. Menu Bar
     */
    private void setupWindow() {

        // Setup the Frame
        this.frameSize = new Point(SCREEN_WIDTH, SCREEN_HEIGHT);
        this.windowFrame = new Frame("Cannon VS Ball");
        this.makeSheet();
        this.windowFrame.setLayout(new BorderLayout());
        this.windowFrame.setBackground(Color.lightGray);
        this.windowFrame.setForeground(Color.black);
        this.dragBox = new Rectangle(0,0,0,0);

        // Setup the Menu Bar
        this.menuBar = new MenuBar();
        this.menuBar.add(this.setupControlMenu());
        this.menuBar.add(this.setupParametersMenu());
        this.menuBar.add(this.setupEnvironmentMenu());
        this.windowFrame.setMenuBar(this.menuBar);

        // Setup the Game Canvas
        this.gameCanvas = new GameCanvas(this.canvasSize);
        this.gameCanvas.setBackground(Color.white);
        this.drawingPanel = new Panel();
        this.drawingPanel.setLayout(new BorderLayout());
        this.drawingPanel.add("Center", this.gameCanvas);
        this.drawingPanel.setVisible(true);
        this.windowFrame.add("Center", this.drawingPanel);

        // Setup the Control Panel
        this.setupControlPanel();

        // Add Listeners
        this.windowFrame.addComponentListener(this);
        this.windowFrame.addWindowListener(this);
        this.gameCanvas.addMouseListener(this);
        this.gameCanvas.addMouseMotionListener(this);

        // Setup the Window
        this.windowFrame.addWindowListener(this);
        this.windowFrame.setResizable(true);
        this.windowFrame.setVisible(true);
        this.windowFrame.setBounds(10, 10, this.frameSize.x, this.frameSize.y);
        this.windowFrame.setPreferredSize(new Dimension(this.frameSize.x, this.frameSize.y));
        this.windowFrame.setMinimumSize(new Dimension(200, 200));

        this.windowFrame.validate();

        this.mousePressed = false;
        this.mouseLocation1 = new Point();
        this.mouseLocation2 = new Point();

    }

    public void makeSheet() {
        Insets insets = this.windowFrame.getInsets();
        int x = SCREEN_WIDTH - insets.left - insets.right - 16;
        int y = SCREEN_HEIGHT - insets.top - insets.bottom - 106;
        this.canvasSize = new Point(x, y);
        this.windowFrame.setSize(x, y);
        this.windowFrame.setBackground(Color.lightGray);
    }

    /**
     * Set up the control panel
     */
    private void setupControlPanel() {

        // Setup the velocity scroll bar
        this.velocityScrollBar = new Scrollbar(Scrollbar.HORIZONTAL);
        this.velocityScrollBar.setMaximum(VELOCITY_MAX);
        this.velocityScrollBar.setMinimum(VELOCITY_MIN);
        this.velocityScrollBar.setUnitIncrement(SCROLLBAR_UNIT);
        this.velocityScrollBar.setBlockIncrement(SCROLLBAR_BLOCK_STEP);
        this.velocityScrollBar.setValue(VELOCITY_INIT);
        this.velocityScrollBar.setVisibleAmount(SLIDER_SIZE);
        this.velocityScrollBar.setBackground(Color.gray);
        this.velocityScrollBar.addAdjustmentListener(this);

        // Setup the angle scroll bar
        this.angleScrollBar = new Scrollbar(Scrollbar.HORIZONTAL);
        this.angleScrollBar.setMaximum(ANGLE_MAX);
        this.angleScrollBar.setMinimum(ANGLE_MIN);
        this.angleScrollBar.setUnitIncrement(SCROLLBAR_UNIT);
        this.angleScrollBar.setBlockIncrement(SCROLLBAR_BLOCK_STEP);
        this.angleScrollBar.setValue(ANGLE_INIT);
        this.angleScrollBar.setVisibleAmount(SLIDER_SIZE);
        this.angleScrollBar.setBackground(Color.gray);
        this.angleScrollBar.addAdjustmentListener(this);

        // Setup the labels
        this.velocityLabel = new Label("Initial Velocity (m/s): 5", Label.CENTER);
        this.angleLabel = new Label("Angle: ", Label.CENTER);
        this.boundsLabel = new Label("In bounds", Label.CENTER);
        this.timeLabel = new Label("Time: 0", Label.LEFT);
        this.ballScoreLabel = new Label("Ball: 0", Label.LEFT);
        this.playerScoreLabel = new Label("Player: 0", Label.LEFT);

        // Setup the Control Panel layout
        GridBagLayout controlLayout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();

        controlLayout.columnWeights = new double[] { 1, 6, 2, 2, 2, 2, 2, 6, 1, };
        controlLayout.columnWidths = new int[] { 1, 6, 2, 2, 2, 2, 2, 6, 1, };
        controlLayout.rowWeights = new double[] { 1, 1 };
        controlLayout.rowHeights = new int[] { 1, 1 };

        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.BOTH;

        // Setup the contorl panel
        this.controlPanel = new Panel();
        this.controlPanel.setBackground(Color.lightGray);
        this.controlPanel.setSize(this.frameSize.x, 2 * BUTTON_HEIGHT);
        this.controlPanel.setLayout(controlLayout);

        // Add components to the control panel
        // Velocity Scroll Bar
        constraints.gridx = 1;
        constraints.gridy = 0;
        controlLayout.setConstraints(this.velocityScrollBar, constraints);
        controlPanel.add(this.velocityScrollBar);

        // Angle Scroll Bar
        constraints.gridx = 7;
        controlLayout.setConstraints(this.angleScrollBar, constraints);
        controlPanel.add(this.angleScrollBar);

        // Bounds Label
        constraints.gridx = 3;
        constraints.gridwidth = 3;
        controlLayout.setConstraints(this.boundsLabel, constraints);
        this.controlPanel.add(this.boundsLabel);

        // Angle Label
        constraints.gridwidth = 1;
        constraints.gridx = 7;
        constraints.gridy = 1;
        controlLayout.setConstraints(this.angleLabel, constraints);
        this.controlPanel.add(this.angleLabel);

        // Velocity Label
        constraints.gridx = 1;
        controlLayout.setConstraints(this.velocityLabel, constraints);
        this.controlPanel.add(this.velocityLabel);

        // Time Label
        constraints.gridx = 3;
        controlLayout.setConstraints(this.timeLabel, constraints);
        this.controlPanel.add(this.timeLabel);

        // Ball Score Label
        constraints.gridx = 4;
        controlLayout.setConstraints(this.ballScoreLabel, constraints);
        this.controlPanel.add(this.ballScoreLabel);

        // Player Score Label
        constraints.gridx = 5;
        controlLayout.setConstraints(this.playerScoreLabel, constraints);
        this.controlPanel.add(this.playerScoreLabel);

        // Add panel to the frame
        this.windowFrame.add("South", this.controlPanel);
        this.controlPanel.setVisible(true);

    }

    /**
     * 
     * @return the initialized Control Menu
     */
    private Menu setupControlMenu() {

        // Initialize the control menu
        this.controlMenu = new Menu("Control");

        // Setup the Menu Items
        this.pauseMenuItem = new MenuItem("Pause", new MenuShortcut(KeyEvent.VK_P));
        this.runMenuItem = new MenuItem("Run", new MenuShortcut(KeyEvent.VK_R));
        this.restartMenuItem = new MenuItem("Restart", new MenuShortcut(KeyEvent.VK_E));
        this.quitMenuItem = new MenuItem("Quit", new MenuShortcut(KeyEvent.VK_Q));

        // Add the menu items to the control menu
        this.controlMenu.add(this.runMenuItem);
        this.controlMenu.add(this.pauseMenuItem);
        this.controlMenu.add(this.restartMenuItem);
        this.controlMenu.addSeparator();
        this.controlMenu.add(this.quitMenuItem);

        // Add the action listeners
        this.runMenuItem.addActionListener(this);
        this.pauseMenuItem.addActionListener(this);
        this.restartMenuItem.addActionListener(this);
        this.quitMenuItem.addActionListener(this);

        // Return the completed control menu
        return this.controlMenu;
    }

    /**
     * Set up the Parameters Menu, adding each submenu and components
     */
    private Menu setupParametersMenu() {

        // Initialize the parameters menu
        this.parametersMenu = new Menu("Parameters");

        // Create the Sizes Menu
        this.sizeMenu = new Menu("Size");

        // Create all of the checkboxes for the target Size
        this.targetSizes = new ArrayList<>();
        this.targetSizes.add(new CheckboxMenuItem(GameCanvas.BALL_SIZE_XSMALL));
        this.targetSizes.add(new CheckboxMenuItem(GameCanvas.BALL_SIZE_SMALL));
        this.targetSizes.add(new CheckboxMenuItem(GameCanvas.BALL_SIZE_MEDIUM));
        this.targetSizes.add(new CheckboxMenuItem(GameCanvas.BALL_SIZE_LARGE));
        this.targetSizes.add(new CheckboxMenuItem(GameCanvas.BALL_SIZE_XLARGE));
        this.targetSizes.get(0).setState(true);

        // Add the checkboxes to the menu
        for (CheckboxMenuItem cb : this.targetSizes)
            this.sizeMenu.add(cb);

        // Add the Sizes Menu to the Parameters Menu
        this.parametersMenu.add(this.sizeMenu);

        // Create the Speeds menu
        this.speedMenu = new Menu("Speed");

        // Create all of the checkboxes for target Speed
        this.targetSpeeds = new ArrayList<>();
        this.targetSpeeds.add(new CheckboxMenuItem(GameCanvas.BALL_SPEED_SLOW));
        this.targetSpeeds.add(new CheckboxMenuItem(GameCanvas.BALL_SPEED_NORMAL));
        this.targetSpeeds.add(new CheckboxMenuItem(GameCanvas.BALL_SPEED_FAST));
        this.targetSpeeds.get(0).setState(true);

        // Add the checkboxes to the menu
        for (CheckboxMenuItem cb : this.targetSpeeds)
            this.speedMenu.add(cb);

        // Add the speed menu to the parameters menu
        this.parametersMenu.add(this.speedMenu);

        // Add the action listeners
        for (CheckboxMenuItem cb : this.targetSizes)
            cb.addItemListener(this);
        for (CheckboxMenuItem cb : this.targetSpeeds)
            cb.addItemListener(this);

        // Return the created Parameters Menu
        return this.parametersMenu;
    }

    /**
     * 
     * @return
     */
    private Menu setupEnvironmentMenu() {

        // Initialize the Environment Menu
        this.environmentMenu = new Menu("Environment");

        // Set up the gravity amounts list
        this.gravityAmounts = new ArrayList<>();

        // Add all the checkbox items to the gravity amounts list
        this.gravityAmounts.add(new CheckboxMenuItem("Mercury"));
        this.gravityAmounts.add(new CheckboxMenuItem("Venus"));
        this.gravityAmounts.add(new CheckboxMenuItem("Earth"));
        this.gravityAmounts.add(new CheckboxMenuItem("Moon"));
        this.gravityAmounts.add(new CheckboxMenuItem("Mars"));
        this.gravityAmounts.add(new CheckboxMenuItem("Jupiter"));
        this.gravityAmounts.add(new CheckboxMenuItem("Saturn"));
        this.gravityAmounts.add(new CheckboxMenuItem("Uranus"));
        this.gravityAmounts.add(new CheckboxMenuItem("Neptune"));
        this.gravityAmounts.add(new CheckboxMenuItem("Pluto"));
        this.gravityAmounts.get(3).setState(true);

        // Add the checkboxes to the environment menu
        for (CheckboxMenuItem cb : this.gravityAmounts)
            this.environmentMenu.add(cb);

        // Add the item listeners
        for (CheckboxMenuItem cb : this.gravityAmounts)
            cb.addItemListener(this);

        // Set up the associated gravity values
        this.gravityValues = new HashMap<>();
        this.gravityValues.put("Mercury", 3.7);
        this.gravityValues.put("Venus", 8.87);
        this.gravityValues.put("Earth", 9.1);
        this.gravityValues.put("Moon", 1.62);
        this.gravityValues.put("Mars", 3.72);
        this.gravityValues.put("Jupiter", 24.79);
        this.gravityValues.put("Saturn", 10.44);
        this.gravityValues.put("Uranus", 8.87);
        this.gravityValues.put("Neptune", 11.15);
        this.gravityValues.put("Pluto", 0.62);

        // Return the completed environment menu
        return this.environmentMenu;

    }

    /**
     * Update the text on the labels in the control panel
     */
    private void updateLabels() {
        this.ballScoreLabel.setText("Ball: " + this.ballScore);
        this.playerScoreLabel.setText("Player: " + this.playerScore);
        this.timeLabel.setText("Time: " + this.time);
        this.velocityLabel.setText("Velocity (m/s): " + this.velocity);
        this.angleLabel.setText("Angle: " + this.angle + "\u00B0");
        this.boundsLabel.setText(this.inBounds ? "In Bounds" : "Out of Bounds");
    }

    /**
     * Handle the event being thrown by an item.
     */
    public void itemStateChanged(ItemEvent event) {

        CheckboxMenuItem source = (CheckboxMenuItem) event.getSource();

        // Check if the Size checkbox is being changed
        if (this.targetSizes.contains(source)) {
            for (CheckboxMenuItem cb : this.targetSizes)
                cb.setState(false);
            source.setState(true);
        }

        else if (this.targetSpeeds.contains(source)) {
            for (CheckboxMenuItem cb : this.targetSpeeds)
                cb.setState(false);
            source.setState(true);
        }

        else if (this.gravityAmounts.contains(source)) {
            for (CheckboxMenuItem cb : this.gravityAmounts)
                cb.setState(false);
            source.setState(true);
        }

        this.updateLabels();
    }

    /**
     * Handle the event being performed.
     */
    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();

        if (source == this.quitMenuItem)
            this.stop();
        else if (source == this.runMenuItem)
            this.runGame = true;
        else if (source == this.pauseMenuItem)
            this.runGame = false;
        else if (source == this.restartMenuItem)
            this.restart();

        this.updateLabels();
    }

    /**
     * Handle a scrollbar being adjusted
     */
    public void adjustmentValueChanged(AdjustmentEvent event) {

        Scrollbar sb = (Scrollbar) event.getSource();

        // Check if the velocity was updated
        if (sb == this.velocityScrollBar) {
            this.velocity = sb.getValue();
        }

        // Check if the angle was updated
        else if (sb == this.angleScrollBar) {
            this.angle = sb.getValue();
        }

        // Update the control panel labels
        this.updateLabels();
    }

    /**
     * Start the program's thread
     */
    private void start() {
        this.run = true;

        if (gameThread == null) {
            this.gameThread = new Thread(this);
            this.gameThread.start();
        }
    }

    /**
     * Update the game values
     */
    private void updateValues() {

        // Update gravity
        for (CheckboxMenuItem cb : this.gravityAmounts)
            if (cb.getState()) {
                this.gravity = this.gravityValues.get(cb.getLabel());
                break;
            }

        // Update the Size
        for (CheckboxMenuItem cb : this.targetSizes)
            if (cb.getState()) {
                this.ballSize = cb.getLabel();
                break;
            }
        this.gameCanvas.setBallSize(this.ballSize);

        // Update the Speed
        for (CheckboxMenuItem cb : this.targetSpeeds)
            if (cb.getState()) {
                this.ballSpeed = cb.getLabel();
                break;
            }

        this.gameCanvas.setCannonAngle(this.angle);
        this.ballScore = gameCanvas.getBallScore();
        this.playerScore = gameCanvas.getPlayerScore();
        this.gameCanvas.setAcceleration(this.gravity);

    }

    /**
     * Restart the game and reinitialize values
     */
    private void restart() {

        // Initialize Values
        this.ballScore = 0;
        this.playerScore = 0;
        this.time = 0;
        this.velocity = VELOCITY_INIT;
        this.angle = ANGLE_INIT;
        this.runGame = false;
        this.inBounds = true;

        // Reset the Parameters and Environment
        for (CheckboxMenuItem cb : this.gravityAmounts)
            cb.setState(false);
        this.gravityAmounts.get(0).setState(true);

        for (CheckboxMenuItem cb : this.targetSizes)
            cb.setState(false);
        this.targetSizes.get(0).setState(true);

        for (CheckboxMenuItem cb : this.targetSpeeds)
            cb.setState(false);
        this.targetSpeeds.get(0).setState(true);

        // Update Labels
        this.updateLabels();
    }

    /**
     * Clean up resources and stop the program.
     */
    private void stop() {

        // Remove checkbox listeners
        for (CheckboxMenuItem cb : this.targetSizes)
            cb.removeItemListener(this);
        for (CheckboxMenuItem cb : this.targetSpeeds)
            cb.removeItemListener(this);
        for (CheckboxMenuItem cb : this.gravityAmounts)
            cb.removeItemListener(this);

        // Remove action listeners for menu items
        this.runMenuItem.removeActionListener(this);
        this.pauseMenuItem.removeActionListener(this);
        this.quitMenuItem.removeActionListener(this);

        // Remove scroll bar listeners
        this.velocityScrollBar.removeAdjustmentListener(this);
        this.angleScrollBar.removeAdjustmentListener(this);

        // Remove window and frame listeners
        this.windowFrame.removeWindowListener(this);
        this.windowFrame.removeComponentListener(this);

        // Exit
        System.exit(0);
    }

    /**
     * Run the game
     */
    public void run() {

        while (this.run) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
            }

            if (this.runGame) {
                try {
                    Thread.sleep(THREAD_DELAY);
                } catch (InterruptedException e) {
                }

                // Update values
                this.clock++;
                this.updateValues();

                // Update ball on certain ticks

                // Update time label with adjustment
                if (this.clock % (500 / THREAD_DELAY) == 0)
                    this.time++;

                // Update labels after a game tick
                this.updateLabels();

                // Repaint the game canvas
                this.gameCanvas.move();
                this.gameCanvas.repaint();
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
            }

        }

    }

    public Rectangle getDragBox(Point newPoint) {
		return new Rectangle(Math.min(this.mouseLocation1.x, newPoint.x), 
                    Math.min(this.mouseLocation1.y, newPoint.y), 
		            Math.max(this.mouseLocation1.x, newPoint.x) - Math.min(this.mouseLocation1.x, newPoint.x), 
                    Math.max(this.mouseLocation1.y, newPoint.y) - Math.min(this.mouseLocation1.y, newPoint.y));
    }

    /**
     * Handle component resizing
     */
    public void componentResized(ComponentEvent e) {
        // Need to stop resizing if an object is hit
        Point newSize = new Point(this.windowFrame.getWidth(), this.windowFrame.getHeight());
        Point newScreenSize = new Point(newSize.x - 16, newSize.y - 106);
        if (this.gameCanvas.ableToResize(newScreenSize))
        {
            this.frameSize = new Point(newSize);
        }
        else
        {
            this.windowFrame.setSize(new Dimension(this.frameSize.x, this.frameSize.y));
        }

    }

    /* Unused Component Methods */
    public void componentHidden(ComponentEvent e) {
    }

    public void componentShown(ComponentEvent e) {
    }

    public void componentMoved(ComponentEvent e) {
    }

    /* Window Listenener Methods */
    public void windowClosing(WindowEvent e) {
        this.stop();
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowOpened(WindowEvent e) {
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }


    /* Mouse Listener Methods */
    public void mousePressed(MouseEvent event) {
        this.mouseLocation1.setLocation(event.getPoint());
        this.mousePressed = true;
    }

    public void mouseReleased(MouseEvent event) {
        // If the mouse was not pressed, do nothing
        if (!this.mousePressed)
            return;

        // Otherwise, do a bounding box
        this.mouseLocation2.setLocation(event.getPoint());
        this.gameCanvas.addObstacle(this.getDragBox(event.getPoint()));
        
        // Set the mouse pressed back to false
        this.mousePressed = false;
        this.gameCanvas.repaint();
    }

    public void mouseDragged(MouseEvent event) {
        if (this.mousePressed)
        {
            this.dragBox.setBounds(this.getDragBox(event.getPoint()));
            this.gameCanvas.setDragBox(this.dragBox);
            this.gameCanvas.repaint();
        }
    } 

    public void mouseMoved(MouseEvent event) {

    }

    public void mouseClicked(MouseEvent event) {
        
        this.dragBox = new Rectangle(0,0,0,0);
        this.gameCanvas.setDragBox(this.dragBox);

        if (event.getButton() != 1)
            return;

        if (event.getClickCount() == 1)
            this.gameCanvas.fireCannon(event.getPoint());
        else if (event.getClickCount() == 2)
            this.gameCanvas.deleteObstacles(event.getPoint());
        
    }

    public void mouseEntered(MouseEvent event) {
        this.gameCanvas.repaint();
    }

    public void mouseExited(MouseEvent event) {

    }

}








/**
 * The Game Canvas
 */
class GameCanvas extends Canvas {
    /* UID */
    private static final long SerialVersionUID = 11L;

    /* Constants */
    private final static Rectangle ZERO_RECTANGLE = new Rectangle(0,0,0,0);
    private final static Point ZERO_POINT = new Point(0,0);

    /* Screen Values */
    private Point screenSize;
    private Rectangle perimeter;

    /* Ball Constants */
    public static final String BALL_SIZE_XSMALL = "Extra Small";
    public static final String BALL_SIZE_SMALL = "Small";
    public static final String BALL_SIZE_MEDIUM = "Medium";
    public static final String BALL_SIZE_LARGE = "Large";
    public static final String BALL_SIZE_XLARGE = "Extra Large";
    public static final String BALL_SPEED_SLOW = "Slow";
    public static final String BALL_SPEED_NORMAL = "Normal";
    public static final String BALL_SPEED_FAST = "Fast";

    /* Ball Values */
    private int ballSize, ballSpeed;
    private boolean moveDown, moveRight;
    private Rectangle ball;

    /* Cannon Constants */
    private static final int CANNON_LENGTH = 90;
    private static final int CANNON_WIDTH = 30;
    private static final int CANNON_BASE_SIZE = 58;
    private static final int CANNON_ANGLE_INIT = 45;

    /* Cannon Values */
    private Polygon cannon;
    private Point cannonBasePoint, cannonTipPoint, cannonLaunchTip;
    private int cannonAngle, launchAngle, launchVelocity;
    private double launchAcceleration;

    /* Projectile Constants */
    private static final int PROJECTILE_SIZE = 15;

    /* Projectile Values */
    private Point projectile;
    private int projectileSize;
    private boolean projectileFired;
    private double projectileVelocity, projectileAcceleration;

    /* Game Values */
    private int ballScore, playerScore;

    /* Obstacle Values */
    private Vector<Rectangle> walls, edges;

    /* Canvas Values */
    private Image imageBuffer;
    private Graphics graphics;
    private Rectangle dragBox;

    /* Misc Values */
    private Random rand;

    /**
     * Default constructors
     */
    public GameCanvas(Point size) {
        this.screenSize = size;
        this.perimeter = new Rectangle(1, 1, this.screenSize.x - 2, this.screenSize.y - 2);
        this.projectileFired = false;

        this.rand = new Random();

        // Init Obstacles
        this.walls = new Vector<>();
        this.edges = new Vector<>();

        // Init Canvas Values
        this.dragBox = new Rectangle(GameCanvas.ZERO_RECTANGLE);

        // Init Game Values
        this.ballScore = 0;
        this.playerScore = 0;

        // Init Cannon
        this.cannon = new Polygon();
        this.cannonBasePoint = new Point(this.screenSize.x - 31, this.screenSize.y - 31);
        this.cannonTipPoint = new Point(GameCanvas.ZERO_POINT);
        this.setCannonAngle(GameCanvas.CANNON_ANGLE_INIT);

        // Init Projectile
        this.projectileSize = GameCanvas.PROJECTILE_SIZE;
        this.projectile = new Point();
        this.resetProjectile();

        // Init Ball Values
        this.ball = new Rectangle(GameCanvas.ZERO_RECTANGLE);
        this.setBallSize(GameCanvas.BALL_SIZE_XSMALL);
        this.resetBall();
        this.moveDown = true;
        this.moveRight = true;
    }

    /* Score Getters */
    public int getBallScore() {
        return this.ballScore;
    }

    public int getPlayerScore() {
        return this.playerScore;
    }

    
    public void resetProjectile() {
        this.projectile.setLocation(this.cannonTipPoint);
    }

    public void setDragBox(Rectangle newDragBox) {
        if (this.perimeter.contains(newDragBox))
            this.dragBox = newDragBox;
    }

    public void setAcceleration(double acceleration) {
        this.projectileAcceleration = acceleration;
    }
    
    /**
     * Set the ball size
     * @param size String for the ball size
     */
    public void setBallSize(String size) {
        switch(size) {
            case GameCanvas.BALL_SIZE_XSMALL:
                this.ballSize = 10;
                break;

            case GameCanvas.BALL_SIZE_SMALL:
                this.ballSize = 20;
                break;
            
            case GameCanvas.BALL_SIZE_MEDIUM:
                this.ballSize = 40;
                break;

            case GameCanvas.BALL_SIZE_LARGE:
                this.ballSize = 70;
                break;

            case GameCanvas.BALL_SIZE_XLARGE:
                this.ballSize = 100;
                break;
        }
    }

    
    private void resetBall() {

        boolean fits;
        
        do
		{
			fits = true;														
			this.ball.x = 2 + rand.nextInt(this.screenSize.x - this.ballSize - 4) + this.ballSize/2;
			this.ball.y = 2 + rand.nextInt(this.screenSize.y - this.ballSize - 4) + this.ballSize/2;

            for (Rectangle wall : this.walls)
                if (this.getBallHitbox().intersects(wall))
                    fits = false;

            fits = !(this.getBallHitbox().intersects(this.getCannonHitbox()) || this.cannon.intersects(this.getBallHitbox()));

            this.moveDown = this.ball.x % 2 == 0;
            this.moveRight = this.ball.y % 2 == 0;
			
		}while(!fits);

    }

    private Rectangle getBallHitbox() {
        return new Rectangle(this.ball.x - this.ballSize/2, this.ball.y - this.ballSize/2, this.ballSize+1, this.ballSize+1);
    }

    private Rectangle getProjectileHitbox() {
        return new Rectangle(this.projectile.x - this.projectileSize/2, this.projectile.y - this.projectileSize/2, 
                    this.projectileSize, this.projectileSize);
    }

    private Rectangle getCannonHitbox() {
        return new Rectangle(this.cannonTipPoint.x, this.cannonTipPoint.y, this.screenSize.x, this.screenSize.y);
    }

    public void setCannonAngle(int angle) {
        this.cannonAngle = angle;
        double radians = ((double)this.cannonAngle / 360)*2*Math.PI;
        double adjustedAngle = Math.PI / 2 - radians;
        
        // Move the cannon tip point
        int x = (int) (this.cannonBasePoint.x - Math.cos(radians) * GameCanvas.CANNON_LENGTH);
        int y = (int) (this.cannonBasePoint.y - Math.sin(radians) * GameCanvas.CANNON_LENGTH);
        this.cannonTipPoint.setLocation(x, y);

        // Setup the polygon for the cannon barrel
        x = cannonBasePoint.x;
        y = cannonBasePoint.y;
        int adjustedWidth = (int)(Math.cos(adjustedAngle)*(GameCanvas.CANNON_WIDTH/2));
        int adjustedHeight = (int)(Math.sin(adjustedAngle)*(GameCanvas.CANNON_WIDTH/2));
        this.cannon = new Polygon();
        this.cannon.addPoint(x - adjustedWidth, y + adjustedHeight);
        this.cannon.addPoint(x + adjustedWidth, y - adjustedHeight);
        x = cannonTipPoint.x;
        y = cannonTipPoint.y;
        this.cannon.addPoint(x + adjustedWidth, y - adjustedHeight);
        this.cannon.addPoint(x - adjustedWidth, y + adjustedHeight);

        if (!this.projectileFired && this.projectile != null)
            this.resetProjectile();
    }

    /**
     * Add an obstacle to the game
     * @param obstacle the Rectangle representing the obstacle
     */
    public void addObstacle(Rectangle obstacle) {

        boolean intersects = false;
        
        // Check if the new obstacle intersects with the ball
        if (obstacle.intersects(this.getBallHitbox()))
            intersects = true;

        // Check if the obstacle intersects with the projectile
        if (!intersects && obstacle.intersects(this.getProjectileHitbox()))
            intersects = true;

        // Check if the obstacle intersects with the cannon
        if (!intersects && obstacle.intersects(this.getCannonHitbox()))
            intersects = true;

        // Check if the new obstacle against existing walls
        int index = 0;
        Rectangle wall;
        while (index < this.walls.size() && !intersects) 
        {
            wall = this.walls.elementAt(index);
            if (obstacle.intersection(wall).equals(wall))
            {
                this.walls.remove(index);
                this.edges.remove(index*4);
                this.edges.remove(index*4);
                this.edges.remove(index*4);
                this.edges.remove(index*4);
                index--;
            }
            if (wall.intersection(obstacle).equals(obstacle))
            {
                intersects = true;
            }
            index ++;
        }

        // Add the new Obstalce to the list if it's big enough and doesn't intersect anything
        if (!intersects && obstacle.width > 0 && obstacle.height > 0)
        {
            this.walls.addElement(new Rectangle(obstacle));
			this.edges.addElement(new Rectangle(obstacle.x, obstacle.y - 1, obstacle.width, 1));				
			this.edges.addElement(new Rectangle(obstacle.x + obstacle.width, obstacle.y, 1, obstacle.height));
			this.edges.addElement(new Rectangle(obstacle.x, obstacle.y + obstacle.height, obstacle.width, 1));
			this.edges.addElement(new Rectangle(obstacle.x - 1, obstacle.y, 1, obstacle.height));
        }

        this.dragBox = new Rectangle(GameCanvas.ZERO_RECTANGLE);
    }

    public void update(Graphics graphics) {
        paint(graphics);
    }

    public void paint(Graphics p_Graphics) {

        // Create the image buffer
        this.imageBuffer = createImage(this.screenSize.x, this.screenSize.y);
        
        // Get new graphics
        if ( graphics != null )
            graphics.dispose();
        graphics = this.imageBuffer.getGraphics();

        // Draw the border
        graphics.setColor(Color.blue);
        graphics.drawRect(this.perimeter.x, this.perimeter.y, this.perimeter.width, this.perimeter.height);

        // Draw the Ball
        graphics.setColor(Color.red);
        graphics.fillOval(this.ball.x - this.ballSize/2, this.ball.y - this.ballSize/2, this.ballSize, this.ballSize);
        graphics.setColor(Color.black);
        graphics.drawOval(this.ball.x - this.ballSize/2, this.ball.y - this.ballSize/2, this.ballSize, this.ballSize);

        // Draw the drag box
        graphics.setColor(Color.black);
        graphics.drawRect(this.dragBox.x, this.dragBox.y, this.dragBox.width, this.dragBox.height);
        
        // Draw the obstacles
        graphics.setColor(Color.orange);
        for (Rectangle wall : this.walls)
            graphics.fillRect(wall.x, wall.y, wall.width, wall.height);

        // Draw the Projectile
        graphics.setColor(Color.blue);
        graphics.fillOval(this.projectile.x - this.projectileSize/2, this.projectile.y - this.projectileSize/2, 
                    this.projectileSize, this.projectileSize);

        // Draw the cannon
        graphics.setColor(Color.gray);
        graphics.fillPolygon(this.cannon);
        graphics.setColor(Color.MAGENTA);
        graphics.fillOval(this.cannonBasePoint.x - GameCanvas.CANNON_BASE_SIZE/2, 
                    this.cannonBasePoint.y - GameCanvas.CANNON_BASE_SIZE/2,
                    GameCanvas.CANNON_BASE_SIZE, GameCanvas.CANNON_BASE_SIZE );

        // Draw the buffer to the screen
        p_Graphics.drawImage(this.imageBuffer, 0, 0, null);
    }

    public void move() {
        this.moveBall();
        this.moveProjectile();
    }

    
    public void moveBall() {

        // Move the ball
        ball.y += this.moveDown ? 1 : -1;
        ball.x += this.moveRight ? 1 : -1;

        // Get the hitbox of the ball, and make it slightly larger
        Rectangle ballHitbox = this.getBallHitbox();
        ballHitbox.grow(1,1);

        // Bounce off obstacles
        for(int i = 0; i < this.edges.size() ; i+=4) 
        {
            if (ballHitbox.intersects(this.edges.get(i)))
                this.moveDown = false;
            if (ballHitbox.intersects(this.edges.get(i+2)))
                this.moveDown = true;
            if (ballHitbox.intersects(this.edges.get(i+1)))
                this.moveRight = true;
            if (ballHitbox.intersects(this.edges.get(i+3)))
                this.moveRight = false;
        }

        // Bounce off the borders
        if ( ballHitbox.y <= this.perimeter.y )
            this.moveDown = true;
        if ( (ballHitbox.y + ballHitbox.height) >= (this.perimeter.y + this.perimeter.height))
            this.moveDown = false;
        if ( ballHitbox.x <= this.perimeter.x )
            this.moveRight = true;
        if ( (ballHitbox.x + ballHitbox.width) >= (this.perimeter.x + this.perimeter.width))
            this.moveRight = false;

        // Check if it intersects with the cannon and destroys it this round
        if (ballHitbox.intersects(this.getCannonHitbox()) || this.getCannonHitbox().intersects(ballHitbox) )
        {
            this.resetBall();
            this.ballScore++;
        }
    }


    public void moveProjectile() {

        if (!projectileFired)
            return;

        

    }

    public void fireCannon(Point mousePoint) {
        if (this.getCannonHitbox().contains(mousePoint))
            this.projectileFired = true;
    }

    public void deleteObstacles(Point mousePoint) {
        int index = 0;
        Rectangle wall;
        while (index < this.walls.size()) 
        {
            wall = this.walls.elementAt(index);
            if (wall.contains(mousePoint))
            {
                this.walls.remove(index);
                this.edges.remove(index*4);
                this.edges.remove(index*4);
                this.edges.remove(index*4);
                this.edges.remove(index*4);
                index--;
            }
            index ++;
        }
    }

    public boolean ableToResize(Point newSize) {

        boolean ableToResize = true;

        // Check the ball
        if (newSize.x < (this.ball.x + this.ballSize/2) || newSize.y < (this.ball.y + this.ballSize/2))
            ableToResize = false;

        // Check all the walls
        for (Rectangle wall : this.walls)
            if (newSize.x < (wall.x + wall.width) || newSize.y < (wall.y + wall.height) )
                ableToResize = false;

        // Resize if able to
        if (ableToResize)
        {
            this.screenSize = new Point(newSize);
            this.perimeter = new Rectangle(2, 2, this.screenSize.x - 2, this.screenSize.y - 2);
            this.cannonBasePoint.setLocation(this.screenSize.x - GameCanvas.CANNON_BASE_SIZE/2, 
                                    this.screenSize.y - GameCanvas.CANNON_BASE_SIZE/2);
        }

        return ableToResize;
    }

}
