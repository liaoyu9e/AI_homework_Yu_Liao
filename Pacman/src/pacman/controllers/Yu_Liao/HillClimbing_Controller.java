package pacman.controllers.Yu_Liao;

import pacman.controllers.Controller;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Game;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

/**
 *
 * @author Yu Liao
 */
public class HillClimbing_Controller extends Controller<MOVE> {

	public static StarterGhosts ghosts = new StarterGhosts();

	private static final int MIN_DISTANCE = 20; // if a ghost is this close, run away

	public MOVE getMove(Game game, long timeDue) {
		
		int currentNodeIndex = game.getPacmanCurrentNodeIndex();

		// if any non-edible ghost is too close (less than MIN_DISTANCE), run away
		for (GHOST ghost : GHOST.values())
			if (game.getGhostEdibleTime(ghost) == 0 && game.getGhostLairTime(ghost) == 0)
				if (game.getShortestPathDistance(currentNodeIndex,
						game.getGhostCurrentNodeIndex(ghost)) < MIN_DISTANCE) {
					// reset target
					System.out.println("Running away!");
					return game.getNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(),
							game.getGhostCurrentNodeIndex(ghost), DM.PATH);
				}
		MOVE[] allMoves = MOVE.values();
		
		// HillClimbing algorithm starts here
		int highScore = Integer.MIN_VALUE;
		MOVE highMove = MOVE.NEUTRAL;
		for (MOVE m : allMoves) {
			if (m == game.getPacmanLastMoveMade().opposite() || m == MOVE.NEUTRAL){
            	System.out.println("Trying Move: " + m + " But it's invalid.");
            	continue;
			}	
			Game gameCopy = game.copy();
			Game gameAtM = gameCopy;
			gameAtM.advanceGame(m, ghosts.getMove(gameAtM, timeDue));
			int curNode = gameAtM.getPacmanCurrentNodeIndex();
			if (curNode == currentNodeIndex){
            	System.out.println("Trying Move: " + m + " But it's invalid.");
				continue;
			}
			int tempScore = gameAtM.getScore();
			if (highScore < tempScore) {
				highScore = tempScore;
				highMove = m;
			}
		}
		System.out.println("Go " + highMove.toString());
		return highMove;
	}
}