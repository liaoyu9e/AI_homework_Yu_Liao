package pacman.game;

/**
 * Data for the game state
 * Created by Yu Liao on 4/13/16.
 */
public class DataPoint {

//    int score;
//    int time;
//    double distance;
//    int index;
//    String move;
	private int closestPill;
	private int closestGhost;
	private boolean islair = false;
	private boolean toPill, awayGhost;
	public int getClosestPill() {
		return closestPill;
	}
	public void setClosestPill(int closestPill) {
		this.closestPill = closestPill;
	}
	public int getClosestGhost() {
		return closestGhost;
	}
	public void setClosestGhost(int closestGhost) {
		this.closestGhost = closestGhost;
	}
	public boolean isIslair() {
		return islair;
	}
	public void setIslair(boolean islair) {
		this.islair = islair;
	}
	public boolean isToPill() {
		return toPill;
	}
	public void setToPill(boolean toPill) {
		this.toPill = toPill;
	}
	public boolean isAwayGhost() {
		return awayGhost;
	}
	public void setAwayGhost(boolean awayGhost) {
		this.awayGhost = awayGhost;
	}
}
