/**
*
* @author  Andrew Valancius
* 
* @since   2016-02-26 
*/


package quoridorFE;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.VertexFactory;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.generate.GridGraphGenerator;
import org.jgrapht.Graphs;
import org.jgrapht.graph.ClassBasedVertexFactory;
import org.jgrapht.graph.SimpleGraph;


/**
* The QuoridorBoard class implements the Quoridor board and the methods needed to interact with it.
* 
* <img src="quoridorFEdiagram.png">
* 
*/
public class QuoridorBoard {
	
	UndirectedGraph<BoardNode, edgeFE> board;
	
	HashSet<Player> playerSet;
	
	private HashSet<Wall> wallSet;
	private HashSet<Wall> invalidWallSet;
	
	/**
	 * Constructor for initializing a 2 player quoridor board.
	 * 
	 * Note: Player objects passed to the constructor are assumed to be unique and 
	 * 		 class methods may not behave as expected if there are player objects with duplicate IDs on the board.
	 * 
	 * @param player1 Player object representing player 1.
	 * @param player2 Player object representing player 2.
	 */
	public QuoridorBoard(Player player1, Player player2) {
		this(player1, player2, null, null);
	}
	

	/**
	 * Constructor for initializing a 4 player quoridor board.
	 * 
	 * Note: Player objects passed to the constructor are assumed to be unique and 
	 * 		 class methods may not behave as expected if there are player objects with duplicate IDs on the board.
	 * 
	 * @param player1 Player object representing player 1.
	 * @param player2 Player object representing player 2.
	 * @param player3 Player object representing player 3.
	 * @param player4 Player object representing player 4.
	 */
	public QuoridorBoard(Player player1, Player player2, Player player3, Player player4) {
		playerSet = new HashSet<Player>();
		wallSet = new HashSet<Wall>();
		invalidWallSet = new HashSet<Wall>();
		
		// Gotta populate the board
		board = new SimpleGraph<BoardNode, edgeFE>(edgeFE.class);
		
		GridGraphGenerator<BoardNode, edgeFE> gridGen =
				new GridGraphGenerator<BoardNode, edgeFE>(9, 9);
		
		VertexFactory<BoardNode> vFactory =
	            new ClassBasedVertexFactory<BoardNode>(BoardNode.class);
		
		gridGen.generateGraph(board, vFactory, null);
		// The empty graph is now constructed
		
		int xcount = 0;
		int ycount = 0;
		for (BoardNode n : board.vertexSet()){
			//System.out.println("Node x: " + n.getxPos() + " y: " + n.getyPos());
			n.setxPos(xcount);
			n.setyPos(ycount);
			xcount++;
			if (xcount == 9) {
				xcount = 0;
				ycount++;
			}
			//System.out.println("Node x: " + n.getxPos() + " y: " + n.getyPos());
		}
		// Every node of the graph now has a position
		
		
		
		this.getNodeByCoords(4, 0).setPlayer(player1);
		this.getNodeByCoords(4, 8).setPlayer(player2);
		playerSet.add(player1);
		playerSet.add(player2);
		if (player3 != null && player4 != null) {
			this.getNodeByCoords(0, 4).setPlayer(player3);
			this.getNodeByCoords(8, 4).setPlayer(player4);
			playerSet.add(player3);
			playerSet.add(player4);
		}
		// Now the players have been placed on the board.
		
		
		// debug info
		/*
		for (BoardNode n : board.vertexSet()){
			System.out.println("Node x: " + n.getxPos() + " y: " + n.getyPos());
			for (edgeFE e : board.edgesOf(n)){
				System.out.println(e.toString());
			}
		}
		*/
	}
	

	/**
	 * Retrieves the BoardNode that corresponds to the given position on the board.
	 * @param x The x value of the position on the board
	 * @param y The y value of the position on the board
	 * @return BoardNode representing the given position on the board.
	 * 
	 * @see BoardNode
	 */
	public BoardNode getNodeByCoords(int x, int y) {
		for (BoardNode n : this.board.vertexSet()) {
			if (n.getxPos() == x && n.getyPos() == y) {
				return n;
			}
		}
		return null;
	}
	
