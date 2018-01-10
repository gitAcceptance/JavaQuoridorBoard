package quoridorFE;


import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

// Main import
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
//Imports for mouse events
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
// Import Panes
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.scene.text.TextBoundsType;

/**
* BEFORE YOU START
* The GUI should replicate the following google drawing found here:
* https://docs.google.com/drawings/d/1RK_2RZ2UmnLUQSSWyXzRwYLT4PVYZUkyu5Kdvs8xISQ/edit?usp=sharing
*/


public class Viewer extends Application {
	/*
	private static double DEFAULT_TILE_DIMMENSION = 50;
	private static double DEFAULT_WALL_WIDTH = DEFAULT_TILE_DIMMENSION / 5;
	private static double DEFAULT_WALL_HEIGHT = (2 * DEFAULT_TILE_DIMMENSION) + DEFAULT_TILE_DIMMENSION;  
	private static double DEFAULT_TILE_TRANSLATE = DEFAULT_WALL_WIDTH * DEFAULT_TILE_DIMMENSION;
	*/
	private static int DEFAULT_WALL_WIDTH = 20; // Used to be 30. Needs to be 15
	private static int DEFAULT_WALL_HEIGHT = 5 * DEFAULT_WALL_WIDTH;  // Used to be 90. Needs to be 75

	private static int DEFAULT_TILE_TRANSLATE = 3 * DEFAULT_WALL_WIDTH;
	private static int DEFAULT_TILE_DIMMENSION = 2 * DEFAULT_WALL_WIDTH;
	
	public static final CountDownLatch latch = new CountDownLatch(1);
	public static Viewer viewer = null;
	private QuoridorBoard board;
	
	private BorderPane theBorderPane;
	private Pane centerPane;
	private Scene mainScene = null;
	
	private Text p1Text = null;
	private Text p2Text = null;	
	private Text p3Text = null;
	private Text p4Text = null;
	
	
	private ArrayList<Tile> tileList;
	
	
	
	public Viewer() {
		viewerStartUp(this);
	}
	
	public static void viewerStartUp(Viewer v) {
		viewer = v;
		//latch.countDown();
	}
	
	public static Viewer waitForViewerStartUp() {
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return viewer;
	}
	
	public void testTheReference() {
		VBox vbox = new VBox(10);
		vbox.getChildren().addAll(new Button("Cut"), new Button("Copy"), new Button("Paste"));
		this.theBorderPane.setLeft(vbox);
		
	}
	
	// This will be commented out eventually because it should be launched 
	// from Client.java once, then updated as needed.
    public static void main(String[] args) {
        Application.launch(args);
    } 

    
	public void setBoard(QuoridorBoard board) {
		this.board = board;
	}  
	
    /**
     *  Called to update the state of the board based by the Server.
    **/
    public synchronized void refresh(){
		// Need to change states here, placeholder stub mainly.
		System.out.println("REFRESHING");
		Platform.runLater(new Runnable() {
            @Override
            public void run() {
            	
            	// this clears out the pawns on the board
            	for (Tile t : tileList) {
            		t.removePawn();
            	}
            	
            	// This finds the tile and draws a circle on it
            	for (Player p : board.getPlayerSet()) {
            		Tile t = getTileByCoords(board.getNodeByPlayerNumber(p.getID()).getxPos(), 
            								 board.getNodeByPlayerNumber(p.getID()).getyPos());
            		t.placePawn(p.getID());
            	}
            	
            	// This draws the walls
				for (Wall w : board.getWallSet()) {
					centerPane.getChildren().add(convertWall(w));
					//System.out.println("Should be drawing: " + w.toString());
				}
				
				// TODO refresh the wall count
				for (Player p : board.getPlayerSet()) {
					
					if (p.getID() == 1) {
						p1Text.setText(p.getName() + " Walls: " + p.wallsLeft());
						p1Text.setFill(Color.RED);
					} else if (p.getID() == 2) {
						p2Text.setText(p.getName() + " Walls: " + p.wallsLeft());
						p2Text.setFill(Color.BLUE);
					} else if (p.getID() == 3) {
						p3Text.setText(p.getName() + " Walls: " + p.wallsLeft());
						p3Text.setFill(Color.GREEN);
					} else if (p.getID() == 4) {
						p4Text.setText(p.getName() + " Walls: " + p.wallsLeft());
						p4Text.setFill(Color.PURPLE);
					}
					
				}
            }
		});
    }
    
    private Tile getTileByCoords(int x, int y) {
    	for (Tile t : tileList) {
    		if (t.theColumn == x && t.theRow == y) {
    			return t;
    		}
		}
    	return null;
    }
    
