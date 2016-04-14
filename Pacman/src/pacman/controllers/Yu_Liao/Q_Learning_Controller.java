package pacman.controllers.Yu_Liao;

import java.util.*;
import pacman.controllers.Controller;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Game;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

/**
 *
 * @author Yu Liao
 */

class StateNode{
	
	public Game curGame;
	private int closestPill;
	private int closestPowerPill;
	private int closestGhost;
	private boolean islair = false;

	public Set<MoveNode> adjacent = new HashSet<MoveNode>();
	
	public StateNode(Game curGame) {
		this.curGame = curGame;
		
		int current = curGame.getPacmanCurrentNodeIndex();
		int[] pills=curGame.getActivePillsIndices();
		int[] powerPills=curGame.getActivePowerPillsIndices();
		
		this.closestPill = Integer.MAX_VALUE;
		
		for(int i=0;i<pills.length;i++){				//find the nearest pill
			int tempDistance = curGame.getManhattanDistance(current, pills[i]);
			this.closestPill = tempDistance < this.closestPill ? tempDistance : this.closestPill;
		}
		
		this.closestPowerPill = Integer.MAX_VALUE;
		
		for(int i=0;i<powerPills.length;i++){				//find the nearest power pill
			int tempDistance = curGame.getManhattanDistance(current, powerPills[i]);
			this.closestPowerPill = tempDistance < this.closestPowerPill ? tempDistance : this.closestPowerPill;
		}
		
		GHOST[] ghosts = GHOST.values();
		
		this.closestGhost = Integer.MAX_VALUE;
		
		for(GHOST ghost : ghosts){						//find the nearest ghost and whether it is edible
			int tempDistance = curGame.getShortestPathDistance(current, curGame.getGhostCurrentNodeIndex(ghost));
			if(tempDistance < this.closestGhost){
				this.closestGhost = tempDistance;
				this.islair = curGame.getGhostEdibleTime(ghost)>0;
			}
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StateNode other = (StateNode) obj;
		return this.islair == other.islair && this.closestGhost == other.closestGhost && this.closestPill == other.closestPill && this.closestPowerPill == other.closestPowerPill;
	}
	
}

class MoveNode{
	public float QScore;
	public MOVE move;
	public StateNode fromState,toState;
	public MoveNode(StateNode fromState, MOVE move, StateNode toState) {
		this.fromState = fromState;
		this.move = move;
		this.toState = toState;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MoveNode other = (MoveNode) obj;
		if (fromState == null) {
			if (other.fromState != null)
				return false;
		} else if (!fromState.equals(other.fromState))
			return false;
//		if (move != other.move)
//			return false;
		if (toState == null) {
			if (other.toState != null)
				return false;
		} else if (!toState.equals(other.toState))
			return false;
		return true;
	}
}

public class Q_Learning_Controller extends Controller<MOVE> {

	public static StarterGhosts ghosts = new StarterGhosts();
	
	public StateNode curNode = null;
	
	//parameters of Q learning
	public static final float learningRate = 0.8f;
	public static final float discount = 0.5f;
	
	//method of updating state node
	public static void updateNode(StateNode curNode, MOVE m, StateNode nextNode){
		MoveNode link = new MoveNode(curNode, m, nextNode);
		
		int reward = (nextNode.curGame.wasPillEaten() ? 30 : 0)
				+ (nextNode.curGame.wasPowerPillEaten() ? 20 : 0)
				+ 50 * (nextNode.curGame.getNumGhostsEaten() - curNode.curGame.getNumGhostsEaten())
				- (curNode.curGame.getPacmanLastMoveMade() == m.opposite() ? 7 : 5)
				+ 300 * (nextNode.curGame.getPacmanNumberOfLivesRemaining() - curNode.curGame.getPacmanNumberOfLivesRemaining());
				
		if(curNode.adjacent.isEmpty() || !(curNode.adjacent.contains(link)) ){
			link.QScore = reward;
			curNode.adjacent.add(link);
			System.out.println("new link added");
		}else{
			System.out.println("link updated");
			for(MoveNode mn : curNode.adjacent){
				if(link.equals(mn)){
					
					float tempQ = 0;
					for(MoveNode mnp : mn.toState.adjacent){
						tempQ = mnp.QScore > tempQ ? mnp.QScore : tempQ;
					}
					
					link.QScore = mn.QScore + learningRate * (reward + discount * tempQ - mn.QScore);
					curNode.adjacent.remove(mn);
					curNode.adjacent.add(link);
					break;
				}
			}
		}
	}

	public MOVE getMove(Game game, long timeDue) {
		
		int current = game.getPacmanCurrentNodeIndex();
		
		this.curNode = new StateNode(game);

		MOVE[] allMoves = game.getPossibleMoves(current);
		
		// Q learning algorithm starts here
		float highScore = Integer.MIN_VALUE;
		MOVE highMove = MOVE.NEUTRAL;
		
		for (MOVE m : allMoves) {
			
			Game gameCopy = game.copy();
			Game gameAtM = gameCopy;
			
			gameAtM.advanceGame(m, ghosts.getMove(gameAtM, timeDue));
			StateNode tempNode = new StateNode(gameAtM);
			
			updateNode(this.curNode, m, tempNode);
		}
		
		for (MoveNode mn : this.curNode.adjacent){
			System.out.println("Try to move " + mn.move.toString() + " with Q value " + mn.QScore);
			if (highScore < mn.QScore){
				highScore = mn.QScore;
				highMove = mn.move;
			}
		}
		System.out.println("Go " + highMove.toString());
		return highMove;
	}
}