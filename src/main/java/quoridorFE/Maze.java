package quoridorFE;

import java.util.ArrayList;
import java.util.Scanner;

public class Maze {

    // field
    Cell [][] cell;
    int rows;
    int cols;
    int playerNum;
    boolean start = true;

    public Maze (int rows, int cols, int player) {
    	playerNum = player;
        this.rows = rows;
	this.cols = cols;
	cell = new Cell[rows][cols];
        // do the first row
        cell[0][0] = new Cell (new VertWall(), new HorWall(), 
                               new VertDownWall(), new HorDownWall(), new CenterWall());
        cell[8][0] = new Cell (new VertWall(), new HorDownWall(), 
                new VertDownWall(), new HorWall(), new CenterWall());
        
        for (int col = 1; col < cols; col++)
	    cell[0][col] = new Cell(new VertDownWall(), new HorWall(),new VertDownWall(), new HorDownWall(), new CenterWall());
        
        cell[0][8] = new Cell (new VertDownWall(), new HorWall(), new VertWall(), new HorDownWall(), new CenterWall());
        // do the rest of the rows
        for (int row = 1; row < rows; row++) {
            cell[row][0] = new Cell (new VertWall(), new HorDownWall(),
                                     new VertDownWall(), new HorDownWall(), new CenterWall());
            for (int col = 1; col < cols; col++)
                cell[row][col] = new Cell(new VertDownWall(), 
                                         new HorDownWall(),
		                 new VertDownWall(), new HorDownWall(), new CenterWall());
	}
        
        for (int row = 1; row < rows; row++){
    	    cell[row][8] = new Cell(new VertDownWall(), new HorDownWall(),new VertWall(), new HorDownWall(), new CenterWall());
        
        }
        
        for (int i = 1; i < rows; i++){
        	cell[8][i] = new Cell(new VertDownWall(), new HorDownWall(), new VertDownWall(), new HorWall(), new CenterWall());	
        }
        	
        	
        cell[8][0] = new Cell (new VertWall(), new HorDownWall(), 
                new VertDownWall(), new HorWall(), new CenterWall());
        cell[8][8] = new Cell (new VertDownWall(), new HorDownWall(), 
                new VertWall(), new HorWall(), new CenterWall());
  
    }

    
    //Method to place walls within board
    public void placeWall(int col, int row, String direction){
    	
    	//Can't place a wall in row 8
    	if (row == 8){
    		System.out.println("Invalid Move");
    	}
    	// Can't place wall in column 8
    	else if (col == 8){
    		System.out.println("Invalid Move");
    	}
    	
    	//If column zero
    	if(col == 0){
    		//check if vertical wall
    		if (direction.equalsIgnoreCase("v")){
        		//If bottom of cell is already a wall
        		if(cell[row][col].down.toString() == "---"){
        			//If left of cell is already a wall
        			if(cell[row][col].left.toString() == "|"){
            			cell[row][col] = new Cell(new VertWall(), new HorDownWall(), new VertDownWall(), new HorWall(), new CenterWall());
            		}
            		else{
            			cell[row][col] = new Cell(new VertDownWall(), new HorDownWall(), new VertDownWall(), new HorWall(), new CenterWall());
            		}
        			cell[row][col] = new Cell(new VertWall(), new HorDownWall(), new VertWall(), new HorWall(), new CenterWall());
        		}
        		
        		else{
        			cell[row][col] = new Cell(new VertWall(), new HorDownWall(), new VertWall(), new HorDownWall(), new CenterWall());
        		}
        		//If bottom of second cell to add wall
        		if(cell[row +1][col].down.toString() == "---"){
        			cell[row +1][col] = new Cell(new VertWall(), new HorDownWall(), new VertWall(), new HorWall(), new CenterWall());
        		}
        		else{
        			cell[row +1][col] = new Cell(new VertWall(), new HorDownWall(), new VertWall(), new HorDownWall(), new CenterWall());
        		}
        		
        	}
    		
    		//check if direction is horizontal
    		else if (direction.equalsIgnoreCase("h")){
    			//check if cell to right is a wall
        		if(cell[row][col].right.toString() == "|"){
        			cell[row][col] = new Cell(new VertWall(), new HorDownWall(), new VertWall(), new HorWall(), new CenterWall());
        		}
        		else{
        			cell[row][col] = new Cell(new VertWall(), new HorDownWall(), new VertDownWall(), new HorWall(), new CenterWall());
        		}
        		//check if second cell to right is a wall
        		if(cell[row][col+1].right.toString() == "|"){
        			cell[row][col+1] = new Cell(new VertWall(), new HorDownWall(), new VertWall(), new HorWall(), new CenterWall());
        		}
        		else{
        			cell[row][col +1] = new Cell(new VertWall(), new HorDownWall(), new VertDownWall(), new HorWall(), new CenterWall());
        		}
        	}
		}
    	
    	//If in row zero
    	if(row == 0){
    		//check if direction is vertical
    		if (direction.equalsIgnoreCase("v")){
        		//check if bottom of cell is a wall
        		if(cell[row][col].down.toString() == "---"){
        			//check if left of cell is a wall
        			if(cell[row][col].left.toString() == "|"){
            			cell[row][col] = new Cell(new VertWall(), new HorWall(), new VertDownWall(), new HorWall(), new CenterWall());
            		}
            		else{
            			cell[row][col] = new Cell(new VertWall(), new HorWall(), new VertDownWall(), new HorWall(), new CenterWall());
            		}
        			cell[row][col] = new Cell(new VertWall(), new HorWall(), new VertWall(), new HorWall(), new CenterWall());
        		}
        		
        		else{
        			cell[row][col] = new Cell(new VertWall(), new HorWall(), new VertWall(), new HorDownWall(), new CenterWall());
        		}
        		//check if bottom of second cell to add wall is alreayd a wall
        		if(cell[row +1][col].down.toString() == "---"){
        			cell[row +1][col] = new Cell(new VertWall(), new HorWall(), new VertWall(), new HorWall(), new CenterWall());
        		}
        		else{
        			cell[row +1][col] = new Cell(new VertWall(), new HorWall(), new VertWall(), new HorDownWall(), new CenterWall());
        		}
        		
        	}
    		//Check if direction is horizontal
    		else if (direction.equalsIgnoreCase("h")){
    			//Check if right of cell is a wall
        		if(cell[row][col].right.toString() == "|"){
        			cell[row][col] = new Cell(new VertWall(), new HorWall(), new VertWall(), new HorWall(), new CenterWall());
        		}
        		else{
        			cell[row][col] = new Cell(new VertWall(), new HorWall(), new VertDownWall(), new HorWall(), new CenterWall());
        		}
        		//check if second cell to use already has a right wall
        		if(cell[row][col+1].right.toString() == "|"){
        			cell[row][col+1] = new Cell(new VertWall(), new HorWall(), new VertWall(), new HorWall(), new CenterWall());
        		}
        		else{
        			cell[row][col +1] = new Cell(new VertWall(), new HorWall(), new VertDownWall(), new HorWall(), new CenterWall());
        		}
        	}
		}
    	
    	
    	else if(col != 0 && row!= 0){
    	 if (direction.equalsIgnoreCase("v")){
    		
    		if(cell[row][col].down.toString() == "---"){
    			if(cell[row][col].left.toString() == "|"){
        			cell[row][col] = new Cell(new VertWall(), new HorDownWall(), new VertDownWall(), new HorWall(), new CenterWall());
        		}
        		else{
        			cell[row][col] = new Cell(new VertDownWall(), new HorDownWall(), new VertDownWall(), new HorWall(), new CenterWall());
    			cell[row][col] = new Cell(new VertDownWall(), new HorDownWall(), new VertWall(), new HorWall(), new CenterWall());
    		}
    		}
    		else{
    			cell[row][col] = new Cell(new VertDownWall(), new HorDownWall(), new VertWall(), new HorDownWall(), new CenterWall());
    		}
    		if(cell[row +1][col].down.toString() == "---"){
    			cell[row +1][col] = new Cell(new VertDownWall(), new HorDownWall(), new VertWall(), new HorWall(), new CenterWall());
    		}
    		else{
    			cell[row +1][col] = new Cell(new VertDownWall(), new HorDownWall(), new VertWall(), new HorDownWall(), new CenterWall());
    		}
    		//cell[row][col] = new Cell(new HorDownWall(), new VertDownWall(), new HorWall(), new VertWall());
    		//cell[row +1][col] = new Cell(new HorDownWall(), new VertDownWall(), new HorWall(), new VertWall());
    	}
    	else if (direction.equalsIgnoreCase("h")){
    		if(cell[row][col].right.toString() == "|"){
    			cell[row][col] = new Cell(new VertDownWall(), new HorDownWall(), new VertWall(), new HorWall(), new CenterWall());
    		}
    		else{
    			cell[row][col] = new Cell(new VertDownWall(), new HorDownWall(), new VertDownWall(), new HorWall(), new CenterWall());
    		}
    		if(cell[row][col+1].right.toString() == "|"){
    			cell[row][col+1] = new Cell(new VertDownWall(), new HorDownWall(), new VertWall(), new HorWall(), new CenterWall());
    		}
    		else{
    			cell[row][col +1] = new Cell(new VertDownWall(), new HorDownWall(), new VertDownWall(), new HorWall(), new CenterWall());
    		}
    	}
		
    	else{
    		System.out.println("Invalid Move");
    		
    	}
    	
    	}
    	
    	 
    }
    
    
    public String toString() {
        String result = "";
        if(playerNum == 4){
        cell[0][4].center.setCenterHor("1v");
        cell[8][4].center.setCenterHor("2^");
        cell[4][0].center.setCenterVer("3>");
        cell[4][8].center.setCenterVer("<4");
        }
        
        if(playerNum == 2){
        	cell[0][4].center.setCenterHor("1v");
            cell[8][4].center.setCenterHor("2^");
        }
     // print top walls
        for (int col = 0; col < cols; col++){
        	if(col == 4){
        		result += "+" + cell[0][col].center;
        	}
        	else
        		result += "+" + cell[0][col].up;
        }
	result += "+\n";
        // rest of rows
        for (int row = 0; row < rows; row++) {
        	if(row == 4 && playerNum == 4){
        		result += cell[4][0].center;
        	}
        	else
            result += cell[row][0].left;
	    for (int col = 0; col < cols; col++){
	    	if(col == 8 && row == 4 && playerNum == 4){
        		result += "  " + cell[4][8].center;
        	}
	    	else
	    		result += "   " + cell[row][col].right;   			    	
	    }
	    result += "\n";
	    for (int col = 0; col < cols; col++){
	    	if(col == 4 && row == 8){
        		result += "+" + cell[row][col].center;
        	}
	    	else
	        result += "+" + cell[row][col].down;
        }
	    result += "+\n";
        }
	    // take out the upper left and lower right "corner"
        //result = " " + result.substring(1, result.length()-2) + " \n";
	return result;
    }