    private Rectangle convertWall(Wall w) {
    	Rectangle rect = new Rectangle();

    	if (w.orientation == 'v') {
    		rect = new Rectangle(DEFAULT_WALL_WIDTH, DEFAULT_WALL_HEIGHT);
    		// OLD WAY
    		//rect.setTranslateX(DEFAULT_WALL_WIDTH + (w.x * (4 * DEFAULT_WALL_WIDTH))); 
    		// rect.setTranslateY(w.y * (2 * DEFAULT_WALL_WIDTH));

    		rect.setTranslateX((DEFAULT_WALL_WIDTH * 2) + (3 * w.x * DEFAULT_WALL_WIDTH));  
    		rect.setTranslateY(w.y * (3 * DEFAULT_WALL_WIDTH));

    		// This is how the behavior should act
    		// wall.setTranslateX(30 + (column * 15));
		    // wall.setTranslateY(row * 45);
    	} else {
    		rect = new Rectangle(DEFAULT_WALL_HEIGHT, DEFAULT_WALL_WIDTH);
    		
    		rect.setTranslateX(3 * w.x * DEFAULT_WALL_WIDTH);    
    		rect.setTranslateY((w.y * DEFAULT_TILE_TRANSLATE) + (2 * DEFAULT_WALL_WIDTH));
    		
    	}
    	if (w.player == 1) {
    		rect.setFill(Color.RED);
    	} else if (w.player == 2) {
    		rect.setFill(Color.BLUE);
    	} else if (w.player == 3) {
    		rect.setFill(Color.GREEN);
    	} else if (w.player == 4) {
    		rect.setFill(Color.PURPLE);
    	} 	
    	return rect;
    }

    /**
	 * Top area - HBox
	 * HBox will lay out children in a single horizontal row
	 */
    private HBox drawTop() {
		
		HBox top = new HBox(1);
		top.setId("top");

		// If you want to nest a VBox within and HBox
		// VBox bottom = new VBox(1);
		// Then nest with 
	    // bottom.getChildren().add(top);

	    // Set properties for the HBox

	    // Create text to be displayed at the top panel
	    Text textTop = new Text("Quoridor - Death Match");
	    top.setStyle("-fx-background-color: #7FFFD4;");

	    // Set text to be blue
	    textTop.setFill(Color.BLUE);

	    // Set the font type and size of the top text
	    textTop.setFont(Font.font("Times New Roman", 22));

	    // Center the text 
	    textTop.setTextAlignment(TextAlignment.CENTER);

	    // Add all elements to the HBox
	    top.getChildren().addAll(textTop);
        
        // Center the top box alignment
        top.setAlignment(Pos.CENTER);

	    return top;
	}

	/**
	 * Bottom Area - Vbox
	 * Vbox will lay out children in a single vertical row 
	 */ 
	private VBox drawBottom() {

	    VBox bottom = new VBox();	// new VBox(num) will determine spacing between buttons
	    bottom.setId("bottom");
	    
	    // Set properties for the VBox
        bottom.setStyle("-fx-background-color: #7FFFD4;");

	    Text textBottom = new Text("Lorem ipsum dolor sit amet, \nconsectetur adipiscing elit, \nsed do eiusmod tempor incididunt ut labore et \ndolore magna aliqua. Ut enim ad \nminim veniam, quis nostrud exercitation ullamco laboris \nnisi ut aliquip ex ea commodo consequat. \nDuis aute irure dolor in reprehenderit in voluptate velit \nesse cillum dolore eu fugiat nulla pariatur. \nExcepteur sint occaecat cupidatat non proident, \nsunt in culpa qui officia deserunt mollit anim id est laborum.");

        // Attempt to display the board coords in the bottom of the text
	    // Text textBottom = new Text(board.getNodeByCoords(1, 2).toString());

	    // Init the left scrollpane.
	    ScrollPane sp = new ScrollPane();
	    sp.setPrefSize(480, 110);
	    sp.setContent(textBottom);

	    // Init the right HBox
	    HBox console = new HBox(30);
	    console.getChildren().addAll(sp);

	    // Default text for players turn
        Text turn_text = new Text("It is no ones turn.");
        turn_text.setFont(Font.font("Times New Roman", 50));

        // Add elements to the right text box
        console.getChildren().addAll(turn_text);

	    // Add all elements to the Vbox
	    bottom.getChildren().addAll(console);

	    //TODO: Create multiple VBoxes within the BottomPane
	    //TODO: Create a Vbox and a column for Exit Game, Kick Player
	    //TODO: Make each button the same size
	    //TODO: Center the buttons on the screen

	    return bottom;
	}

