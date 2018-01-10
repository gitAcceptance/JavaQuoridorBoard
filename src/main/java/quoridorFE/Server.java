package quoridorFE;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

/*
 * The server that can be run both as a console application or a GUI
 */
public class Server {

    // a unique ID for each connection
    private static int uniqueId;
    // an ArrayList to keep the list of the Client
    private ArrayList<ClientThread> al = new ArrayList<ClientThread>();
    // to display time
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    // the port number to listen for connection
    private int port;
    // the boolean that will be turned of to stop the server
    private boolean keepGoing;
    // the name of the machine to be used
    private String machineName;

    private static FEai AI = new FEai();

	private QuoridorBoard board;
	public static Viewer viewer = null;

    /*
     *  server constructor that receive the port to listen to for connection as parameter
     *  in console
     */
    public Server(String machine, int port1) {
        port = port1;
        machineName = machine;
    }

    public void start() {
        keepGoing = true;
        /* create socket server and wait for connection requests */
        try{
            // the socket used by the server
            ServerSocket serverSocket = new ServerSocket(port);


            // MAIN LOOP
            // infinite loop to wait for connections
            while(keepGoing) {
                // format message saying we are waiting
                display("Server waiting for Clients on port " + port + ".");

                Socket socket = serverSocket.accept();          // accept connection
            	//System.out.println("	MADE IT");
                // if I was asked to stop
                if(!keepGoing)
                    break;
                ClientThread t = new ClientThread(socket);  // make a thread of it
                al.add(t);                                                                        // save it in the ArrayList
                t.start();
            }
            // I was asked to stop
            try {
                serverSocket.close();
                for(int i = 0; i < al.size(); ++i) {
                    ClientThread tc = al.get(i);
                    try {
                        tc.IOscannerIn.close();
                        tc.IOscannerOut.close();
                        tc.socket.close();
                    } catch(IOException ioE) {

                    }
                }
            }
            catch(Exception e) {
                display("Exception closing the server and clients: " + e);
            }
        }
        // something went bad
        catch (IOException e) {
            String msg = sdf.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
            display(msg);
        }
    }                
    /*
     * Display an event (not a message) to the console or the GUI
     */
    private void display(String msg) {
        System.out.println(msg);
    }

	public void stop(){
		keepGoing = false;
	}


    /*
     *  to broadcast a message to all Clients
     */
    private synchronized void broadcast(String message) {
        // add HH:mm:ss and \n to the message
        String time = sdf.format(new Date());
        String messageLf = time + " " + message + "\n";
        // display message on console or GUI
        System.out.print(messageLf);

        // we loop in reverse order in case we would have to remove a Client
        // because it has disconnected
        for(int i = al.size(); --i >= 0;) {
            ClientThread ct = al.get(i);
            // try to write to the Client if it fails remove it from the list
            if(!ct.writeMsg(messageLf)) {
                al.remove(i);
                display("Disconnected Client " + ct.username + " removed from list.");
            }
        }
    }

    // for a client who logoff using the LOGOUT message
    synchronized void remove(int id) {
        // scan the array list until we found the Id
        for(int i = 0; i < al.size(); ++i) {
            ClientThread ct = al.get(i);
            // found it
            if(ct.id == id) {
                al.remove(i);
                return;
            }
        }
    }

    /*
     *  To run as a console application just open a console window and:
     * > java Server
     * > java Server portNumber
     * If the port number is not specified 1500 is used
     */
    public static void main(String[] args) {
        // start server on port 1500 unless a PortNumber is specified 
        int portNumber = 1500;
        String machineName = "localhost";
        switch(args.length) {
        case 2:
            try {
                machineName = args[0];
                portNumber = Integer.parseInt(args[1]);
            } catch(Exception e) {
                System.out.println("Invalid port number.");
                System.out.println("Usage is: > java Server [portNumber]");
                return;
            }
        case 0:
            break;
        default:
            System.out.println("Usage is: > java Server [portNumber]");
            return;

        }
        // create a server object and start it
        Server server = new Server(machineName, portNumber);
        server.start();
    }

    /** One instance of this thread will run for each client */
    class ClientThread extends Thread {
            // String constant to pose as playername in the IAM message
            String PLAYERNAME = "fex:JONNYBOY";
            // the socket where to listen/talk
            Socket socket;
        // IO handlers
            Scanner IOscannerIn;
            PrintWriter IOscannerOut;
            // my unique id (easier for deconnection)
            int id;
            // the Username of the Client
            String username;
            // the only type of message a will receive
            String cm;
            // the date I connect
            String date;
            
            int playerId = 0;

            // Constructor
            ClientThread(Socket socket) {
                this.socket = socket;
                /* Creating both Data Stream */
                System.out.println("Thread trying to create Input/Output Streams");
                try {
                    // create output first
            		IOscannerOut = new PrintWriter(socket.getOutputStream(), true);
                    IOscannerIn  = new Scanner(socket.getInputStream());
                } catch (IOException e) {
                    display("Exception creating new Input/output Streams: " + e);
                    return;
                }
            }
            
