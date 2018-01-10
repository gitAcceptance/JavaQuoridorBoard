/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quoridorFE;

import java.io.*;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Ignore;
import static org.junit.Assert.*;

/**
 *
 * @author Kyle
 */
public class ServerTest {
    // Hope no one minds some Global Variables.
    protected static ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    protected static ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    public static Server test_server = new Server("test", 8080);

    @BeforeClass
    public static void setUpClass() {

	System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }


    /**
     * Test of start method, of class Server.
     */
    //@Ignore
    @Test
    public void testStart() throws Exception{
        Thread t = new Thread(new Runnable(){
		public void run(){
			test_server.start();
		}
	});
	t.start();
	t.join(500);
	assertTrue(t.isAlive());
	t.stop();
    }

    /**
     * Test of stop method, of class Server.
     */
    @Ignore
    @Test
    public void testStop() {
        System.out.println("stop");
        Server instance = null;
        instance.stop();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    @Test
    public void testDisplay(){
	// Gonna need a thread.
        Thread t = new Thread(new Runnable(){
                public void run(){
                        test_server.start();
                }
        });

	// Steal System.output and place it in a readable place for testing, then test the first words that should appear.
	String[] temp = outContent.toString().split("\\s+");
	assertEquals(temp[0], "Server");

	// Clean up after work.
	t.stop();
    }

    /**
     * Test of remove method, of class Server.
     */
    @Ignore  
    @Test
    public void testRemove() {

	// Setup a server to test.
        Thread t = new Thread(new Runnable(){
                public void run(){
                        test_server.start();
                }
        });

	//Client instance = new Client();
        /*System.out.println("remove");
        int id = 0;
        Server instance = null;
        instance.remove(id);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");*/
    }

    /**
     * Test of main method, of class Server.
     */
    @Ignore  
    @Test
    public void testMain() {
        System.out.println("main");
        String[] args = null;
        Server.main(args);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
