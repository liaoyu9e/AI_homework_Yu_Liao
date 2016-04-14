package pacman.game;

import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import java.io.*;


/**
 *
 * Store Game state data with txt file
 * Created by Yu Liao on 4/13/16.
 */
public class Recorder {

    /**
     * Store game state to the specific directory
     * @param game
     * @param pacManMove
     */
    public void printGameInfo(Game game, MOVE pacManMove) {

    	// find state features
    	MOVE toPill=MOVE.NEUTRAL, toGhost=MOVE.NEUTRAL;
        int current = game.getPacmanCurrentNodeIndex();
		int[] pills=game.getActivePillsIndices();
		int[] powerPills=game.getActivePowerPillsIndices();
		
		int closestPill = Integer.MAX_VALUE;
		int i = 0;
		for(i=0;i<pills.length;i++){				//find the nearest pill
			int tempDistance = game.getManhattanDistance(current, pills[i]);
			if(tempDistance < closestPill){
				closestPill = tempDistance;
				toPill = game.getNextMoveTowardsTarget(current, pills[i], DM.PATH);
			}
		}
		
		for(;i<pills.length + powerPills.length;i++){				//find the nearest power pill
			int tempDistance = game.getManhattanDistance(current, powerPills[i - pills.length]);
			if(tempDistance < closestPill){
				closestPill = tempDistance;
				toPill = game.getNextMoveTowardsTarget(current, powerPills[i - pills.length], DM.PATH);
			}
		}
		
		GHOST[] ghosts = GHOST.values();
		
		int closestGhost = Integer.MAX_VALUE;
		boolean islair = false;
		
		for(GHOST ghost : ghosts){						//find the nearest ghost and whether it is edible
			int tempDistance = game.getShortestPathDistance(current, game.getGhostCurrentNodeIndex(ghost));
			if(tempDistance < closestGhost){
				closestGhost = tempDistance;
				islair = game.getGhostEdibleTime(ghost)>0;
				toGhost = game.getNextMoveTowardsTarget(current, game.getGhostCurrentNodeIndex(ghost), DM.PATH);
			}
		}

        // directory to store training data
        String path = "/home/liaoyu/workspace/Pacman2/data/data1.txt";
        File file = new File(path);

        try {
            PrintWriter pw = new PrintWriter(new FileWriter(file, true));

    		pw.print(closestPill + " "); // get the distance from nearest pill or power pill
    		
    		pw.print(closestGhost + " "); // get the distance from closest ghost
    		pw.print(islair ? 1 : 0 + " "); // get whether this ghost is edible
    		
    		pw.print(pacManMove == toPill ? 1 : 0 + " "); // get whether pac man move towards pills
    		pw.println(pacManMove == toGhost ? 0 : 1); // get whether pac man move away from ghosts
    		
//            pw.print(game.getScore() + " "); // get current score
//            pw.print(game.getTotalTime() + " "); // get time
//            pw.print(game.getEuclideanDistance(currPacLocation, ghostLocation) + " "); // get distance
//            pw.print(game.getPacmanCurrentNodeIndex() + " "); // get current index
//            pw.println(pacManMove); // move

            pw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This is for test
     * @param args
     */
    public static void main(String[] args) {

        return;
    }

}