	private BoardNode getNodeByCoords(int x, int y, SimpleGraph<BoardNode, edgeFE> board) {
		for (BoardNode n : board.vertexSet()) {
			if (n.getxPos() == x && n.getyPos() == y) {
				return n;
			}
		}
		return null;
	}

	/**
	 * Retrieves the BoardNode corresponding to the space that the given player is occupying.
	 * @param player Number of the player whos location you're looking for.
	 * @return BoardNode representing the location of the given player.
	 */
	public synchronized BoardNode getNodeByPlayerNumber(int player) {
		for (BoardNode n : this.board.vertexSet()) {
			if (n.getPlayer() != null) {
				if (player == n.getPlayer().getID()) {
					return n;
				}
			}
		}
		return null;
	}
	
	private BoardNode getNodeByPlayerNumber(int player, SimpleGraph<BoardNode, edgeFE> board) {
		for (BoardNode n : board.vertexSet()) {
			if (n.getPlayer() != null) {
				if (player == n.getPlayer().getID()) {
					return n;
				}
			}
		}
		return null;
	}
	
	
	/**
	 * Allows access to the set of players on the board
	 * 
	 * @return A HashSet containing all the walls that have been placed on the board.
	 * @see Wall
	 */
	public HashSet<Player> getPlayerSet() {
		return this.playerSet;
	}
	
	/**
	 * Allows access to the set of walls that have been placed so far.
	 * 
	 * @return A HashSet containing all the walls that have been placed on the board.
	 * @see Wall
	 */
	public synchronized HashSet<Wall> getWallSet() {
		return this.wallSet;
	}
	
	
		/**
	 * Allows access to the set of invalid walls
	 * 
	 * @return A HashSet containing all the invalid walls of the given board
	 * @see Wall
	 */
	public synchronized HashSet<Wall> getInvalidWallSet() {
		return this.invalidWallSet;
	}

	/**
	 * Checks to see if the proposed pawn move is a valid move.
	 * 
	 * @param player Number of the player attempting the move.
	 * @param x The destination x position.
	 * @param y The destination y position.
	 * @return False for an invalid move, True for a valid one.
	 */
	public synchronized boolean isValidMove(int player, int x, int y) {
		
		if (x > 8 || y > 8) return false; // Move is out of bounds.
		
		BoardNode source = this.getNodeByPlayerNumber(player);
		BoardNode target = this.getNodeByCoords(x, y);
		
		if (source.getxPos() == target.getxPos() && source.getyPos() == target.getyPos()) return false; 		// source and destination are the same
		if (source.getPlayer() == null) return false; 	// there is no player at this location.
		if (target.getPlayer() != null) return false; 	// you can't move your pawn to the same place as an opponents
		
		if (source.getPlayer().getID() != player) {
			// player attempting to move a pawn that does not belong to him
			return false;
		}
		DijkstraShortestPath<BoardNode, edgeFE> path =
				new DijkstraShortestPath<BoardNode, edgeFE>(this.board, source, target);
		List<edgeFE> edgeList = path.getPathEdgeList();
		Set<BoardNode> nodesOnThePath = new HashSet<BoardNode>();
		
		if (edgeList.size() > 1) {
			for (edgeFE e : edgeList) {
				nodesOnThePath.add(e.getSource());
				nodesOnThePath.add(e.getTarget());
			}
			for (BoardNode n : nodesOnThePath) {
				// if there is an empty node on this path it better be the target position
				if (n.getPlayer() == null && n != target) return false;
			}	
		}
		// ELSE return true
		return true;
	}
	
