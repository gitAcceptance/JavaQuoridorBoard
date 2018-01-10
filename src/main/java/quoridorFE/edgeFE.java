package quoridorFE;

import org.jgrapht.graph.DefaultEdge;

public class edgeFE extends DefaultEdge{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public BoardNode getSource() {
		return (BoardNode) super.getSource();
	}
	
	@Override
	public BoardNode getTarget() {
		return (BoardNode) super.getTarget();
	}
	
	@Override public String toString()
    {
        return "(" + this.getSource().toString() + " : " + this.getTarget().toString() + ")";
    }
}