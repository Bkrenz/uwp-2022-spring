package networkchat;

/**
 * CET 350 - Java
 * Assignment 7
 * 
 * Group 5
 * @author Robert Krency, rdk5039@calu.edu
 * @author Kevin Reisch, rei3819@calu.edu
 */

import java.io.*;

import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetSocketAddress;

import java.awt.*;
import java.awt.event.*;

public class Chat
    implements Runnable, ActionListener, WindowListener
{

    public static void main(String[] args) 
    {
        (new Chat()).start();
    }
    
    /* Window Constants */
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;

    /* Frame Components */
    Frame frame;
    Thread thread;
    Button changePortButton, sendButton, serverButton, clientButton, disconnectButton, changeHostButton;
    Label portLabel, hostLabel;
    TextField chatTextField, portTextField, hostTextField;

    /* Networking Constants */
    protected static final boolean auto_flush = true;
    private static final int DEFAULT_PORT = 44004;
    private static final int TIMEOUT = 1000;

    /* Networking Values */
    private ServerSocket server;
    private Socket client;
    private String hostName;
    private int timeout;

    /* IO Values */
    private BufferedReader reader;
    private PrintWriter writer;

    /* Program Operation Control Values */
    private State state;



    /**
     * Constructor; builds the window.
     */
    public Chat()
    {
        this.timeout = Chat.TIMEOUT;
    }

    public Chat(int p_Timeout) 
    {
        this.timeout = p_Timeout;
    }


    public void setupWindow() 
    {
        
        // Setup the Buttons
        this.changePortButton = new Button("Change Port");
        this.changeHostButton = new Button("Change Host");
        this.sendButton = new Button("Send");
        this.serverButton = new Button("Start Server");
        this.disconnectButton = new Button("Disconnect");
        this.clientButton = new Button("Connect");

    }


    public void start() 
    {
        if (this.thread == null)
        {
            this.thread = new Thread(this);
            this.thread.start();
        }
    }

    public void stop()
    {
        // Close the sockets and IO operators
        switch (this.state) {
            case Client:
                this.stopClient();
                break;
            case Server:
                this.stopServer();
                break;
            default:
                break;
        }

        // Set the state to Initialized
        this.state = State.Initialized;

        // Set thread priority to min

        // Remove Listeners

        // Dispose of the frame
        this.frame.dispose();

        // Exit the program
        System.exit(0);
    }


    /**
     * Program State values
     */
    private enum State {
        Initialized,
        Server,
        Client
    }

}