	/**
	 * Checks to see if the proposed wall placement is a valid move.
	 * 
	 * @param player Number of the player attempting the move.
	 * @param x The x position of the wall
	 * @param y The y position of the wall
	 * @param orientation The orientation of the wall. 
	 * @return False for an invalid move, True for a valid one.
	 */
	public synchronized boolean isValidMove(int player, int x, int y, char orientation) {
		// check for out of bounds
		if (x > 7 || y > 7) return false;
		
		// check for invalid orientations
		if (orientation != 'v' && orientation != 'h') return false;
		
		// check number of walls left
		if (this.getNodeByPlayerNumber(player).getPlayer().wallsLeft() == 0) return false;
		
		// check against repeat wall placements
		Wall testy = new Wall(x, y, orientation);
		if (this.getWallSet().contains(testy)) return false;
		
		// check wall against list of invalid walls
		if (this.invalidWallSet.contains(testy)) return false;
		
		// check for path to win condition
		SimpleGraph<BoardNode, edgeFE> boardCopy = new SimpleGraph<BoardNode, edgeFE>(edgeFE.class);
		Graphs.addGraph(boardCopy, this.board);
		
		if (orientation == 'v') {
			BoardNode firstSource = this.getNodeByCoords(x, y, boardCopy);
			BoardNode firstTarget = this.getNodeByCoords(x+1, y, boardCopy);
			BoardNode secondSource = this.getNodeByCoords(x, y+1, boardCopy);
			BoardNode secondTarget = this.getNodeByCoords(x+1, y+1, boardCopy);
			boardCopy.removeEdge(firstSource, firstTarget);
			boardCopy.removeEdge(secondSource, secondTarget);
		} else {
			BoardNode firstSource = this.getNodeByCoords(x, y, boardCopy);
			BoardNode firstTarget = this.getNodeByCoords(x, y+1, boardCopy);
			BoardNode secondSource = this.getNodeByCoords(x+1, y, boardCopy);
			BoardNode secondTarget = this.getNodeByCoords(x+1, y+1, boardCopy);
			boardCopy.removeEdge(firstSource, firstTarget);
			boardCopy.removeEdge(secondSource, secondTarget);
		}
		
		for(Player p : this.playerSet) {
			boolean pathExists = false;
			for (int i = 0; i < 9; i++) {
				if (p.getID() == 1) {	
					if (DijkstraShortestPath.findPathBetween(boardCopy, this.getNodeByPlayerNumber(p.getID(), boardCopy), this.getNodeByCoords(i, 8, boardCopy)) != null) {
						// if there is no path, check next node
						pathExists = true;
						break;
					}
				} else if (p.getID() == 2) {
					if (DijkstraShortestPath.findPathBetween(boardCopy, this.getNodeByPlayerNumber(p.getID(), boardCopy), this.getNodeByCoords(i, 0, boardCopy)) != null) {
						// if there is no path, check next node
						pathExists = true;
						break;
					}
				} else if (p.getID() == 3) {
					if (DijkstraShortestPath.findPathBetween(boardCopy, this.getNodeByPlayerNumber(p.getID(), boardCopy), this.getNodeByCoords(8, i, boardCopy)) != null) {
						// if there is no path, check next node
						pathExists = true;
						break;
					}
				} else if (p.getID() == 4) {
					if (DijkstraShortestPath.findPathBetween(boardCopy, this.getNodeByPlayerNumber(p.getID(), boardCopy), this.getNodeByCoords(0, i, boardCopy)) != null) {
						// if there is no path, check next node
						pathExists = true;
						break;
					}
				}
			}
			
			if (!pathExists) return false;
		}
		// if you made it here, then it must be a valid move
		return true;
	}
	
	/**
	 * Place a wall on the board
	 * 
	 * @param player Number of the player making the move.
	 * @param x The x position of the wall
	 * @param y The y position of the wall
	 * @param orientation The orientation of the wall. 
	 */
	public synchronized void placeWall(int player, int x, int y, char orientation) {
		if (this.isValidMove(player, x, y, orientation) == false) throw new IllegalMoveException("You fucked up, scrub. You tried to place an invalid wall at [("+ x + ", " + y + ") " + orientation + ")]");
		
		Player p = this.getNodeByPlayerNumber(player).getPlayer();
		
		if (orientation == 'v') {
			BoardNode firstSource = this.getNodeByCoords(x, y);
			BoardNode firstTarget = this.getNodeByCoords(x+1, y);
			BoardNode secondSource = this.getNodeByCoords(x, y+1);
			BoardNode secondTarget = this.getNodeByCoords(x+1, y+1);
			this.board.removeEdge(firstSource, firstTarget);
			this.board.removeEdge(secondSource, secondTarget);
		} else {
			BoardNode firstSource = this.getNodeByCoords(x, y);
			BoardNode firstTarget = this.getNodeByCoords(x, y+1);
			BoardNode secondSource = this.getNodeByCoords(x+1, y);
			BoardNode secondTarget = this.getNodeByCoords(x+1, y+1);
			this.board.removeEdge(firstSource, firstTarget);
			this.board.removeEdge(secondSource, secondTarget);
		}
		p.decrementWalls();
		
		Wall placedWall = new Wall(player, x, y, orientation);
		wallSet.add(placedWall);
		generateInvalidWalls(placedWall);
	}
	
