package quoridorFE;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.alg.DijkstraShortestPath;
import org.junit.Test;
//import org.junit.Ignore;

public class TestQuoridorBoard {

	@Test
	public void testQuoridorBoard2Player() {
		QuoridorBoard testBoard = new QuoridorBoard(new Player(1, "test1", 6666, 10, 4, 0), new Player(2, "test2", 6667, 10, 4, 8));
		int player1Xpos = 4;
		int player1Ypos = 0;
		int player2Xpos = 4;
		int player2Ypos = 8;
		
		BoardNode n1 = testBoard.getNodeByCoords(player1Xpos, player1Ypos);
		BoardNode n2 = testBoard.getNodeByCoords(player2Xpos, player2Ypos);
		
		assertEquals("Player 1 is not where it should be.", n1.getPlayer().getID(), 1);
		assertEquals("Player 2 is not where it should be.", n2.getPlayer().getID(), 2);
	}

	@Test
	public void testQuoridorBoard4Player() {
		QuoridorBoard testBoard = new QuoridorBoard(new Player(1, "test1", 6666, 5, 4, 0), new Player(2, "test2", 6667, 5, 4, 8),
													new Player(3, "test3", 6668, 5, 0, 4), new Player(4, "test4", 6669, 5, 8, 4));
		
		BoardNode n1 = testBoard.getNodeByCoords(4, 0);
		BoardNode n2 = testBoard.getNodeByCoords(4, 8);
		BoardNode n3 = testBoard.getNodeByCoords(0, 4);
		BoardNode n4 = testBoard.getNodeByCoords(8, 4);
		
		assertEquals("Player 1 is not where it should be.", n1.getPlayer().getID(), 1);
		assertEquals("Player 2 is not where it should be.", n2.getPlayer().getID(), 2);
		assertEquals("Player 3 is not where it should be.", n3.getPlayer().getID(), 3);
		assertEquals("Player 4 is not where it should be.", n4.getPlayer().getID(), 4);
	}

	@Test
	public void testGetNodeByCoords() {
		QuoridorBoard testBoard = new QuoridorBoard(new Player(1, "test1", 6666, 10, 4, 0), new Player(2, "test2", 6667, 10, 4, 8));
		BoardNode node = testBoard.getNodeByCoords(2, 0);
		
		assertEquals("X value was not as expected", 2, node.getxPos());
		assertEquals("Y value was not as expected", 0, node.getyPos());
	}

	@Test
	public void testGetPlayerPositionInt() {
		QuoridorBoard testBoard = new QuoridorBoard(new Player(1, "test1", 6666, 10, 4, 0), new Player(2, "test2", 6667, 10, 4, 8));
		BoardNode node2 = testBoard.getNodeByPlayerNumber(2);
		
		assertEquals("X value was not as expected.", 4, node2.getxPos());
		assertEquals("Y value was not as expected.", 8, node2.getyPos());
	}

	@Test
	public void testIsValidMovePawn() {
		QuoridorBoard testBoard = new QuoridorBoard(new Player(1, "test1", 6666, 10, 4, 0), new Player(2, "test2", 6667, 10, 4, 8));
		assertTrue("Expected isValidMove() to return true.", testBoard.isValidMove(1, 3, 0));
		assertTrue("Expected isValidMove() to return true.", testBoard.isValidMove(1, 4, 1));
		assertTrue("Expected isValidMove() to return true.", testBoard.isValidMove(1, 5, 0));
		
		assertFalse("trying to move to where i already am.", testBoard.isValidMove(1, 4, 0));
		
		testBoard.placeWall(1, 4, 0, 'h'); // Put a wall in the way then try to move over it.
		assertFalse("Expected isValidMove() to return false.", testBoard.isValidMove(1, 4, 1));
		
		testBoard.placeWall(1, 4, 7, 'h'); // Put a wall in the way then try to move over it.
		assertFalse("Expected isValidMove() to return false.", testBoard.isValidMove(2, 4, 7));
		
		// Trying a bunch of invalid moves.
		assertFalse("Expected isValidMove() to return false.", testBoard.isValidMove(1, 4, 4));
		assertFalse("Expected isValidMove() to return false.", testBoard.isValidMove(1, 6, 0));
		assertFalse("Expected isValidMove() to return false.", testBoard.isValidMove(2, 4, 4));
		assertFalse("Expected isValidMove() to return false.", testBoard.isValidMove(2, 8, 6));
		
	}

