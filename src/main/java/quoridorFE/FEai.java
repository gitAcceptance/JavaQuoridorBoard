package quoridorFE;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Random;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.DijkstraShortestPath;

public class FEai {


        public static Random ran = new Random();
	public static int weight = 10;
        //public static int seed = ran.nextInt(1000);

	// Some global variables.
	public static String move[] = {
	    //"[(4, 4), v]",
	    "[(4, 6), v]",
	    "[(4, 8), h]",
	    "[(6, 8), v]",
        "[(2, 4), v]",
        "[(2, 2), v]",
        "[(7, 4), h]",
        "[(7, 8), v]"

	};
	public static int counter = -1;


	public FEai() {
	    //System.out.println("	This AI's seed value:" + seed);
	    // TODO Auto-generated constructor stub
	}

	public static String getRecordedMove(){
	    if(counter >= move.length-1){
		System.out.println("	Reseting count!");
		counter = 0;
	    }else{
	    	counter++;
	    }
	    return move[counter];
	}

	
	public static String getMoveShortestPath(int player, QuoridorBoard qboard) {
		
		UndirectedGraph<BoardNode, edgeFE> board = qboard.board;
		BoardNode source = qboard.getNodeByPlayerNumber(player);
		ArrayList<BoardNode> winningNodeList = generateWinningNodeList(player, qboard);
		ArrayList<DijkstraShortestPath<BoardNode, edgeFE>> pathList = new ArrayList<DijkstraShortestPath<BoardNode, edgeFE>>(); 
		
		// Getting
		for (BoardNode n : winningNodeList) {
			pathList.add(new DijkstraShortestPath<BoardNode, edgeFE>(board, source, n));
		}
		DijkstraShortestPath<BoardNode, edgeFE> shortestPath = null;
		for (DijkstraShortestPath<BoardNode, edgeFE> p : pathList) {
			if (shortestPath == null) {
				shortestPath = p;
			}
			if (shortestPath.getPathLength() > p.getPathLength()) {
				shortestPath = p;
			}
		}
		// shortestPath now points to the shortest path.
		
		List<edgeFE> edgeList = shortestPath.getPathEdgeList();
		Set<BoardNode> nodesOnThePath = new HashSet<BoardNode>();
		
		for (edgeFE e : edgeList) {
			nodesOnThePath.add(e.getSource());
			nodesOnThePath.add(e.getTarget());
		}
		
		// Now I've gotta get the first node on the path that isn't my current pawn position
		// and that isn't occupied by another pawn
		BoardNode attempt = null;
		
		for (BoardNode b : nodesOnThePath) {
			
			System.out.println("attempt: " + b.toString());
			if (qboard.isValidMove(player, b.getxPos(), b.getyPos())) {
				// we found a good move
				attempt = b;
				break;
			}
		}
		
		String retStr = "("+ attempt.getxPos() + ", "+ attempt.getyPos() +")";
		
		// Currently, prints out all the path lengths from player to all other nodes on the board
		/*ArrayList<int[]> pathChart = getPaths
		    (qboard.getNodeByPlayerNumber(player), qboard);*/
		System.out.println("	reststr:" + retStr);
		return retStr;
	}
	
	