	/**
	 * Right Area - Vbox
	 * Vbox will lay out children in a single vertical row 
	 */ 
	public VBox drawRight () {

		VBox right = new VBox(50);
		right.setId("right");
		//right.setPrefWidth(200);
		// Set properties for the VBox
	    right.setStyle("-fx-background-color: #E89F65;");
	    
	    right.setSpacing(15);
	    right.setPadding(new Insets(10));

	    // HBox to display p1's walls
	    HBox p1 = new HBox(15);
	    p1Text = new Text("");
	    p1Text.setId("WallCount");
	    p1Text.setStyle("-fx-text-inner-color: RED;");
	    p1.getChildren().addAll(p1Text);

	    // Displaying p2's walls
	    HBox p2 = new HBox(15);
	    p2Text = new Text("");
	    p2Text.setId("WallCount");
	    p2Text.setStyle("-fx-text-inner-color: BLUE;");
	    p2.getChildren().addAll(p2Text);

	    // Displaying p3's walls
	    HBox p3 = new HBox(15);
	    p3Text = new Text("");
	    p3Text.setId("WallCount");
	    p3Text.setStyle("-fx-text-inner-color: GREEN;");
	    p3.getChildren().addAll(p3Text);
	    
	    // Displaying p4's walls
	    HBox p4 = new HBox(15);
	    p4Text = new Text("");
	    p4Text.setId("WallCount");
	    p4Text.setStyle("-fx-text-inner-color: PURPLE;");
	    p4.getChildren().addAll(p4Text);
    
	    // Vbox buttons holds all of the buttons
	    VBox buttons = new VBox(50);
	    buttons.setSpacing(10);
	    // Create buttons for movement 
	    Button moveUp = new Button("UP");
	    Button moveDown = new Button("DOWN");
	    Button moveLeft = new Button("LEFT");
	    Button moveRight = new Button("RIGHT");
	    Button kickPlayer = new Button("KICK PLAYER");
	    Button endGame = new Button("END GAME");
	    
	    moveUp.setMaxWidth(Double.MAX_VALUE);
	    moveDown.setMaxWidth(Double.MAX_VALUE);
	    moveLeft.setMaxWidth(Double.MAX_VALUE);
	    moveRight.setMaxWidth(Double.MAX_VALUE);
	    kickPlayer.setMaxWidth(Double.MAX_VALUE);
	    endGame.setMaxWidth(Double.MAX_VALUE);
	    
	    // setting up event that happens when up is pressed
	    moveUp.setOnAction(new EventHandler<ActionEvent>() {
		@Override public void handle(ActionEvent e) {
		// currently, it prints up to the console. eventually, the line below will be
		// used to talk to other parts of our project, informing them that up has been pressed
		System.out.println("WHAT LIES CAN I TELL YOU TODAY?");
		}
	     });
	     
	      // setting up event that happens when down is pressed
	    moveDown.setOnAction(new EventHandler<ActionEvent>() {
		@Override public void handle(ActionEvent e) {
		System.out.println("DON'T VOTE TRUMP, I'LL THINK YOU'RE STUPID");
		}
	     });

	      // setting up event that happens when left is pressed
	    moveLeft.setOnAction(new EventHandler<ActionEvent>() {
		@Override public void handle(ActionEvent e) {
		System.out.println("SOAP IN A SOCK LEAVES NO BRUISES");
		}
	     });
	     
	      // setting up event that happens when right is pressed
	    moveRight.setOnAction(new EventHandler<ActionEvent>() {
		@Override public void handle(ActionEvent e) {
		System.out.println("THE NSA KNOWS WHAT PORN I WATCH");
		}
	     });	
	     // setting up event that happens when kickPlayer is pressed
	    kickPlayer.setOnAction(new EventHandler<ActionEvent>() {
		@Override public void handle(ActionEvent e) {
		System.out.println("THE PUMPING LEMMA");
		}
	     });
	     	      // setting up event that happens when endGame is pressed
	    endGame.setOnAction(new EventHandler<ActionEvent>() {
		@Override public void handle(ActionEvent e) {
		System.out.println("GO, AND HAVE A WONDERFUL DAY");
		}
	     });
	     
	    buttons.getChildren().addAll(moveUp, moveDown, moveLeft, moveRight, kickPlayer, endGame);
	     
	    //TODO: Listener to get the number of walls for each player
	    //TODO: Update player walls on each turn for all players
	    

	    // Add all elements to the VBox
	    //right.getChildren().addAll(p1, p2, p3, p4, buttons);
	    right.getChildren().addAll(p1, p2, p3, p4);
        
        // Center the right pane
		//right.setAlignment(Pos.CENTER);
	    
		return right;
	}

	
	/** 
	 * Draw the center using a pane
	 */
	public Pane drawCenterWithPane (Pane thePane) {

		// May need to alter to be 1 to 10
		for (int row = 0; row < 9; row++) {
			for (int column = 0; column < 9; column++) {

				// Create a new tile
				Tile tile = new Tile(row, column);

				// Add the tile to an array
				tileList.add(tile);

				// Translate the X and Y, drawing another tile (old way to do it)
				// tile.setTranslateX(column * (1.5 * DEFAULT_WALL_WIDTH));
				// tile.setTranslateY(row * (1.5 * DEFAULT_WALL_WIDTH));

				tile.setTranslateX(column * DEFAULT_TILE_TRANSLATE);
				tile.setTranslateY(row * DEFAULT_TILE_TRANSLATE);

				// Add the tile to the board
				thePane.getChildren().add(tile);

				// Pesudo: Adding multiple children in a list into pane
				// for (Node node: Collection)
					// objectname.getChildren().add(node);
			}
		}
		
		// Draw Vertical Walls
		for (int row = 0; row < 8; row++) {
			for (int column = 0; column < 24; column+=3) { // +=3 lines up the columns properly
					
				Jwall wall = new Jwall(15, 75, row, column, 'v');

				wall.setTranslateX(30 + (column * 15));
				wall.setTranslateY(row * 45);

				thePane.getChildren().add(wall);											

			} // END FOR
		} // END FOR  
		
		
		// Draw horizontal walls
		for (int row = 0; row < 24; row+=3) { // +=3 lines up the rows properly
			for (int column = 0; column < 8; column++) {

				Jwall wall = new Jwall(75, 15, row, column, 'h'); 

				wall.setTranslateX(column * 45);  
				wall.setTranslateY(30 + (row * 15));
				
				thePane.getChildren().add(wall);

			} // END FOR
		} // END FOR 
		
		thePane.setStyle("-fx-background-color: #E89F65;");
		return thePane;
	}
    
