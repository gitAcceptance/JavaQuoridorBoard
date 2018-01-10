package quoridorFE;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Ignore;
import java.util.*;

public class TestAi {

	@Test
	public void testGetMoveShortestPath() {
		QuoridorBoard testBoard = new QuoridorBoard(new Player(1, "TST", "test1", 5), 
                                                                                                        new Player(2, "TST", "test2", 5),
                                                                                                        new Player(3, "TST", "test3", 5),
                                                                                                        new Player(4, "TST", "test4", 5));
		String str = "(4, 1)";
		String cmpstr = FEai.getMoveShortestPath(1, testBoard);
		assertEquals("Expected " + str, str, cmpstr);
		testBoard.movePawn(1, 4, 1);
		testBoard.movePawn(1, 4, 2);
		testBoard.movePawn(1, 4, 3);
		testBoard.movePawn(1, 4, 4);
		testBoard.movePawn(2, 4, 7);
		testBoard.movePawn(2, 4, 6);
		testBoard.movePawn(2, 4, 5);
		// pawn 1 is at (4, 4) and 2 is at (4, 5)
		str = "(4, 6)";
		cmpstr = FEai.getMoveShortestPath(1, testBoard);
		assertEquals("Single jump, expected " + str + " Recieved: " + cmpstr, str, cmpstr);
		// tested single jump
		
		testBoard.movePawn(3, 0, 5);
		testBoard.movePawn(3, 0, 6);
		testBoard.movePawn(3, 1, 6);
		testBoard.movePawn(3, 2, 6);
		testBoard.movePawn(3, 3, 6);
		testBoard.movePawn(3, 4, 6);
		// pawn 1 is at (4, 4), 2 is at (4, 5), and 3 is at (4, 6)
		str = "(4, 7)";
		cmpstr = FEai.getMoveShortestPath(1, testBoard);
		assertEquals("Double jump, expected " + str + " Recieved: " + cmpstr, str, cmpstr);
		// tested double jump
		
		testBoard.movePawn(4, 8, 5);
		testBoard.movePawn(4, 8, 6);
		testBoard.movePawn(4, 8, 7);
		testBoard.movePawn(4, 7, 7);
		testBoard.movePawn(4, 6, 7);
		testBoard.movePawn(4, 5, 7);
		testBoard.movePawn(4, 4, 7);
		// pawn 1 is at (4, 4), 2 is at (4, 5), 3 is at (4, 6), and 4 is at (4, 7)
		str = "(4, 8)";
		cmpstr = FEai.getMoveShortestPath(1, testBoard);
		assertEquals("Triple jump, expected " + str + " Recieved: " + cmpstr, str, cmpstr);
		// tested triple jump
		
	}
	
	@Ignore
	@Test
	public void testGetShitMoveWithBlockingEnemy() {
		QuoridorBoard testBoard = new QuoridorBoard(new Player(1, "TST", "test1", 5), 
													new Player(2, "TST", "test2", 5),
													new Player(3, "TST", "test3", 5),
													new Player(4, "TST", "test4", 5));
			String str = "(4, 1)";
			String cmpstr = FEai.getMoveShortestPath(1, testBoard);
			assertEquals("Expected " + str, str, cmpstr);
			
			testBoard.movePawn(1, 4, 1);
			testBoard.movePawn(1, 4, 2);
			testBoard.movePawn(1, 4, 3);
			testBoard.movePawn(1, 4, 4);
			testBoard.movePawn(1, 4, 5);
			// pawn 1 is at (4, 5)
			testBoard.movePawn(2, 4, 7);
			testBoard.movePawn(2, 4, 6);
			// pawn 2 is at (4, 6)
			testBoard.movePawn(3, 0, 5);
			testBoard.movePawn(3, 0, 6);
			testBoard.movePawn(3, 0, 7);
			testBoard.movePawn(3, 1, 7);
			testBoard.movePawn(3, 2, 7);
			testBoard.movePawn(3, 3, 7);
			testBoard.movePawn(3, 4, 7);
			// pawn 3 is at (4, 7)
			testBoard.movePawn(4, 8, 5);
			testBoard.movePawn(4, 8, 6);
			testBoard.movePawn(4, 8, 7);
			testBoard.movePawn(4, 8, 8);
			testBoard.movePawn(4, 7, 8);
			testBoard.movePawn(4, 6, 8);
			testBoard.movePawn(4, 5, 8);
			testBoard.movePawn(4, 4, 8);
			// pawn 4 is at (4, 8)
			
			// 4/13/2014 AV - This test is here to highlight how the shit AI might return a bad move if it's shortest path to victory ends on an occupied space. 
			
			cmpstr = FEai.getMoveShortestPath(1, testBoard);
			assertTrue("Expected a valid move to be returned by FEai.getShitMove()", testBoard.isValidMove(1, Integer.parseInt(Character.toString(cmpstr.charAt(1))), Integer.parseInt(Character.toString(cmpstr.charAt(4)))));
	}
	
