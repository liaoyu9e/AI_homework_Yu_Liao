package pacman.controllers.Yu_Liao;


import pacman.controllers.Controller;
import pacman.game.*;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

import java.util.*;

/**
 *  
 *  @Author Yu Liao
 *  
 */
public class KNN_Controller extends Controller<MOVE> {

    private static final int k = 5; // define k nearest neighbor

    // data sets path
    String path = "/home/liaoyu/workspace/Pacman2/data/data1.txt";

    // read data sets
    ReadData readData = new ReadData();

    // store data sets to the list
    public List<DataPoint> dataSet1 = readData.read(path);

    // all possible moves
    MOVE left = MOVE.LEFT;
    MOVE right = MOVE.RIGHT;
    MOVE up = MOVE.UP;
    MOVE down = MOVE.DOWN;
    MOVE neutral = MOVE.NEUTRAL;


    //KNN algorithm starts here
    public MOVE getMove(Game game, long timeDue) {
    	
    	// get current index of pac man
        int current= game.getPacmanCurrentNodeIndex();
        MOVE[] allMoves = game.getPossibleMoves(current);

        int size = dataSet1.size();
        
        int[] pills=game.getActivePillsIndices();
		int[] powerPills=game.getActivePowerPillsIndices();
		
		int cPill = 0;
		int closestPill = Integer.MAX_VALUE;
		int i = 0;
		for(i=0;i<pills.length;i++){				//find the nearest pill
			int tempDistance = game.getManhattanDistance(current, pills[i]);
			if(tempDistance < closestPill){
				closestPill = tempDistance;
				cPill = pills[i];
			}
		}
		
		for(;i<pills.length + powerPills.length;i++){				//find the nearest power pill
			int tempDistance = game.getManhattanDistance(current, powerPills[i - pills.length]);
			if(tempDistance < closestPill){
				closestPill = tempDistance;
			}
		}
		
		GHOST[] ghosts = GHOST.values();
		
		int closestGhost = Integer.MAX_VALUE;
		boolean islair = false;
		GHOST cGhost = ghosts[0];
		for(GHOST ghost : ghosts){						//find the nearest ghost and whether it is edible
			int tempDistance = game.getShortestPathDistance(current, game.getGhostCurrentNodeIndex(ghost));
			if(tempDistance < closestGhost){
				closestGhost = tempDistance;
				islair = game.getGhostEdibleTime(ghost)>0;
				cGhost = ghost;
			}
		}

        // convert data type to DataPoint
        DataPoint dp = new DataPoint();

        dp.setClosestPill(closestPill);
        dp.setClosestGhost(closestGhost);
        dp.setIslair(islair);


        Map<Double, Integer> map = new HashMap<>();

        // calculate euclidean distance for all data points
        for (i = 0; i < size; i++) {

            DataPoint di = dataSet1.get(i);
            double distance = computeDistance(di, dp);
           // System.out.println("distance:" + distance);
            map.put(distance, i);
        }

        // sort distance with ascending order and also keep the index
        SortedSet<Double> set = new TreeSet<>(map.keySet());

        int[] res = new int[k];
        int count = 0;

        // get k nearest index with the distance
        for (Double distance : set) {
            res[count] = map.get(distance);
            System.out.println("res[count]: " + res[count]);
            count++;
            if (count == k) {
                break;
            }
        }

        // get most voted strategy and find final move
        int[] votes = {0, 0, 0}; //Strategy vote

        for (i = 0; i < res.length; i++) {
             votes[0] += dataSet1.get(res[i]).isToPill() ? 1 : 0; //vote for going towards pill
             votes[1] += dataSet1.get(res[i]).isAwayGhost() ? 1 : 0; // vote for going away from ghost
             votes[2] += dataSet1.get(res[i]).isAwayGhost() ? 0 : 1; // vote for going towards ghost
        }
        int maxVote = 0, maxVoteIndex = 0;
        for (i = 0; i < votes.length; i++){
        	if (maxVote < votes[i]){
        		maxVote = votes[i];
        		maxVoteIndex = i;
        	}
        }
        switch(maxVoteIndex){
        	case 0 : return game.getNextMoveTowardsTarget(current, cPill, DM.PATH);
        	case 1 : return game.getNextMoveAwayFromTarget(current, game.getGhostCurrentNodeIndex(cGhost), DM.PATH);
        	case 2 : return game.getNextMoveTowardsTarget(current, game.getGhostCurrentNodeIndex(cGhost), DM.PATH);
        	default : return game.getNextMoveTowardsTarget(current, cPill, DM.PATH);
        }
    }

    /**
     * Calculate euclidean distance
     * @param d1
     * @param d2
     * @return distance in double between d1 and d2
     */
    public double computeDistance(DataPoint d1, DataPoint d2) {

        double pillPow = Math.pow((d1.getClosestPill() - d2.getClosestPill()), 2); // closest pill
        double ghostPow = Math.pow((d1.getClosestGhost() - d2.getClosestGhost()), 2); // closest ghost
        double lairPow = d1.isIslair() == d2.isIslair() ? 100 : 0 ; // lair or not

        return Math.sqrt(pillPow + ghostPow + lairPow);
    }

}


