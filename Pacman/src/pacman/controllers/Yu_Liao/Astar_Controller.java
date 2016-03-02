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
public class Astar_Controller extends Controller<MOVE> {

	private int stepCount, target;

	private boolean nullTarget = true;

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
					this.nullTarget = true;
					return game.getNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(),
							game.getGhostCurrentNodeIndex(ghost), DM.PATH);
				}
		MOVE[] allMoves = MOVE.values();
		// if no target point set, set the target point
		if (this.nullTarget) {
			// get all active pills
			int[] activePills = game.getActivePillsIndices();

			// get all active power pills
			int[] activePowerPills = game.getActivePowerPillsIndices();

			// create a target array that includes all ACTIVE pills and power pills
			int[] targetNodeIndices = new int[activePills.length + activePowerPills.length];

			for (int i = 0; i < activePills.length; i++)
				targetNodeIndices[i] = activePills[i];

			for (int i = 0; i < activePowerPills.length; i++)
				targetNodeIndices[activePills.length + i] = activePowerPills[i];

			// set the target and reset step count
			this.target = game.getClosestNodeIndexFromNodeIndex(currentNodeIndex, targetNodeIndices, DM.PATH);
			this.nullTarget = false;
			this.stepCount = 0;
			System.out.println("New target");
		}
		// Astar algorithm starts here
		int lowDist = Integer.MAX_VALUE;
		MOVE lowMove = MOVE.NEUTRAL;
		for (MOVE m : allMoves) {
			if (m == game.getPacmanLastMoveMade().opposite() || m == MOVE.NEUTRAL)
				continue;
			Game gameCopy = game.copy();
			Game gameAtM = gameCopy;
			gameAtM.advanceGame(m, ghosts.getMove(gameAtM, timeDue));
			int curNode = gameAtM.getPacmanCurrentNodeIndex();
			if (curNode == currentNodeIndex)
				continue;
			if (curNode == this.target) {
				this.nullTarget = true;
				System.out.println("Target captured");
				return m;
			}
			int tempDist = this.stepCount + 1 + gameAtM.getShortestPathDistance(curNode, this.target);
			if (lowDist > tempDist) {
				lowDist = tempDist;
				lowMove = m;
			}

		}
		this.stepCount++;
		System.out.println("Go " + lowMove.toString());
		return lowMove;
	}
}