	public void placeWallUnchecked(int player, int x, int y, char orientation) {
		Player p = this.getNodeByPlayerNumber(player).getPlayer();
		
		if (orientation == 'v') {
			BoardNode firstSource = this.getNodeByCoords(x, y);
			BoardNode firstTarget = this.getNodeByCoords(x+1, y);
			BoardNode secondSource = this.getNodeByCoords(x, y+1);
			BoardNode secondTarget = this.getNodeByCoords(x+1, y+1);
			this.board.removeEdge(firstSource, firstTarget);
			this.board.removeEdge(secondSource, secondTarget);
		} else {
			BoardNode firstSource = this.getNodeByCoords(x, y);
			BoardNode firstTarget = this.getNodeByCoords(x, y+1);
			BoardNode secondSource = this.getNodeByCoords(x+1, y);
			BoardNode secondTarget = this.getNodeByCoords(x+1, y+1);
			this.board.removeEdge(firstSource, firstTarget);
			this.board.removeEdge(secondSource, secondTarget);
		}
		p.decrementWalls();
		
		Wall placedWall = new Wall(x, y, orientation);
		wallSet.add(placedWall);
		generateInvalidWalls(placedWall);
	}
	
	/**
	 * Generates all the wall placements invalidated by a given wall placement and 
	 * adds them to a set of invalid walls.
	 * 
	 * @param placedWall
	 */
	private synchronized void generateInvalidWalls(Wall placedWall) {
		if (placedWall.orientation == 'h') {
			invalidWallSet.add(new Wall(placedWall.x, placedWall.y, 'v')); 
			if (placedWall.x > 0) invalidWallSet.add(new Wall(placedWall.x - 1, placedWall.y, 'h'));
			if (placedWall.x < 7) invalidWallSet.add(new Wall(placedWall.x + 1, placedWall.y, 'h'));
		} else {
			invalidWallSet.add(new Wall(placedWall.x, placedWall.y, 'h'));
			if (placedWall.y > 0) invalidWallSet.add(new Wall(placedWall.x, placedWall.y - 1, 'v'));
			if (placedWall.y < 7) invalidWallSet.add(new Wall(placedWall.x, placedWall.y + 1, 'v'));
		}
	}
	
	/**
	 * Moves the specified players pawn to the given position on the board.
	 * 
	 * @param player Number of the player making the move.
	 * @param x The x position destination of the pawn.  
	 * @param y The y position of the wall
	 */
	public synchronized void movePawn(int player, int x, int y) {
		if (this.isValidMove(player, x, y) == false) {
			throw new IllegalMoveException("You fucked up, scrub.You tried to move a pawn to ("+ x + ", " + y + ")");
		} else {
			movePawnUnchecked(player, x, y);
		}
	}
	
	public synchronized void movePawnUnchecked(int player, int x, int y) {
		BoardNode currentLocation = this.getNodeByPlayerNumber(player);
		BoardNode targetLocation = this.getNodeByCoords(x, y);
		Player p = currentLocation.getPlayer();
		
		targetLocation.setPlayer(p);
		currentLocation.setPlayer(null);
	}

	public synchronized void removePlayer(int playerId) {
		this.getNodeByPlayerNumber(playerId).setPlayer(null); // Remove player from space
		
		Player kicked = null;
		for (Player p : this.playerSet) {
			if (p.getID() == playerId) {
				kicked = p;
			}
		}
		if (kicked != null && this.playerSet.contains(kicked)) {
			this.playerSet.remove(kicked); // Remove player from playerSet
		}
	}
	