            //  will run forever
            public void run() {

                //ANOTHER MAIN LOOP
                // I want our program to spin until it sees "hello"
                // im going to add a boolean to toggle the spin
                boolean helloSpin = true;
                boolean gameSpin = true;
		//String[] sc;
                // to loop until LOGOUT
                //boolean keepGoing = true;
                while(keepGoing) {
                    // to keep everything simple i want to Read once and write once!
                    // im gong to initialize the string to write above everything and 
                    // writeMsg once at the bottom.
                    String answer = "";
                    // read a String
                    String message = IOscannerIn.nextLine();
                    // the message part of the ChatMessage
                    System.out.println("Recieved: " + message);
                    String msg = message;
                    msg = msg.replace(',', ' ');
                    msg = msg.replace('(', ' ');
                    msg = msg.replace(')', ' ');
                    msg = msg.replace('[', ' ');
                    msg = msg.replace(']', ' ');
                    //System.out.println("  Fixed String. " + message);
                    // Split message, parse out what we want and update the local board.
                    String[] sc = msg.split("\\s+");

                    if (helloSpin) {
                        if(message.equalsIgnoreCase("HELLO")) {
                            // Flip the hello toggle and build the IAM message
                            helloSpin = false;
                            answer = "IAM " + PLAYERNAME;
                            System.out.println("Sending: " + answer);
                            writeMsg(answer);
                        }
                    } else if (gameSpin) {
                    	//String[] sc = message.split("\\s+");
               		//GAME <p> <name1> <name2> [<name3> <name4>]
                    	if (message.contains("GAME")) {
                            gameSpin = false;
                            //TODO find a way of communicating overall number of players to server	
                            playerId = Integer.parseInt(sc[1]);
                            // If two playersg.
				if(sc.length == 4){
					board = new QuoridorBoard(new Player(1, sc[2], 0000, 10, 4, 0), new Player(2, sc[3], 0000, 10, 4, 8));
					System.out.println("Two players locked in!");
				} else {// is four player
				        board = new QuoridorBoard(new Player(1, sc[2], 0000, 5, 4, 0), new Player(2, sc[3], 0000, 5, 4, 8), new Player(3, sc[4], 0000, 5, 0, 4), new Player(4, sc[5], 0000, 5, 8, 4));
				        System.out.println("Four players locked in!");
				}
				/**
							 * How to launch the application thread in order to be
							 * able to update the player move
							 
							Thread t = new Thread() {
							    @Override
							    public void run() {
							        javafx.application.Application.launch(Viewer.class);
							    }
							};
							t.setDaemon(true);
							t.start();
							/**
							 * Called after launching the UI
							 
							viewer = Viewer.waitForViewerStartUp();
							    
							// Call function from set board
							viewer.setBoard(board);
							
							//viewer.testTheReference();
							//System.out.println("I ran the thing.");
							viewer.refresh();
							
							// Refresh the board state 
							// Viewer.refresh();
                        */
				}
			/*
		        try {
               		    Thread.sleep(100);
        		} catch(InterruptedException ex) {
        		    Thread.currentThread().interrupt();
        		}*/

                    } else if(message.contains("MYOUSHU")){ // I'm being requested for a move.
                        System.out.println("I will give you a move, give me a god damned second..");
			// If we are fighting two players..	
			//if(board.getPlayerSet().size() <= 2){
                    	answer = "TESUJI " + AI.getMove(playerId, board);
                        System.out.println("Sending: " + answer);
                        writeMsg(answer);
                    } else if(message.contains("ATARI")){ // Someone has just moved legally and it's being broadcast.
						/* tired of parsing clutter, so removed all.
						message = message.replace(',', ' ');
						message = message.replace('(', ' ');
						message = message.replace(')', ' ');
						message = message.replace('[', ' ');
						message = message.replace(']', ' ');
						//System.out.println("	Fixed String. " + message);*/
						// Split message, parse out what we want and update the local board.
						//String[] sc = message.split("\\s+");
						// Is a wall move
						if(message.toLowerCase().contains("h") || message.toLowerCase().contains("v")){
							// FIXME THIS IS NOT CHECKING TO SEE IF IT'S A VALID MOVE
							board.placeWall(Integer.parseInt(sc[1]), Integer.parseInt(sc[2]), Integer.parseInt(sc[3]), sc[4].charAt(0));
							//viewer.refresh();
							System.out.println("placed wall at [(" + sc[2] +", "+ sc[3] +") " + sc[4] + "]");
						}else{
							// ..else is a pawn move
							// FIXME THIS IS NOT CHECKING TO SEE IF IT'S A VALID MOVE
							board.movePawn(Integer.parseInt(sc[1]), Integer.parseInt(sc[2]), Integer.parseInt(sc[3]));
							//viewer.refresh();
							System.out.println("placed pawn at (" + sc[2] +", "+ sc[3] +")");
						}
                        //System.out.println("Recieved: " + message);
                    } else if(message.contains("GOTE")){ // Someone made and illegal move and is now gone.
                    	board.removePlayer(Integer.parseInt(sc[1]));
                    	//viewer.refresh();
                        System.out.println("Player kicked: " + sc[1]);
                    } else if(message.contains("KIKASHI")){ // Game is over and someone won.
                        //System.out.println("Recieved: " + message);
                        break;
                    }
                }
                // remove myself from the arrayList containing the list of the
                // connected Clients
                //System.out.println("Player #" + sc[1] + " has won!");
		System.out.println("	Exiting.");
                remove(id);
                System.out.print(".");
                close();
		System.out.print(".");
		System.exit(0);
            }

