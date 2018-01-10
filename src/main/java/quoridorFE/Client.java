package quoridorFE;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;
/*
 * The Client that can be run both as a console or a GUI
 */
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client  {

    // Declared list of current players, 2 or 4 long until people start gettting kicked.
    private static ArrayList<Player> players = new ArrayList<Player>();
    private static LinkedList<Player> turnList;
    private static ListIterator<Player> whosTurnIsIt;
    private static Player currentPlayer;

    // for I/O
    private Scanner IOscannerIn;
    private PrintWriter IOscannerOut;
    private Socket socket;

    // the server and the port
    private String server;
    private int port;

    // Thread safe semaphore.
    static Semaphore semaphore = new Semaphore(1);
    static Semaphore boardLock = new Semaphore(1);
    public static boolean listen_loop = true;

    // Bools for our commandline parameter flags.
    public static boolean text_only = false;
    public static boolean gui_only = false;
    public static boolean automate = true;
    public static int DELAY = 500;
    // This is our EVERYTHING, the board that will hold the players, walls and their board states.
    public static QuoridorBoard board;
    public static ArrayList<Client> clients;
    public static Viewer viewer = null;

    /*
     *
     *  server: the server address
     *  port: the port number
     *  username: the username
     */
	Client(String server, int port) {
		this.server = server;
		this.port = port;
	}

    /*
     * To start the dialog
     */
    public boolean start() {
        // try to connect to the server
        try {
        	socket = new Socket(server, port);
        }

        catch(Exception ec) {
        	// display("Error connectiong to server:" + ec);
            return false;
        }

        String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
        //display(msg);
        /* Creating both Data Stream */
        try {
            IOscannerIn  = new Scanner(socket.getInputStream());
            IOscannerOut = new PrintWriter(socket.getOutputStream(), true);
        }
        catch (IOException eIO) {
           // display("Exception creating new Input/output Streams: " + eIO);
            return false;
        }

        // creates the Thread to listen from the server 
        new ListenFromServer().start();
      
        return true;
    }

  
    /*
     * To send a message to the server
     */
    void sendMessage(String msg) {
    	IOscannerOut.println(msg);
    }

   
    /*
     * When something goes wrong
     * Close the Input/Output streams and disconnect not much to do in the catch clause
     */
    private void disconnect() {
            try { 
                if(IOscannerIn != null) IOscannerIn.close();
            } catch(Exception e) {}
            try {
                if(IOscannerOut != null) IOscannerOut.close();
            } catch(Exception e) {}
            try{
                if(socket != null) socket.close();
            } catch(Exception e) {} 

            // inform the GUI
            //if(cg != null)
                 //   cg.connectionFailed();

    }
    /*
     * To start the Client in console mode use one of the following command
     * > java Client
     * > java Client username
     * > java Client username portNumber
     * > java Client username portNumber serverAddress
     * at the console prompt
     * If the portNumber is not specified 1500 is used
     * If the serverAddress is not specified "localHost" is used
     * If the username is not specified "Anonymous" is used

     * In console mode, if an error occurs the program simply stops
     * when a GUI id used, the GUI is informed of the disconnection
     */

    // Synchronized list to hold team names from threads.
    private static final CopyOnWriteArrayList<String> nameList = new CopyOnWriteArrayList<>();
    
    public static void main(String[] args) throws ClassNotFoundException, IOException {

        
        //Line to be used for regex
        String line = "";
        //Loop through commands and add them to line
        for (int n = 0; n < args.length; n++){
            line += args[n] + " ";
        }
        for (int i = 1; i < args.length; i++) {
            if(args[i].equals("--delay")) {
                DELAY = Integer.parseInt(args[i+1]);
                break;
            }
        }
        System.out.println("Delay is set to: " + DELAY);
        // Gotta check and see if they sent us any little flags. ;)
        if(line.contains("--text")){
            text_only = true;
            System.out.println("    Text Only is ON");
        }
        if(line.contains("--gui")){
            gui_only = true;
            System.out.println("    GUI Only is ON");
        }
        //TODO DELAY PARAMETER 

        //Replace all colons in line with whitespace
        String my_line = line.replaceAll(":", " ");

        //Split line up into individual string components
        String[] wordsOfCommandLineParameters = my_line.split("\\s+");

        // default values
        int portNumber = 1500;  //Default port
        String serverAddress = "localhost";  // Default hostname


        //Instantiate several clients to use
        Client client = new Client(serverAddress, portNumber);
        Client client2 = new Client(serverAddress, portNumber);
        Client client3 = new Client(serverAddress, portNumber);
        Client client4 = new Client(serverAddress, portNumber);

        //List for holding all clients
        //ArrayList<Client> 
        clients =  new ArrayList<Client>();

        //TODO change the names of the patterns and matchers to be more descriptive!

        //Patter to use for checking if two players
        //to check against command line parameters
        String pattern = "(.*)(\\s*)(:)(\\s*)(\\d+)(\\s*)(.*)(\\s*)(:)(\\s*)(\\d+)";

        //Pattern to use for checking if four players
        //to check against command line parameters
        String pattern2 = "(.*)(\\s*)(:)(\\s*)(\\d+)(\\s*)(.*)(\\s*)(:)(\\s*)(\\d+)(.*)(\\s*)(:)(\\s*)(\\d+)(\\s*)(.*)(\\s*)(:)(\\s*)(\\d+)";

        //Compile both patterns
        Pattern r = Pattern.compile(pattern);
        Pattern mr = Pattern.compile(pattern2);

        //Create matcher object to check for matching string
        Matcher m = r.matcher(line);
        Matcher m2 = mr.matcher(line);

        // depending of the number of arguments provided we fall through
        //Only runs if there are four players
        if(m2.find()) {
            try {
                serverAddress = wordsOfCommandLineParameters[0];
                portNumber = Integer.parseInt(wordsOfCommandLineParameters[1]);
                players.add(0, new Player(1, "", portNumber, 5, 0, 0));

                client = new Client(serverAddress, portNumber);
                serverAddress = wordsOfCommandLineParameters[2];
                portNumber = Integer.parseInt(wordsOfCommandLineParameters[3]);
                players.add(1, new Player(2, "", portNumber, 5, 0, 0));

                client2 = new Client(serverAddress, portNumber);
                serverAddress = wordsOfCommandLineParameters[4];
                portNumber = Integer.parseInt(wordsOfCommandLineParameters[5]);
                players.add(2, new Player(3, "", portNumber, 5, 0, 0));

                client3 = new Client(serverAddress, portNumber);
                serverAddress = wordsOfCommandLineParameters[6];
                portNumber = Integer.parseInt(wordsOfCommandLineParameters[7]);
                players.add(3, new Player(4, "", portNumber, 5, 0, 0));

                client4 = new Client(serverAddress, portNumber);
                clients.add(client);
                clients.add(client2);
                clients.add(client3);
                clients.add(client4);
                
                // New quoridor board init  --          public QuoridorBoard(Player player1, Player player2, Player player3, Player player4)
                currentPlayer = players.get(0);
                board = new QuoridorBoard(players.get(0), players.get(1), players.get(2), players.get(3));


                // Setup player objects time.
                // Some of these values will change as they become useful, like starting
				//positions for each player, etcc.
                // public Player(int ID, String name, int port, int wallsLeft, int startingX,
				//int startingY){
                //players.add(new Player(1, serverAddress, portNumber, 5, 0, 0));
                //currentPlayer = players.get(0);

                // test if we can start the connection to the Server
                // if it failed nothing we can do
                if(!client.start())
                    return;
                if(!client2.start())
                    return;
                if(!client3.start())
                    return;
                if(!client4.start())
                    return;
            } catch(Exception e) {
                System.out.println("Invalid port number.");
                System.out.println("Usage is: > java Client [username] [portNumber] [serverAddress]");
                return;
            }
        } else if(m.find()) {
        	// only runs if there are 2 players
            try {
                serverAddress = wordsOfCommandLineParameters[0];
                portNumber = Integer.parseInt(wordsOfCommandLineParameters[1]);
                //
                players.add(0, new Player(1, "", portNumber, 10, 0, 0));
                client = new Client(serverAddress, portNumber);
                serverAddress = wordsOfCommandLineParameters[2];
                portNumber = Integer.parseInt(wordsOfCommandLineParameters[3]);
                //
                players.add(1, new Player(2, "", portNumber, 10, 0, 0));
                client2 = new Client(serverAddress, portNumber);
                clients.add(client);
                clients.add(client2);
                
                // New board init  --          public QuoridorBoard(Player player1, Player player2) {
                currentPlayer = players.get(0);
                board = new QuoridorBoard(players.get(0), players.get(1));


                // test if we can start the connection to the Server
                // if it failed nothing we can do
                if(!client.start())
                    return;
                if(!client2.start())
                    return;

            }

            catch(Exception e) {
                System.out.println("Invalid port number.");
                System.out.println("Usage is: > java Client [username] [portNumber] [serverAddress]");
                return;
            }
        }

        else {
            System.out.println("Usage is: > java Client [username] [portNumber] {serverAddress]");
            return;
        }
        
        // wait for messages from user
        Scanner scan = new Scanner(System.in);
        // loop forever for message from the user
        int turn = 0;
        
        
        turnList = new LinkedList<Player>();
        for (Player p : board.getPlayerSet()) {
        	turnList.add(p);
        }
        Collections.sort(turnList);
        whosTurnIsIt = turnList.listIterator(0);
        
                             
        if(gui_only) {
		
			System.out.println("GUI is launching!!");
			
			//We only want to launch the viewer once
			//gui_only = false;
			
			// Launch to open Andrew's Viewer class
			// Viewer.launch(Viewer.class);  
			
			//TODO Launch GUI.
			
			/**
			 * How to launch the application thread in order to be
			 * able to update the player move
			 */
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
			 */
			viewer = Viewer.waitForViewerStartUp();
			    
			// Call function from set board
			viewer.setBoard(board);
			
			//viewer.testTheReference();
			//System.out.println("I ran the thing.");
			viewer.refresh();
			
			// Refresh the board state 
			// Viewer.refresh();
		  
        }
               

        //hello section
        int temp = 1;
        for (Client c : clients) {
            c.sendMessage("HELLO");
            System.out.println("Writing HELLO to player" + temp);
            temp++;
        }
        // Make thread sleep for a moment before requesting the next move.
        try {
            Thread.sleep(100);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        // GAME
        
        // FIXME why is this a for loop and not for each and why is does it depend on yet another collection that we have to keep synced with all the others
        for(int i = 0 ; i < nameList.size(); i++){
            System.out.print(" " + (i+1) + " " + nameList.get(i) + " ");

            //If two players, print send server its player number along with opponents name.
            if(clients.size() == 2){
                 clients.get(i).sendMessage("GAME " + (i+1) + " " + nameList.get(0) + " " + nameList.get(1));
            }
            //If four players, send server its player number along with all opponents names
            if(clients.size() == 4){
                 clients.get(i).sendMessage("GAME " + (i+1) + " " + nameList.get(0) + " " + nameList.get(1) + " " + nameList.get(2) + " " + nameList.get(3));
            }
        }


        try {
        	Thread.sleep(100);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }



        while(automate) {
        
			//System.out.println("	Nope");
			
			//If automation is on we do . . .
			System.out.println("Automating");
            // We want the turn time to be fixed so delay - start == new delay
			long START = System.currentTimeMillis();

			// This bit makes sure the iterator wraps around after everyone has made a move
			if (!whosTurnIsIt.hasNext()) {
				whosTurnIsIt = turnList.listIterator(turnList.indexOf(turnList.getFirst()));
			}
			
			// This next block keeps currentPlayer 
			currentPlayer = whosTurnIsIt.next();
			if (currentPlayer.getID() == 1) {
				nextTurn(client);
			} else if (currentPlayer.getID() == 2) {
				nextTurn(client2);
			} else if (currentPlayer.getID() == 3) {
				nextTurn(client3);
			} else if (currentPlayer.getID() == 4) {
				nextTurn(client4);
			}
			
			if(gui_only){
		    	// Refreshing the GUI
	        	    viewer.refresh();
			}
			// Gotta check if there is a winner yet!
			int temp1 = isWinner();
    			if(temp1 != 0){
	    		    System.out.println("There is a winner! Player #" + temp1 + " has won!");
			    //broadcast(clients, "");
    	    	            cleanUp(clients);
    	    		    //System.exit(0);
		    	    //Now have to wait for player to end game.
    		        }
			long END = System.currentTimeMillis();
    		// Make thread sleep for a moment before requesting the next move.
    		try {
        		Thread.sleep(DELAY - (END - START));
    		} catch(InterruptedException ex) {
        		Thread.currentThread().interrupt();
    		}										
        }
    }


    // Basically a simple method that shuts down all the servers.
    public static void cleanUp(ArrayList<Client> clients){
        System.out.println("  Cleaning up!");
        listen_loop = false;
	//automate = false;
        // FIXME could be for each loop
        for(int i=0; i<clients.size(); i++){
	    if(clients.get(i) != null){
            	try{
                    System.out.println("    Asking player " + clients.get(i).port + " to shutdown.");
                    clients.get(i).sendMessage("KIKASHI " + currentPlayer.getID());
                    //clients.get(i).disconnect();
            	}catch(Exception e){
                    System.out.print(e);
                    System.out.println("    Sending to: " + clients.get(i).port);
            	}
	    }else{
	    	System.out.println("Player not found.. skipping.");
	    }
        }
    }


	public static void broadcast(ArrayList<Client> clients, String text){
		// FIXME could be for each loop
		for(int i=0; i<clients.size(); i++){
            try{
                clients.get(i).sendMessage(text);
            }catch(Exception e){
                System.out.print(e);
                System.out.println("    Failure to send " + text + " to " + clients.get(i).port);
            }
        }
	}




    ///////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////
    public static void nextTurn(Client currentClient) throws ClassNotFoundException, IOException {
        // First we request a move from the server.
        currentClient.sendMessage("MYOUSHU");
        System.out.println("Sending MYOUSHU to Player: " + currentPlayer.getID());
        // Then we listen on the socket for the reply. TESUJI <move string>
        //String message = (String) sInput.readObject();

        try{
	    semaphore.acquire();
	    //System.out.println("    Semaphore acquired: "+semaphore);
        }catch(InterruptedException e){
            System.out.println(e);
        }
    }


    //Checks the state of all players and states whether someone has won the match. Returns the player number of the victor!
    public static int isWinner() {

		//If only one player remains!
		if(board.getPlayerSet().size() <= 1){
			System.out.println("One player remains, end of game!");
			return board.getPlayerSet().iterator().next().getID();
		}
		
		for (Player p : board.getPlayerSet()) {
			if(p.getID() == 1 && board.getNodeByPlayerNumber(1).getyPos() == 8){
				return 1;
			}
			if(p.getID() == 2 && board.getNodeByPlayerNumber(2).getyPos() == 0){
        		return 2;
			}
			if(p.getID() == 3 && board.getNodeByPlayerNumber(3).getxPos() == 8){
        		return 3;
			}
			if(p.getID() == 4 && board.getNodeByPlayerNumber(4).getxPos() == 0){
    			return 4;
			}
		}
		
		// If we found nothing then no one has won yet return false.
		return 0;
	}


    /*
     * a class that waits for the message from the server and append them to the JTextArea
     * if we have a GUI or simply System.out.println() if in console mode
     */
    class ListenFromServer extends Thread {
        public void run() {
        	//While the thread is still running
        	String msg = "";
            while(listen_loop) {
			//TODO cap all this code with an if statement calling a sycronized global method to aquire a lock based on expected turn order!
			// i think the syncronized method should go in the main client class 
			// somthing like this...
			// if(lock()){
            	
            	//FIXME this sets the names of the players
			    if(nameList.size() == 2 && players.size() == 2){
					players.get(0).setName(nameList.get(0));
					players.get(1).setName(nameList.get(1));
			    }
			    
			    if(nameList.size() == 4 && players.size() == 4){
					players.get(0).setName(nameList.get(0));
					players.get(1).setName(nameList.get(1));
					players.get(2).setName(nameList.get(2));
					players.get(3).setName(nameList.get(3));
			    }
	 

	    

			    //if( IOscannerIn.hasNextLine()){
				// reads in characters from server
				msg = IOscannerIn.nextLine();
				// concurrency stuff									
				//semaphore.release();
				//	System.out.println("	semaphore:" + semaphore);
				//}else{
				//	System.out.println("	has no line!");
				//}*/

				//TODO fix this print statement!													
				//System.out.println("Recieved from Player: " + currentPlayer.getID() + " msg: " + msg);
				// this println below works ... but we need the one above! 
                System.out.println("Recieved from Player: "+ currentPlayer.getID() + " msg: " + msg);
				
		        // tired of parsing clutter, so removed all.
		        msg = msg.replace(',', ' ');
		        msg = msg.replace('(', ' ');
		        msg = msg.replace(')', ' ');
		        msg = msg.replace('[', ' ');
		        msg = msg.replace(']', ' ');
		        //System.out.println("    Fixed String. " + msg);
		
				// splits string into seperat components
				String[] my_cord = msg.split("\\s+");
				
				// It is ID peach.
				if(msg.contains("IAM")) {
					System.out.println(my_cord[0] + " " + my_cord[1]);
					//FIXME this is where things are added to nameList
				    nameList.add(my_cord[1]);
				// It is a wall.
				} else if(msg.contains("TESUJI") && (msg.toLowerCase().contains("v") || msg.toLowerCase().contains("h") )){
					semaphore.release();
					//System.out.println("	Semaphore released: " + semaphore);
					try{
			            boardLock.acquire();
					    //System.out.println("    boardLocked!"+boardLock);
					    //semaphore.acquire();
					}catch(InterruptedException e){
					    System.out.println(e);
					}

					
					//System.out.println("ERROR 1? " + Arrays.toString(my_cord));
					if(board.isValidMove(currentPlayer.getID(), Integer.parseInt(my_cord[1]), Integer.parseInt(my_cord[2]), my_cord[3].charAt(0)) ){
					    //System.out.println("IS GOOD!");
					    board.placeWall(currentPlayer.getID(), Integer.parseInt(my_cord[1]), Integer.parseInt(my_cord[2]), my_cord[3].charAt(0));
					    System.out.println(currentPlayer.getID() + " " +  Integer.parseInt(my_cord[1]) + " " +  Integer.parseInt(my_cord[2]) + " " +  my_cord[3].charAt(0));
						// Gotta broadcast all changes after that.
						//System.out.println("ERROR 2?");
					    broadcast(clients, "ATARI " + currentPlayer.getID() + " [(" + my_cord[1] + ", " + my_cord[2] + "), " + my_cord[3] + "]");
					    viewer.refresh();
					    int winner = isWinner();
				        if(winner != 0){
						    System.out.println("Player #" + winner + " has won!");
						    // TODO tell the servers who won
						    cleanUp(clients);
							//System.exit(0);
				        }
					}else{
					    System.out.println("BAM, KICKED!");
					    board.removePlayer(currentPlayer.getID());
					    
					    if (!whosTurnIsIt.hasNext()) {
					    	// if there is no next then we are at the end and must set the iterator to the beginning
					    	turnList.remove(currentPlayer);
					    	viewer.refresh();
					    	whosTurnIsIt = turnList.listIterator(turnList.indexOf(turnList.getFirst()));
					    } else {
					    	Player nextPlayer = whosTurnIsIt.next();
					    	turnList.remove(currentPlayer);
						    whosTurnIsIt = turnList.listIterator(turnList.indexOf(nextPlayer));
					    }
					    
					    //players.add(currentPlayer.getID() -1, null);
					    broadcast(clients, "GOTE " + currentPlayer.getID());
					    int temp2 = isWinner();
					    if(temp2 != 0){
					        System.out.println("Player #" + temp2 + " has won!");
					        // TODO tell the servers who won
					        cleanUp(clients);
					        //System.exit(0);
					    }
					    
					}
					
					boardLock.release();
					//System.out.println("	boardUnlocked" + boardLock);
				}else if(msg.contains("TESUJI")) {
					// It is a pawn movement.
					semaphore.release();
					//System.out.println("    Semaphore released: " + semaphore);
					try {
					    boardLock.acquire();
					    //System.out.println("    boardLocked"+boardLock);
					    //semaphore.acquire();
					} catch(InterruptedException e){
					    System.out.println(e);
					}

					
				    if (board.isValidMove(currentPlayer.getID(), Integer.parseInt(my_cord[1]), Integer.parseInt(my_cord[2]))) {
				    	board.movePawn(currentPlayer.getID(), Integer.parseInt(my_cord[1]), Integer.parseInt(my_cord[2]));
				    	viewer.refresh();
				    	broadcast(clients, "ATARI " + currentPlayer.getID() + " (" + my_cord[1] + ", " + my_cord[2] + ") ");
				    	int winner = isWinner();
				        if(winner != 0){
						    System.out.println("Player #" + winner + " has won!");
						    // TODO tell the servers who won
						    cleanUp(clients);
							//System.exit(0);
				        }
				    } else {
				    	System.out.println("BAM, KICKED!");
				        board.removePlayer(currentPlayer.getID());
				        viewer.refresh();
				        if (!whosTurnIsIt.hasNext()) {
					    	// if there is no next then we are at the end and must set the iterator to the beginning
					    	turnList.remove(currentPlayer);
					    	whosTurnIsIt = turnList.listIterator(turnList.indexOf(turnList.getFirst()));
					    } else {
					    	Player nextPlayer = whosTurnIsIt.next();
					    	turnList.remove(currentPlayer);
						    whosTurnIsIt = turnList.listIterator(turnList.indexOf(nextPlayer));
					    }
				        broadcast(clients, "GOTE " + currentPlayer.getID());
					int winner = isWinner();
				        if(winner != 0){
						    System.out.println("Player #" + winner + " has won!");
						    // TODO tell the servers who won
						    cleanUp(clients);
							//System.exit(0);
				        }
				    }
					
				    boardLock.release();
				    //System.out.println("    boardLocked"+boardLock);
				} else {
					System.out.println("I didn't quite catch that..");
				}

            }
        }
    }
    // this is a method to print out a "prompt" for the game, it tells you how to send
    //moves and such.
    public static void usage() {
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


  /* Semapore functionality
  private boolean signal = false;

  public synchronized void take() {
    this.signal = true;
    this.notify();
  }

  public synchronized void release() throws InterruptedException{
    while(!this.signal) wait();
    this.signal = false;
  }*/
    
    
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
}