	/**
	Just lets you quickly know if your move if an illegal one with a boolean return value.
	**/
	public static boolean isValid(int player, QuoridorBoard qboard, String attempt){

		// Gotta check if it's a wall or a move.
		boolean verdict = false;
		int x, y;
		char or;
		if(attempt.contains("v") || attempt.contains("h")){
		    //Then it is a wall.
		    x = Integer.parseInt("" + attempt.charAt(2));
		    y = Integer.parseInt("" + attempt.charAt(5));
		    or = attempt.charAt(9);
            	    if (qboard.isValidMove(player, x, y, or)) {
                	// we found a good move
            		return true;
            	    }
		}else{
		    // It is a pawn move.
		    x = Integer.parseInt(""+attempt.charAt(1));
	     	    y = Integer.parseInt(""+attempt.charAt(4));
            	    if (qboard.isValidMove(player, x, y) ) {
                	// we found a good move
                	return true;
            	    }
		}
	        // Hardcoded checking.
		if(x > 7  || y > 7){
			verdict = false;
		}
		return verdict;
	}
	
	
	// Copying team morty's AI concept, good against four players?
	public static String getMove2(int player, QuoridorBoard qboard){
	    //iterate through other players.
	    int shortestPlayer = 0;
	    String move;// = getMoveShortestPath(player, qboard);
	    for(int i=0; i<qboard.getPlayerSet().size()+1; i++){
		//if their length is shorter than ours, block them.
  	        if(shortestPathToWin(i, qboard) <= shortestPathToWin(player, qboard) && i != player){
		    //move = blockPlayer(i, qboard);
		    shortestPlayer = i;
	        }
	    }
	    if(shortestPlayer != 0){
		move = blockPlayer(shortestPlayer, qboard);
		if(isValid(player, qboard, move)){
		    return move;
		}
	    }
	    return getMoveShortestPath(player, qboard);
	}//*/

	
	/**
	The actual AI move method. Good against two players.
	**/
	public static String getMove(int player, QuoridorBoard qboard){
	    //ran = new Random(seed);
	    //int weight = 10;
	    // SMART - If we are going to win, just move to win.
	    if(shortestPathToWin(player, qboard) <= 1 && isValid(player, qboard, getMoveShortestPath(player, qboard))){
		return getMoveShortestPath(player, qboard);
	    }
	    // SMART - If we can place a wall that will let us walk to victory lets place it!
	    //String tunnel = tunnelWall(player, qboard);
	    /*if(tunnel != ""){
		System.out.println("Tunnel:"+tunnel);
		if(isValid(player, qboard, tunnel)){
		    System.out.println("Time to Tunnel to Victory, for the emporeur!");
		    // Make it so we will only do shortest path from then on.
		    weight = 9001;
		    return tunnel;
		}
	    }*/
	    int r = ran.nextInt(100);
	    boolean keepgoing = true;
	    String output = ""; //= getMoveShortestPath(player, qboard);
	    int count = 0;
	    while(keepgoing){
	        // Lets select a move based on that number. Later we will have a weighted system generated based on board state.
		//System.out.println(""+r);
		if(count > 5){
			output = getMoveShortestPath(player, qboard);
			keepgoing = false;
			System.out.println("	I've had it with these god damned snakes on this god damned plane.");
		}else{
			count++;
			output = getMoveShortestPath(player, qboard);
			if(r > (weight * (shortestPathToWin(player, qboard) + 1))){
			   output = blockClosestOpponent(player, qboard);   
			}
			// Check if it's valid, if it isn't we will contiue to search for the next legal move we can make.
			String msg = "";
        	        msg = output.replace(',', ' ');
        	    	msg = msg.replace('(', ' ');
        	    	msg = msg.replace(')', ' ');
        	    	msg = msg.replace('[', ' ');
        	   	msg = msg.replace(']', ' ');
			String[] my_cord = msg.split("\\s+");
			System.out.println("	Handing to isValid" + msg);

			// Checking if it is illegal.
			if(output.toLowerCase().contains("v") || output.toLowerCase().contains("h") && qboard.isValidMove(player, Integer.parseInt(my_cord[1]), Integer.parseInt(my_cord[2]), my_cord[3].charAt(0))){
			    keepgoing = false;
			    System.out.println("        LEGAL MOVE:" + output);
			    //return output;
			}else if(!(output.toLowerCase().contains("v") || output.toLowerCase().contains("h"))){
			    if(qboard.isValidMove(player, Integer.parseInt(my_cord[1]), Integer.parseInt(my_cord[2]))){
			    	keepgoing = false;
                       	    	System.out.println("        LEGAL MOVE:" + output);
			    	//return output;
			    }
			}else{
			    r = ran.nextInt(10);
			    System.out.println("	ILLEGAL MOVE:" + output + "	NUM:" + r);			  
			}
    		}
	    }
	    return output;
	}
	
	
	
	private static ArrayList<BoardNode> generateWinningNodeList(int player, QuoridorBoard qboard) {
		ArrayList<BoardNode> list = new ArrayList<BoardNode>();
		if (player == 1) {
			for (int i = 0; i < 9; i++) {
				list.add(qboard.getNodeByCoords(i, 8));
			}
		} else if (player == 2) {
			for (int i = 0; i < 9; i++) {
				list.add(qboard.getNodeByCoords(i, 0));
			}
		} else if (player == 3) {
			for (int i = 0; i < 9; i++) {
				list.add(qboard.getNodeByCoords(8, i));
			}
		} else if (player == 4) {
			for (int i = 0; i < 9; i++) {
				list.add(qboard.getNodeByCoords(0, i));
			}
		}
		return list;
	}
	
	
	
