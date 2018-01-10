package quoridorFE;

// PLayer object for storing and managing player information.
public class Player implements Comparable<Player>{

	// Private fields
	private int ID;  // Which number player he is.
	private String prefix; // team prefix
	private String name; // Player name
	private int port;
	private int wallsLeft; // How many walls he can still place.
	private int X, Y;

	// Constructor
	public Player(int ID, String prefix, String name, int wallsLeft) {
		this.ID = ID;
		this.prefix = prefix;
        this.name = name;
        this.port = -1;
        this.wallsLeft = wallsLeft;
        this.X = -1;
        this.Y = -1;
	}
	
	// FIXME can we please depreciate this constructor?     AV - 4/3/2016
	public Player(int ID, String name, int port, int wallsLeft, int startingX, int startingY){
		this.ID = ID;
		this.name = name;
		this.port = port;
		this.wallsLeft = wallsLeft;
		this.X = startingX;
		this.Y = startingY;
	}

	public int getID(){
		return ID;
	}

	public String getName(){
		return name;
	}

    public void setName(String newName){
	    this.name = newName;
    }

	public int wallsLeft(){
		return wallsLeft;
	}

	public int getX(){
		return X;
	}

	public int getY(){
		return Y;
	}

	public void decrementWalls(){
		wallsLeft--;
	}
	
	public void incrementWalls(){
		wallsLeft++;
	}

	public void setX(int v){
		X = v;
	}

	public void setY(int v){
		Y = v;
	}

	@Override
	public int compareTo(Player p) {
		return this.ID - p.ID;
	}



}
