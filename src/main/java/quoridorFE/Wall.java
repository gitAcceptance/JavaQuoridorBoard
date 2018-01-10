package quoridorFE;



public class Wall {
	int player;
	int x;
	int y;
	char orientation;
	
	public Wall(int x, int y, char orientation) {
		super();
		this.x = x;
		this.y = y;
		this.orientation = orientation;
	}
	
	public Wall(int player, int x, int y, char orientation) {
		super();
		this.player = player;
		this.x = x;
		this.y = y;
		this.orientation = orientation;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + orientation;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Wall other = (Wall) obj;
		if (this.orientation != other.orientation)
			return false;
		if (this.x != other.x)
			return false;
		if (this.y != other.y)
			return false;
		return true;
	}
	

	@Override
	public String toString() {
		return "Wall [x=" + x + ", y=" + y + ", orientation=" + orientation + "]";
	}
	
}