	// Method getPaths()
	// Param: Boardnode player: The position of a player
	// 	  QuoridorBoard qboard: Current iteration of the board
	// Returns: ArrayList<int[]> rows:
	//	List of lists of each row, filled with the distance from player to each node in that row.
	public static ArrayList<int[]> getPaths(BoardNode player, QuoridorBoard qboard) {
	    System.out.println("Shortest Path from player to all other nodes on the board:"
		+	"Player is at position 0");
	    ArrayList<int[]> rows = new ArrayList<int[]>(9);		//initializing rows
	    for(int i = 0; i < 9; i++){
		int[] row = new int[9];
		for(int j = 0; j < 9; j++){
		    // getting the path from player to node at (i, j)
		    UndirectedGraph<BoardNode, edgeFE> board = qboard.board;
		    row[j] = (int)(new DijkstraShortestPath<BoardNode, edgeFE>
		    (board, player, qboard.getNodeByCoords(i, j)).getPathLength());
		    System.out.print(row[j] + " ");
		}
		rows.add(i, row);	// Adding each row to rows<>
		System.out.println("");
	    }
	    return rows;
	}
	
	
	
	// Method blockClosestOpponent, takes a player integer, which is the current player and the current game board.
	public static String blockClosestOpponent(int player, QuoridorBoard qboard) {

	    //UndirectedGraph<BoardNode, edgeFE> board = qboard.board;
	    // shortest guys player number paired with his shortest path to win.
	    int[] playerPair = new int[]{1, 1000};

	    // Iterate through all players
	    // FIXME this could be a for each loop
	    for(int i=1; i<qboard.getPlayerSet().size()+1; i++){
			// If it is us or the player has been kicked.
			if(i == player || qboard.getNodeByPlayerNumber(i) == null) {
			    //System.out.println(qboard.getNodeByPlayerNumber(i));
			} else {
			    // Check for shortest path.
			    int temp = shortestPathToWin(i, qboard);
   	            	    if(temp < playerPair[1]){
				System.out.println("	New shortest is:" + i + "!");
				playerPair[0] = i;
				playerPair[1] = temp;
   	            	    }
		        }
	    }
	    // Now that we have the closest player lets return a blocking wall on him.
	    
	    String temp = blockPlayer(playerPair[0], qboard);
	    System.out.println("temp"+temp);
	    return temp;
	}
	
	
	
	//Param: current player's number, current board state
	//Returns: string of wall to block that player's shortest path.
	// Will be modified to smartly increase their shortest path the most.
        public static String blockPlayer(int player, QuoridorBoard qboard){
	    System.out.println("	BLOCLKING:" + player);
	    // Now we have the player who is quickest to winning and we need to find is next step on shortest path and block it with a wall.
	    String thisMove = "" + getMoveShortestPath(player, qboard);

	    // parse out everything that is not a number into a nice array
	    String hisMove = thisMove;
	    hisMove = hisMove.replace(',', ' ');
        hisMove = hisMove.replace('(', ' ');
        hisMove = hisMove.replace(')', ' ');
        hisMove = hisMove.replace('[', ' ');
        hisMove = hisMove.replace(']', ' ');
	    String[] holder = hisMove.trim().split("\\s++");

	    // Get his current space
        int curX = qboard.getNodeByPlayerNumber(player).getxPos();
        int curY = qboard.getNodeByPlayerNumber(player).getyPos();

	    // figure out what direction he is moving.
        int dirX = curX - Integer.parseInt(holder[0]);
	    int dirY = curY - Integer.parseInt(holder[1]);

	    /* Gotta build an offset based on direction.
	    if(Integer.parseInt(holder[0]) == -1){
		holder[0] = ""+(Integer.parseInt(holder[0]) - 1);
	    }else if(Integer.parseInt(holder[1]) == -1){
                holder[1] = ""+(Integer.parseInt(holder[1]) - 1);
            }

	     Do orientation based on their direction they will move.
	    if(Math.abs(dirX) == 1 && isValid(player, qboard, ("["+hisMove+", v]") )){
		return "[" + hisMove + ", v]";
	    }else{
  	        return "[" + thisMove + ", h]";
	    }*/

	    // Now that we know his direction of movement lets try blocking it.
	    if(dirX == -1){
	    	return "[(" + (Integer.parseInt(holder[0]) - 1) + ", " + (Integer.parseInt(holder[1])) + "), v]";
	    }else if(dirX == 1){
	    	return "[(" + (Integer.parseInt(holder[0])) + ", " + (Integer.parseInt(holder[1])) +"), v]";
	    }else if(dirY == -1){
            return "[(" + (Integer.parseInt(holder[0])) + ", " + (Integer.parseInt(holder[1]) + 1) + "), h]";
	    }else if(dirY == 1){
            return "[(" + (Integer.parseInt(holder[0])) + ", " + (Integer.parseInt(holder[1])) +"), h]";
	    }else{
	        return "[" + thisMove + ", h]";
	    }
	}