	@Test
	public void testIsValidMoveWall() {
		QuoridorBoard testBoard = new QuoridorBoard(new Player(1, "test1", 6666, 5, 4, 0), new Player(2, "test2", 6667, 5, 4, 8),
													new Player(3, "test3", 6668, 5, 0, 4), new Player(4, "test4", 6669, 5, 8, 4));
		
		// test good placement case
		assertTrue("isValidMove() should return true for a good move. 1", testBoard.isValidMove(4, 0, 3, 'v'));
		
		// test blocking in player 1
		testBoard.placeWall(2, 3, 0, 'v');
		testBoard.placeWall(2, 4, 0, 'v');
		assertFalse("isValidMove() should return false when blocking all paths to victory.", testBoard.isValidMove(2, 4, 1, 'h'));
		
		// test blocking in player 3
		testBoard.placeWall(1, 0, 3, 'h');
		testBoard.placeWall(1, 0, 4, 'h');
		testBoard.placeWall(1, 0, 5, 'h');
		assertFalse("isValidMove() should return false when blocking all paths to victory.", testBoard.isValidMove(4, 1, 4, 'v'));
		
		// test using up all the wall placements
		testBoard.placeWall(1, 0, 1, 'h');
		testBoard.placeWall(1, 0, 7, 'h');
		//testBoard.placeWall(1, 6, 5, 'h'); // fifth used wall, next attempt by player 1 to place wall should end badly
		assertFalse("isValid() should return false when a player tries to place an 6th wall.", testBoard.isValidMove(1, 3, 4, 'v'));
		
		assertFalse("isValid() should return false when a player tries to place a wall across another wall.", testBoard.isValidMove(3, 0, 7, 'v'));
		
		// test out of bounds wall placements
		assertFalse("isValid() should return false for any out of bounds wall placement.", testBoard.isValidMove(2, 0, 10, 'h'));
		
		// test invalid orientations
		assertFalse("isValid() should return false if orientation isn't 'h' or 'v' ", testBoard.isValidMove(3, 6, 7, 'g'));
		
		// test another good move
		assertTrue("isValidMove() should return true for a good move. 2", testBoard.isValidMove(3, 6, 7, 'v'));
		
		// test repeat wall placements
		assertFalse("isValidMove() should return false for a repeat wall placement.", testBoard.isValidMove(1, 0, 7, 'h'));
		
		// test another bad wall case
		assertFalse("isValidMove() should return false for a wall placementt hat would intersect another wall.", testBoard.isValidMove(1, 0, 7, 'v'));
	}
	
	@Test
	public void bigFatWallTest() {
		Player p1 = new Player(1, "test", "test1", 5);
		Player p2 = new Player(2, "test", "test2", 5);
		Player p3 = new Player(3, "test", "test3", 5);
		Player p4 = new Player(4, "test", "test4", 5);
		QuoridorBoard qboard = new QuoridorBoard(p1, p2, p3, p4);
		
		for (int i = 0; i<=7; i++) {
			for (int j = 0; j<=7; j++) {
				assertTrue("Wall placed at (" + i + ", " + j + "), 'h' should be valid.", qboard.isValidMove(1, i, j, 'h'));
				assertTrue("Wall placed at (" + i + ", " + j + "), 'v' should be valid.", qboard.isValidMove(1, i, j, 'v'));
			}
		}
		
		
		
		
		
		
	}
	
	

	@Test
	public void testPlaceWall() {
		QuoridorBoard testBoard = new QuoridorBoard(new Player(1, "test1", 6666, 10, 4, 0), new Player(2, "test2", 6667, 10, 4, 8));
		testBoard.placeWall(1, 4, 0, 'h');
		boolean julean = testBoard.isValidMove(1, 4, 1); // returns false if the wall was placed successfully
		assertFalse("Expected a wall to be in the way.", julean);
		
	}

	@Test
	public void testMovePawn() {
		QuoridorBoard testBoard = new QuoridorBoard(new Player(1, "test1", 6666, 10, 4, 0), new Player(2, "test2", 6667, 10, 4, 8));
		int newX = 4;
		int newY = 1;
		testBoard.movePawn(1, newX, newY);
		
		assertEquals("X value was not as expected.", newX, testBoard.getNodeByPlayerNumber(1).getxPos());
		assertEquals("Y value was not as expected.", newY, testBoard.getNodeByPlayerNumber(1).getyPos());
	}
	
	@Test
	public void testRemovePawn() {
		// TODO make sure the pawn gets removed
		QuoridorBoard testBoard = new QuoridorBoard(new Player(1, "TST", "test1", 5), 
														new Player(2, "TST", "test2", 5),
														new Player(3, "TST", "test3", 5),
														new Player(4, "TST", "test4", 5));
		
		int expectedPlayerCount = 3;
		testBoard.removePlayer(4);
		assertEquals("Expected player count to be " + expectedPlayerCount + ", got " + testBoard.getPlayerSet().size(), expectedPlayerCount, testBoard.getPlayerSet().size());
		
		
	}
	
	@Test
	public void testClone() {
	    // Creating arbitrary test board
	    QuoridorBoard testBoard = new QuoridorBoard(new Player(1, "test1", 6666, 10, 4, 0), new Player(2, "test2", 6667, 10, 4, 8));
	    QuoridorBoard clonedTestBoard = testBoard.clone();
	    
	    assertEquals("Board was not cloned correctly", testBoard.getNodeByPlayerNumber(1), clonedTestBoard.getNodeByPlayerNumber(1));
	}
	
	@Test
	public void testRemoveWall() {
	
	    Player p1 = new Player(1, "test1", 6666, 10, 4, 0);
	    QuoridorBoard testBoard = new QuoridorBoard(p1, 	
				      new Player(2, "test2", 6667, 10, 4, 8));
	    HashSet<Wall> wall1 = testBoard.getWallSet();
	    testBoard.placeWall(1, 0, 0, 'v');
	    Wall w = new Wall(0, 0, 'v');
	    testBoard.removeWall(p1, w);
	    assertEquals("removeWall() is not working correctly", wall1, testBoard.getWallSet() );
	}
	
	
	
	
	
	
	
	
	
	
}
