package pacman.controllers.Yu_Liao;

import pacman.controllers.Controller;
import pacman.controllers.examples.AggressiveGhosts;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Game;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Random;

/**
 *
 * @author Yu Liao
 */
public class Minimax_Controller extends Controller<MOVE> {

	public static StarterGhosts ghosts = new StarterGhosts();
	public static AggressiveGhosts aggGhosts = new AggressiveGhosts();
	
	public MOVE getMove(Game game, long timeDue) {
		MOVE[] allMoves = MOVE.values();
		
		double highScore = Integer.MIN_VALUE;
        ArrayList<MOVE> highMove = new ArrayList<MOVE>();
        highMove.clear();
        
        Random rnd = new Random();
        
        MOVE finalMove = allMoves[rnd.nextInt(allMoves.length)];
		
		for (MOVE m : allMoves) {	
			Game gameCopy = game.copy();
			Game gameAtM = gameCopy;
			gameAtM.advanceGame(m, ghosts.getMove(gameAtM, timeDue));
			if (gameAtM.getPacmanCurrentNodeIndex() == game.getPacmanCurrentNodeIndex() || m == MOVE.NEUTRAL){
				System.out.println("Trying Move: " + m + " But it's invalid.");
				continue;
			}
			int depth = 10;
			double tempScore = Minimax(gameAtM, timeDue, null, depth, true);
			System.out.println("Trying Move " + m.toString() + " with score: " + tempScore);
			if (tempScore > highScore) {
    			highScore = tempScore;
                highMove.clear();
                highMove.add(m);
			}
		}
		
		if(highMove.size() == 1){
			finalMove = highMove.get(0);
		}else if(highMove.size() > 1){
			finalMove = highMove.get(rnd.nextInt(highMove.size()));
		}
		
		System.out.println("Go " + finalMove.toString());
		return finalMove;
	}
	
	private double Minimax(Game game, long timeDue, MOVE pacManMove, int depth, boolean maximizer){
		if(depth == 0){
			double score = game.getScore();
			int closestGhostDistance = Integer.MAX_VALUE;
			GHOST minGhost = null;
			
			for(GHOST ghost : GHOST.values()){
				int GhostDistance = game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(ghost));
				if(GhostDistance < closestGhostDistance){
					closestGhostDistance = GhostDistance;
					minGhost = ghost;
				}
			}
			
			if(closestGhostDistance < 20 && game.getGhostEdibleTime(minGhost) == 0 && game.getGhostLairTime(minGhost) == 0){
				return 1.0/(1.0/(score + 0.00001) + 1.0/(closestGhostDistance + 0.00001));
			}else{
				return score + closestGhostDistance;
			}
			
		}
		
		if(maximizer){
			double v = Double.NEGATIVE_INFINITY;
			for(MOVE m : MOVE.values()){
				v = Math.max(v, Minimax(game, timeDue, m, depth - 1, false));
			}
			return v;
		}else{
			Game gameCopy = game.copy();
			gameCopy.advanceGame(pacManMove, aggGhosts.getMove(game, timeDue));
			double v = Minimax(gameCopy, timeDue, null, depth - 1, true);
			return v;
		}
	}
}