	// Param: player p, and an instance of the board
	// Returns: shortest path to a winning node
	private static int shortestPathToWin(int player, QuoridorBoard qboard){

	    ArrayList<BoardNode> winningNodes = generateWinningNodeList(player, qboard);
	    UndirectedGraph<BoardNode, edgeFE> board = qboard.board;
	    int shortest = 10000000;
	    for(int i = 0; i< winningNodes.size(); i++) {
		int pathLength = (int)(new DijkstraShortestPath<BoardNode, edgeFE>
		    (board, qboard.getNodeByPlayerNumber(player), winningNodes.get(i)).getPathLength() );
		if(pathLength < shortest)
		    shortest = pathLength;
	    }
	    return shortest;
	}
	
	// Returns a wall that would create a tunnel to win for the given player
	// otherwise returns null
	public static String tunnelWall(int player, QuoridorBoard board){
	
	    String eli = "";
	
	    HashSet<Wall> invalidWalls = board.getInvalidWallSet();
	    HashSet<Wall> originalWalls = board.getWallSet();
	    for(Wall e: originalWalls)
		eli += e.x + " " + e.y + " " + e.orientation + "\n";	   
	    Player p = board.getNodeByPlayerNumber(player).getPlayer();

	    String testString = "";
	    Wall test;
	    // Loop through every possible wall placement
	    for(int i = 0; i < 8; i++){
		for(int j = 0; j < 8; j++){
		
		    // Testing the horizontal orientation of the current wall
		    test = new Wall(player, i, j, 'h');
		    
		    // if test is a valid wall & it's not an original wall
		    if(!invalidWalls.contains(test) && !originalWalls.contains(test)){
			//board.placeWallUnchecked(player, i, j, 'h');		// place the wall
			board.placeTestWall(i, j, 'h');
			
			// If we've reduced the number of winning nodes to one
			if(availableWinningNodes(board.getNodeByPlayerNumber(player), board).size() == 1){
			    board.removeWall(p, test);	// remove the wall
			    // return string representation of the wall
			    return "[(" + i + ", " + j + "), " + 'h' + "]";
			}
			    board.removeWall(p, test);// remove the wall
		    }
		    
		    // Testing the vertical orientation of the current wall
		    test = new Wall(player, i, j, 'v');
		    // if test is a valid wall and it's not an original wall
		    if(!invalidWalls.contains(test) && !originalWalls.contains(test)){
			//board.placeWallUnchecked(player, i, j, 'v');	// place the wall
			board.placeTestWall(i, j, 'v');
			
			// If we've reduced the number of winning nodes to one
			if(availableWinningNodes(board.getNodeByPlayerNumber(player), board).size() == 1){
			    board.removeWall(p, test);				// remove the wall
			    // return string representation of the wall
			    return "[(" + i + ", " + j + "), " + 'v' + "]";
			}
			    board.removeWall(p, test);					// remove the wall
		    }
		}// end of j loop
	    }// end of i loop 
	    return eli;
	}
	
	// Returns a list of the winning nodes that
	public static HashSet<BoardNode> availableWinningNodes(BoardNode player, QuoridorBoard qboard) {
	
	    ArrayList<BoardNode> winningNodes = generateWinningNodeList(player.getPlayer().getID(), qboard);
	    HashSet<BoardNode> availableWinningNodes = new HashSet<BoardNode>();

	    for(BoardNode e: winningNodes){
		UndirectedGraph<BoardNode, edgeFE> board = qboard.board;
		if( (new DijkstraShortestPath<BoardNode, edgeFE>
		(board, player, e).getPathLength()) != Double.POSITIVE_INFINITY)
		    availableWinningNodes.add(e);
	    }
	    return availableWinningNodes;
	}
	
	
}