    // INNER CLASSES
    abstract class Wall {
        public abstract String toString();
    }

    class HorWall extends Wall {
        public String toString() {
            return "---";
        }
    }

    class HorDownWall extends HorWall {
        public String toString() {
	    return "   ";
        }
    }

    class VertWall extends Wall {
        public String toString() {
	    return "|";
	}
    }

    class VertDownWall extends Wall {
        public String toString() {
	    return " ";
	}
    }
    
    class CenterWall extends Wall {
    	String center = "";
    	
    	public String toString() {
    		return getCenter();
    	}
    	
    	public void setCenterHor(String id){
    		center = "-" + id;
    	}
    	
    	public void setCenterVer(String id){
    		center = id;
    	}
    	
    	public String getCenter(){
    		return center;
    	}
    }

    
   public void placePlayer(ArrayList<Client> clients){
	   
			   
	   
	   
	   
   }


    class Cell {

        // fields
        public Wall left;
        public Wall up;
        public Wall right;
        public Wall down;
        public CenterWall center;

        public Cell( Wall left, Wall up, Wall right, Wall down, CenterWall player) {
    	this.left = left;
    	this.up = up;
    	this.right = right;
    	this.down = down;
    	this.center = player;
        }
        
        public Cell(Wall wall, String dir){
        	if(dir.equalsIgnoreCase("v")){
        		this.right = wall;
        		this.left = left;
            	this.up = up;
            	this.down = down;
        	}
        	else if(dir.equalsIgnoreCase("h")){
        		this.down = wall;
        		this.left = left;
            	this.up = up;
            	this.right = right;
        	}
        }

    }   
    // END INNER CLASSES

    public static void main (String [] args) {
	Maze maze = new Maze(9,9, 4);
	System.out.println(maze);
	Scanner scan = new Scanner(System.in);
	
	
	while(true) {
		System.out.print("> ");
		// read message from user
		String msg = scan.nextLine();
		String[] words = msg.split("\\s+");
		maze.placeWall(Integer.parseInt(words[0]), Integer.parseInt(words[1]), words[2]);
		System.out.println(maze);
		
    }
    }
}


