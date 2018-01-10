package quoridorFE;

import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Kyle and Jesse
 */
public class PlayerTest {
    
    Player thePlayer = new Player(1, "Jesse", 8080, 1, 4, 4);

    /**
     * Test of getID method, of class Player.
     */
    @Test
    public void testGetID() {
        assertEquals(thePlayer.getID(), 1);
    }

    /**
     * Test of getName method, of class Player.
     */
    @Test
    public void testGetName() {
        assertEquals(thePlayer.getName(), "Jesse");
    }

    /**
     * Test of wallsLeft method, of class Player.
     */
    @Test
    public void testWallsLeft() {
        assertEquals(thePlayer.wallsLeft(), 1);
    }

    /**
     * Test of getX method, of class Player.
     */
    @Test
    public void testGetX() {
        assertEquals(thePlayer.getX(), 4);
    }

    /**
     * Test of getY method, of class Player.
     */
    @Test
    public void testGetY() {
        assertEquals(thePlayer.getY(), 4);
    }

    /**
     * Test of decrementWalls method, of class Player.
     */
    @Test
    public void testDecrementWalls() {
        thePlayer.decrementWalls();
        assertEquals(thePlayer.wallsLeft(), 0);
    }

    /**
     * Test of setX method, of class Player.
     */
    @Test
    public void testSetX() {
        thePlayer.setX(10);
        assertEquals(thePlayer.getX(), 10);
    }

    /**
     * Test of setY method, of class Player.
     */
    @Test
    public void testSetY() {
        thePlayer.setY(15);
        assertEquals(thePlayer.getY(), 15);
    }
}