	// TO DO: Add this method to the javadocs
	
	// Method to return a copy of a given board
	public QuoridorBoard clone() {
	
	    // Generate a blank board
	    QuoridorBoard clonedBoard = new QuoridorBoard(null, null);
	    // Add fields of this board to clonedBoard
	    clonedBoard.board = this.board;
	    clonedBoard.playerSet = this.playerSet;
	    clonedBoard.wallSet = this.wallSet;
	    clonedBoard.invalidWallSet = this.invalidWallSet;
	    return clonedBoard;
	}
	
	// Removes a wall with the given parameters
	public void removeWall(Player player, int x, int y, char orientation) {
	    Wall w = new Wall(x, y, orientation);
	    //if Board doesn't contain wall, leave
	    if (!this.wallSet.contains(w))
		return;
		
	    if (orientation == 'h'){
		BoardNode firstSource = this.getNodeByCoords(x, y);
		BoardNode firstTarget = this.getNodeByCoords(x, y+1);
		BoardNode secondSource = this.getNodeByCoords(x+1, y);
		BoardNode secondTarget = this.getNodeByCoords(x+1, y+1);
		this.board.addEdge(firstSource, firstTarget);
		this.board.addEdge(secondSource, secondTarget);
	    }
	    else{
		BoardNode firstSource = this.getNodeByCoords(x, y);
		BoardNode firstTarget = this.getNodeByCoords(x+1, y);
		BoardNode secondSource = this.getNodeByCoords(x, y+1);
		BoardNode secondTarget = this.getNodeByCoords(x+1, y+1);
		this.board.addEdge(firstSource, firstTarget);
		this.board.addEdge(secondSource, secondTarget);
	    }
	   //player.incrementWalls();
	   Wall removedWall = new Wall(player.getID(), x, y, orientation);
	   wallSet.remove(removedWall);
	    
	    /* Updating invalidWallSet
	    if (removedWall.orientation == 'h') {
		invalidWallSet.remove(new Wall(removedWall.x, removedWall.y, 'v')); 
		if (removedWall.x > 0) invalidWallSet.remove(new Wall(removedWall.x - 1, removedWall.y, 'h'));			
		if (removedWall.x < 7) invalidWallSet.remove(new Wall(removedWall.x + 1, removedWall.y, 'h'));
	} else {
		invalidWallSet.add(new Wall(removedWall.x, removedWall.y, 'h'));
		if (removedWall.y > 0) invalidWallSet.remove(new Wall(removedWall.x, removedWall.y - 1, 'v'));
		if (removedWall.y < 7) invalidWallSet.remove(new Wall(removedWall.x, removedWall.y + 1, 'v'));
		}	*/    
	}
	
	// Can be called with either a wall or the x, y and orientation
	public void removeWall(Player p, Wall w){
	    removeWall(p, w.x, w.y, w.orientation);
	}
	
	
	public void placeTestWall(int x, int y, char orientation) {
	    	if (orientation == 'v') {
			BoardNode firstSource = this.getNodeByCoords(x, y);
			BoardNode firstTarget = this.getNodeByCoords(x+1, y);
			BoardNode secondSource = this.getNodeByCoords(x, y+1);
			BoardNode secondTarget = this.getNodeByCoords(x+1, y+1);
			this.board.removeEdge(firstSource, firstTarget);
			this.board.removeEdge(secondSource, secondTarget);
		} else {
			BoardNode firstSource = this.getNodeByCoords(x, y);
			BoardNode firstTarget = this.getNodeByCoords(x, y+1);
			BoardNode secondSource = this.getNodeByCoords(x+1, y);
			BoardNode secondTarget = this.getNodeByCoords(x+1, y+1);
			this.board.removeEdge(firstSource, firstTarget);
			this.board.removeEdge(secondSource, secondTarget);
		}
		Wall placedWall = new Wall(1, x, y, orientation);
		wallSet.add(placedWall);
	}
	
}









