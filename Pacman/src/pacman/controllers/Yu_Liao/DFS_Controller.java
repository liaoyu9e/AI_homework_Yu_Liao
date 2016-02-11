/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman.controllers.Yu_Liao;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import pacman.controllers.Controller;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Constants;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.internal.Node;

/**
 *
 * @author Yu Liao
 */
public class DFS_Controller extends Controller<MOVE>{


        
        /*public enum MOVE 
	{
		UP 		{ public MOVE opposite(){return MOVE.DOWN;		};},	
		RIGHT 	{ public MOVE opposite(){return MOVE.LEFT;		};}, 	
		DOWN 	{ public MOVE opposite(){return MOVE.UP;		};},		
		LEFT 	{ public MOVE opposite(){return MOVE.RIGHT;		};}, 	
		NEUTRAL	{ public MOVE opposite(){return MOVE.NEUTRAL;	};};	
		
		public abstract MOVE opposite();
	};*/
	

    public static StarterGhosts ghosts = new StarterGhosts();
	public MOVE getMove(Game game,long timeDue)
	{
            Random rnd=new Random();
            MOVE[] allMoves=MOVE.values();
        
            ArrayList<Integer> highScore = new ArrayList<Integer>();
            highScore.add(-1);
            ArrayList<MOVE> highMove = new ArrayList<MOVE>();
            highMove.clear();
            
           
            for(MOVE m: allMoves)
            {
                //System.out.println("Trying Move: " + m);
                Game gameCopy = game.copy();
                Game gameAtM = gameCopy;
                gameAtM.advanceGame(m, ghosts.getMove(gameAtM, timeDue));           
                if(gameAtM.getPacmanCurrentNodeIndex() == game.getPacmanCurrentNodeIndex() || m == MOVE.NEUTRAL){
                	System.out.println("Trying Move: " + m + " But it's invalid.");
                	continue;
                }
                else{
                	int tempHighScore = this.dfs_score(new PacManNode(gameAtM, 0), 7);
                	if(highScore.get(0) < tempHighScore)
                    	{
                    		highScore.clear();
                			highScore.add(tempHighScore);
                            highMove.clear();
                            highMove.add(m);
                    	}
                	else if(highScore.get(0) == tempHighScore){
                		highScore.add(tempHighScore);
                		highMove.add(m);
                	}
                	System.out.println("Trying Move: " + m + ", Score: " + tempHighScore);
                	}
            }
            if(highScore.size() == 1 && highMove.size() == 1){
                System.out.println("High Score: " + highScore.get(0) + ", High Move:" + highMove.get(0));
                return highMove.get(0);
            }else{
            	Game gameCopy = game.copy();
            	for(MOVE m:highMove){
            		gameCopy.advanceGame(m, ghosts.getMove(gameCopy, timeDue));
            		Game gameMore = gameCopy.copy();
            		for(MOVE mm:allMoves){
            			gameMore.advanceGame(mm, ghosts.getMove(gameMore, timeDue));
            			if(gameMore.getScore() != game.getScore()){
            				System.out.println("True High Score: " + highScore.get(highMove.lastIndexOf(m)) + ", High Move:" + m);
            				return m;
            			}
            		}
            	}
            	int res = rnd.nextInt(highMove.size());
            	System.out.println("High Score: " + highScore.get(res) + ", High Move:" + highMove.get(res));
                return highMove.get(res);
            }

                
	}
        
        public int dfs_score(PacManNode currentNode, int maxdepth){
            MOVE[] allMoves=Constants.MOVE.values();
		//System.out.println("Adding Node at Depth: " + rootGameState.depth);
            if(currentNode.depth >= maxdepth) return currentNode.gameState.getScore();
            int highScore = -1;
            for(MOVE m: allMoves)
            {
                Game gameCopy = currentNode.gameState.copy();
                gameCopy.advanceGame(m, ghosts.getMove(gameCopy, 0));
                if(gameCopy.getPacmanCurrentNodeIndex() != currentNode.gameState.getPacmanCurrentNodeIndex() && m != MOVE.NEUTRAL){
                	int score = dfs_score(new PacManNode(gameCopy, currentNode.depth+1), maxdepth);
                    if (highScore < score) highScore = score;
                }
                
            }
                return highScore;
	}
        
    
}
