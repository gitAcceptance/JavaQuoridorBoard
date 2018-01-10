/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quoridorFE;

//import org.powermock.*;
import java.util.ArrayList;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Ignore;
import static org.junit.Assert.*;

/**
 *
 * @author Kyle
 */
public class MazeTest {
        
    @BeforeClass
    public static void setUpClass() {

	Maze m = new Maze(9,9,2);
	assertNotNull(m);
    }

    /**
     * Test of placeWall method, of class Maze.
     */
    @Test
    public void testPlaceWall() {
        //System.out.println("placeWall");
        int col = 3;
        int row = 3;
        String direction = "h";
        Maze instance = new Maze(9,9,2);
        instance.placeWall(col, row, direction);
	assertEquals(instance.cell[col][row].down.toString(), "---");

        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
	//direction = "v";
	//instance.placeWall(col, row, direction);
	col = 5;
	row = 5;
	direction = "v";
        instance.placeWall(col, row, direction);
        assertEquals(instance.cell[col][row].right.toString(), "|");
    }

    /**
     * Test of toString method, of class Maze.
     */
    @Test
    public void testToString() {
        //System.out.println("toString");
        Maze instance = new Maze(9,9,2);
        String expResult = "+---+---+---+---+-1v+---+---+---+---+\n|                                   |\n+   +   +   +   +   +   +   +   +   +\n|                                   |\n+   +   +   +   +   +   +   +   +   +\n|                                   |\n+   +   +   +   +   +   +   +   +   +\n|                                   |\n+   +   +   +   +   +   +   +   +   +\n|                                   |\n+   +   +   +   +   +   +   +   +   +\n|                                   |\n+   +   +   +   +   +   +   +   +   +\n|                                   |\n+   +   +   +   +   +   +   +   +   +\n|                                   |\n+   +   +   +   +   +   +   +   +   +\n|                                   |\n+---+---+---+---+-2^+---+---+---+---+\n";
	//System.out.println(expResult);
        String result = instance.toString();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of placePlayer method, of class Maze.
     */
    @Ignore
    @Test
    public void testPlacePlayer() {
        System.out.println("placePlayer");
        ArrayList<Client> clients = null;
        Maze instance = null;
        instance.placePlayer(clients);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of main method, of class Maze.
     */
    @Ignore
    @Test
    public void testMain() {
        System.out.println("main");
        //String[] args = null;
        //Maze.main(args);
	//MyClass.main();
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
	//PowerMockito.mockStatic(Maze.class);
	//PowerMockito.when(Maze.main()).thenReturn(null);
    }
}
