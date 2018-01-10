package quoridorFE;

public class BoardNode {
	private Player player = null;
	private int xPos;
	private int yPos;
	
	
	public BoardNode(){
		this(-1, -1);
	}
	
	public BoardNode(int xPos, int yPos){
		this.setxPos(xPos);
		this.setyPos(yPos);
	}
	
	public BoardNode newInstance(){
		return new BoardNode();
	}

	/**
	 * @return the player or null if there is no player here
	 */
	public synchronized Player getPlayer() {
		return player;
	}

	/**
	 * @param player the player to set
	 */
	public synchronized void setPlayer(Player player) {
		this.player = player;
	}

	public int getxPos() {
		return xPos;
	}

	public void setxPos(int xPos) {
		this.xPos = xPos;
	}

	/**
	 * @return the yPos
	 */
	public int getyPos() {
		return yPos;
	}

	public void setyPos(int yPos) {
		this.yPos = yPos;
	}
	
	
	public String toString() {
		return "x:" + this.getxPos() + " y:" + this.getyPos();
	}
	
	
}