            // try to close everything
            private void close() {
                // try to close the connection
                try {
                    if(IOscannerOut != null) IOscannerOut.close();
                } catch(Exception e) {}
                try {
                    if(IOscannerIn != null) IOscannerIn.close();
                } catch(Exception e) {};
                try {
                    if(socket != null) socket.close();
                } catch (Exception e) {}
            }

            /*
             * Write a String to the Client output stream
             */
            private boolean writeMsg(String msg) {
                // if Client is still connected send the message to it
                //if(!socket.isConnected()) {
                //close();
                //return false;
                //}
                // write the message to the stream
                IOscannerOut.println(msg);
               
                return true;
            }

            // this is a method to print out a "prompt" for the game, it tells you how to send
            //moves and such.
            public void usage() {
                System.out.println("Welcome to the game of Quorridor!");
                System.out.println("Pay attention or you could get BANNED!");
                System.out.println("The Client will request a move by sending you \"MYOUSHU\"");
                System.out.println("This should trigger you to think about what you want and make a move.");
                System.out.println("Once you get this... send back \"TESUJI\" followed by your move.");
                System.out.println("Moves can either be a pawn move, or a wall placement.");
                System.out.println("  Walls are specified by a starting square ABOVE or");
                System.out.println("  to the LEFT of the wall and an h or w for a ");
                System.out.println("  horizontal or vertical wall.");
                System.out.println("To send a move for your pawn use:   ");
                System.out.println("The whole wall designation is enclosed in");
                System.out.println("square brackets:");
                System.out.println("For Example:");
                System.out.println("A pawn move is a cordinate pair");
                System.out.println("<TESUJI> <(x,y)>");
                //yes im sure that the parenthesis are nessisary
                System.out.println("A valid wall declaration would be");
                System.out.println("<[(1, 0), v]> or <[(1, 0), h]>");
                System.out.println("Make valid moves or you will be BANNED");
                System.out.println("Good Luck!");

            }
    }
}

/** Protocol

 ** Initial Contacted
HELLO

    Client - Initial contact. Sent to elicit the IAM message.

IAM <name>

    Server - Response to HELLO; includes server's preferred display
    name. Display name cannot contain any whitespace.


GAME <p> <name1> <name2> [<name3> <name4>]

    Client - Game is ready to start. <p> is the player number for the
    server receiving this message. Players are numbered from 1. There
    are either 2 or 4 names. As in IAM, names cannot contain any
    whitespace.

    Players are numbered (clockwise from the top of the board)
    1, 4, 2, 3. This means that the player moving from lower to higher
    numbers (be it rows for player 1 and columns for player 3) comes
    first in the pair. The order of play is clockwise around the board
    from player 1.

 ** During Play

MYOUSHU

    Client - Request for a move. Server should be expecting this
    message as it knows which player last moved as well as its own
    player number.

TESUJI <move-string>

    Server - Response to MYOUSHU, includes the move made by the
    player. The move is the target location for the player's pawn or
    the location to place a wall.

ATARI <p> <move-string>

    Client - Communicates player #p's move to all players.

GOTE <p>

    Message sent by game-client to all move-servers informing them
    that <p> made an illegal move and is no longer in the game. The
    pawn for <p> should be removed from the game board and any
    remaining walls are lost. Note: sent as the very last message to
    the offending move-server.

KIKASHI <p>
    Message sent by game-client to all move-servers informing them
    that the game is over and the given player won. The game-client
    cannot send any additional messages to any move-server after
    sending this message.

 * Coordinates

    The board is viewed canonically with player 1 moving from the top to
    the bottom and player 3 moving from left to right. Columns and rows
    are numbered from 0-8 (from left-to-right for columns; from
    top-to-bottom for rows).

    A board position (a square where a pawn moves) is specified by an
    ordered pair (column, row). Each index ranges from 0-8. Since both
    rows and columns are labeled alike, make sure to keep track of which
    is which.

    Player 1 starts in position (4, 0) and is moving to row 8; player 2
    starts in position (4, 8) and is moving to row 0.

    Walls are specified by a starting square ABOVE or to the LEFT of the
    wall and an h or w for a horizontal or vertical wall. The whole wall
    designation is enclosed in square brackets: [(1, 0), v] or [(1, 0), h].

    Note that there are wall coordinates that are not actually valid: Any
    horizontal wall with 8 as its row, or any vertical wall with 8 as its
    column is not permitted.
 */