	/**
	 * Launch the stage of the application.
	 * The stage controls window properties such as title,
	 * icon, visibility, resizability, fullscreen mode, and decorations.
	 */
	@Override
	public void start(Stage theStage) {
	    
	    // Set the title of the stage
   	    theStage.setTitle("Quoridor");

	    mainScene = new Scene(theBorderPane, 1000, 600);

	    // Add a stylesheet to the scene
	    mainScene.getStylesheets().add("application/ViewerStyle.css");
	    
	    // Set the scene for the stage
	    theStage.setScene(mainScene);

	    // Do not allow the stage to be resizable
		// theStage.setResizable(false);

		// Make the stage viewable.
		theStage.show();

		latch.countDown();
	}

	/**
	* Draw the board before start() is called
	*/
	@Override
	public void init() {

		theBorderPane = new BorderPane();
		theBorderPane.setStyle("-fx-background-color: #b94629;");
		tileList = new ArrayList<Tile>();

	    //theBorderPane.setTop(drawTop());
	    //theBorderPane.setBottom(drawBottom());
		theBorderPane.setRight(drawRight());
	    
	    centerPane = new Pane();

	    centerPane.setMaxSize(DEFAULT_TILE_DIMMENSION * 13, DEFAULT_TILE_DIMMENSION * 13);
	    centerPane.setPrefSize(DEFAULT_TILE_DIMMENSION * 13, DEFAULT_TILE_DIMMENSION * 13);
	    theBorderPane.setCenter(drawCenterWithPane(centerPane));
	    
	    //theBorderPane.setRight(drawRight());

	    // Master control for the window size
	    //theBorderPane.setPrefSize(1000, 600);	// Width X Height

	    // TODO: Get the elements of the top, getTop returns a node
	    // theBorderPane.getTop().getChildren();

	} 


	/**
	 * Tile class for creating a square on the grid
	 */
	private class Tile extends StackPane {

		// Declare local variables so that we can use getters for the parameters passed into the constructor
		int theRow;
		int theColumn;
		
		private Circle circle;
		private Text playerNumberText;
		private StackPane stack;