	@Test
	public void testBlockClosestPlayer2() {
		// Build testboard
		QuoridorBoard testBoard = new QuoridorBoard(new Player(1, "TST", "test1", 5), 
					    new Player(2, "TST", "test2", 5));
		// Move player 2 to 4, 3
		testBoard.movePawnUnchecked(2, 4, 3);
		FEai testAI = new FEai();
		String blockingWall = testAI.blockClosestOpponent(1, testBoard);// Asking for a move
		assertEquals( "[(4, 2), h]", blockingWall);
	}
	
	@Test
	public void testBlockClosestPlayer4(){
                // Build testboard
                QuoridorBoard testBoard = new QuoridorBoard(new Player(1, "TST", "test1", 5), 
                                            new Player(2, "TST", "test2", 5),
                                            new Player(3, "TST", "test3", 5),
                                            new Player(4, "TST", "test4", 5));
                // Move player 2 to 4, 3
                testBoard.movePawnUnchecked(3, 3, 4);
                FEai testAI = new FEai();
                String blockingWall = testAI.blockClosestOpponent(1, testBoard);// Asking for a move
		assertEquals("[(3, 4), v]", blockingWall);
	}
	
	@Test
	public void tunnelWall(){
	    
	    // Build TestBoard
	    QuoridorBoard testBoard = new QuoridorBoard(
		new Player(1, "TST", "test1", 10), 
		new Player(2, "TST", "test2", 10));
	    // move player 2 to (7, 8)
	    testBoard.movePawnUnchecked(2, 8, 7);
	    
	    //Place Walls up the rightmost column
	    testBoard.placeWall(1, 7, 0, 'v');
	    testBoard.placeWall(1, 7, 2, 'v');
	    testBoard.placeWall(1, 7, 4, 'v');
	    testBoard.placeWall(1, 7, 6, 'v');
	    
	    
	    FEai testAI = new FEai();
	    String testTunnelWall = testAI.tunnelWall(2, testBoard);
	    
	    // Wall returned should be 7, 7 h
	    assertEquals("The wall returned is incorrect", "[(7, 7), h]", testTunnelWall);
	}
	
	@Test
	public void testAvailableWinningNodes() {
	    
	    // Build TestBoard
	    QuoridorBoard testBoard = new QuoridorBoard(
		new Player(1, "TST", "test1", 10), 
		new Player(2, "TST", "test2", 10));
	    // move player 2 to (7, 8)
	    testBoard.movePawnUnchecked(2, 8, 7);
	    
	    //Place Walls up the rightmost column
	    testBoard.placeWall(1, 7, 0, 'v');
	    testBoard.placeWall(1, 7, 2, 'v');
	    testBoard.placeWall(1, 7, 4, 'v');
	    testBoard.placeWall(1, 7, 6, 'v');	
	    
	    testBoard.placeWallUnchecked(1, 7, 7, 'h');	
	    FEai testAI = new FEai();
	    
	    HashSet<BoardNode> winningNodes = FEai.availableWinningNodes(
		testBoard.getNodeByPlayerNumber(2), testBoard);
	    assertEquals("Not Working Correctly", 1, winningNodes.size());
	}
		
	
}