		public Tile(int row, int column) {

			this.theRow = row;
			this.theColumn = column;

			// System.out.println("Initial tile drawn at (" + this.theRow + "," + this.theColumn + ")");

			// TODO: Add tile to data structures, then on click should return the proper coordinates

			// TODO: Checking if a circle is on the tile means there
			// TODO: Place players on the board on startup (2 or 4 players)

			// Create a new rectangle for the grid
			Rectangle border = new Rectangle (DEFAULT_TILE_DIMMENSION, DEFAULT_TILE_DIMMENSION);
			
			
			border.setFill(Color.BLACK);
			//border.setStyle("-fx-background-color: #B94629;");
			// Set the line color of the tiles to black
			//border.setStroke(Color.TRANSPARENT);

			getChildren().addAll(border);
			/*
			// On mouse click, draw an X on the tile
			setOnMouseClicked(event -> {
				if(event.getButton() == MouseButton.PRIMARY) {

					// Draw a small circle on the tile (this will eventually be player)
					drawCircle();

					

					System.out.println("Tile placed at (" + this.getRow() + "," + this.getColumn() + ")");
				}
			});
			*/
		}

		// These were the getters and setters I was working with
		public void setRow(int theRow) {
			this.theRow = theRow;
		}

		public void setColumn(int theColumn) {
			this.theColumn = theColumn;
		}

		public int getRow() {
			return theRow;
		}

		public int getColumn() {
			return theColumn;
		}

		public void placePawn(int player) {
			circle = new Circle();
			playerNumberText = new Text();
			playerNumberText.setBoundsType(TextBoundsType.VISUAL); 
			stack = new StackPane();


			circle.setCenterX(50.0f);
			circle.setCenterY(50.0f);
			circle.setRadius(DEFAULT_TILE_DIMMENSION * 0.3);
			
			if (player == 1) {
				circle.setFill(Color.RED);
				playerNumberText.setText("1");
				playerNumberText.setFill(Color.WHITE);
				stack.getChildren().addAll(circle,playerNumberText);
	    	} else if (player == 2) {
	    		circle.setFill(Color.BLUE);
	    		playerNumberText.setText("2");
	    		playerNumberText.setFill(Color.WHITE);
	    		stack.getChildren().addAll(circle,playerNumberText);
	    	} else if (player == 3) {
	    		circle.setFill(Color.GREEN);
	    		playerNumberText.setText("3");
	    		playerNumberText.setFill(Color.WHITE);
	    		stack.getChildren().addAll(circle,playerNumberText);
	    	} else if (player == 4) {
	    		circle.setFill(Color.PURPLE);
	    		playerNumberText.setText("4");
	    		playerNumberText.setFill(Color.WHITE);
	    		stack.getChildren().addAll(circle,playerNumberText);
	    	}
			
			getChildren().add(stack);
		}
		
		public void removePawn() {
			getChildren().remove(stack);
		}
	}

	private class Jwall extends StackPane {

		// TODO: Set an identifier to access by wall (x,y,h) (x,y,v)

		// Declare local variables so that we can use getters for the parameters passed into the constructor
		int theWallLength;
		int theWallHeight;
		int theRow;
		int theColumn;
		char theOrientation;

		public Jwall(int wallLength, int wallHeight, int row, int column, char orientation) {

			this.theWallLength = wallLength;
			this.theWallHeight = wallHeight;
			this.theRow = row;
			this.theColumn = column;
			this.theOrientation = orientation;

			Rectangle theWall = new Rectangle(this.theWallLength, this.theWallHeight);

			// Set properties for the walls
			theWall.setFill(null);  // This needs to be null

			// Change this back to black if you need to see 
			theWall.setStroke(Color.TRANSPARENT);	

			// Add all the nodes to the object
			getChildren().addAll(theWall);

	        // On mouse click, update the color
			setOnMouseClicked(event -> {
				if(event.getButton() == MouseButton.PRIMARY) {
					
					// System.out.println("Clicked a wall at coordinate (x, y, orientation)");

					System.out.println("Wall placed at (" + this.getRow() + "," + this.getColumn() +  "," + this.getTheOrientation() + ")");

					// IF (the wall color is red) theWall.getFill()
						// wall placed here!

					theWall.setFill(Color.RED);
    				theWall.setStroke(Color.RED);
					
        			//theWall.isEmpty == false
				}
		    });
        }

        public int getTheWallLength() {
			return theWallLength;
		}

		public int getTheWallHeight() {
			return theWallHeight;
		}

        public int getRow() {
			return theRow;
		}

		public int getColumn() {
			return theColumn;
		}

		public char getTheOrientation() {
			return theOrientation;
		}
	}
}