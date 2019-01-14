/**
 * 
 */
package laghertha;

import java.awt.Color;
import java.util.ArrayList;

import game.Minesweeper;
import game.Pair;

/**
 * @author Vali
 *
 */
public class Lagertha {
	
	private Minesweeper game;
	
	private Status status;
	private String message;
	
	private int[][] informationMap;	
	private ArrayList<Pair> bombsFound;

	public Lagertha(Minesweeper game) {
		this.game = game;
		bombsFound = new ArrayList<>();
		this.setStatus(Status.HAPPY, "I am happy because you choosed to let me play!");
	}
	
	public void readInformations() {			
		// will iterate around disabled buttons and gain informations about
		// the enabled buttons
		informationMap = game.getVisibleContent();
		//ArrayList<Pair> list = null;
		ArrayList<Pair> neighbors = null;
		
		if (bombsFound != null) {
			for (Pair p : bombsFound) {
				informationMap[p.getFirst()][p.getSecond()] = game.FLAG;
				neighbors = game.getNeighbors(p.getFirst(), p.getSecond(), false);
				for (Pair v : neighbors) {
					if (!bombsFound.contains(v))
						if (informationMap[v.getFirst()][v.getSecond()] >= 1)
							informationMap[v.getFirst()][v.getSecond()]--;
				}
			}
		}
	}
	
	public Status getStatus() {
		return status;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setStatus(Status status) {
		this.status = status;
		game.fireLagerthaPanelChanges(this);
	}
	
	public void setStatus(Status status, String message) {
		this.status = status;
		this.message = message;
		game.fireLagerthaPanelChanges(this);
	}
	
	public void doFirstMove() {
		// Since at first move we don't know anything about the board
		// we will chose a random place to reveal it
		
		int x = (int)(Math.random() * game.getWidth());
		int y = (int)(Math.random() * game.getHeight());

		game.markButton(x, y, Color.GREEN);
		this.setStatus(Status.IDEA, "First move is always random! :)");
		if (!game.reveal(x, y)) {
			this.setStatus(Status.HARDONE, "I had bad luck! Let me play one more game!");
			return;
		}
		
		play();
	}
	
	public void play() {
		boolean replay;
		boolean finished = false;
		while (!finished) {
			replay = false;
			game.clearAllMarks();
			this.setStatus(Status.THINKING, "I'm thinking...");
			
			// We will find the best moves by their order
			// Firstly, best move will be the obvious ones, 
			// like the ones which are undiscovered and have all neighbors discovered
			
			boolean isBomb;
			
			readInformations();
			outerloop1:
			for (int i = 0; i < informationMap.length; ++i) {
				for (int j = 0; j < informationMap[0].length; j++) {
					if (informationMap[i][j] < 0) {
						// if this was not discovered
						if (!game.hasUnknownNeighbors(i, j)) {
							// if he has all his neighbors discovered
							isBomb = true;
							ArrayList<Pair> neighbors = game.getNeighbors(i, j, false);
							
							for (Pair p : neighbors) {
								if (informationMap[p.getFirst()][p.getSecond()] == 0) {
									isBomb = false;
									break;
								}
							}
							
							if (isBomb && !bombsFound.contains(new Pair(i, j))) {
								game.markButton(i, j, Color.RED);
								this.setStatus(Status.IDEA, "I found (" + i + ", " + j + ") as a bomb!");
								// it is a bomb, so let's flag it
								bombsFound.add(new Pair(i, j));
								game.flagSetter(i, j);
								// play again with the new board
								readInformations();
								replay = true;
								break outerloop1;
							} else {
								if (!bombsFound.contains(new Pair(i, j)) && informationMap[i][j] < 0) {
									game.markButton(i, j, Color.GREEN);
									this.setStatus(Status.IDEA, "I found (" + i + ", " + j + ") as a safe place!");
									// if it is not, reveal it
									if (!game.reveal(i, j)) {
										this.setStatus(Status.HARDONE, "I failed! I'm so-so-sorry!");
										return;
									}
									// play again with the new board
									readInformations();
									replay = true;
									break outerloop1;
								}
							}
						}
					}
				}
			}
			
			if (replay)
				continue;
			
			// Secondly, we're interested in the places that are discovered
			// and just 1 neighbor of them is undiscovered
			ArrayList<Pair> neighbours = null;
			outerloop2:
			for (int i = 0; i < informationMap.length; ++i) {
				for (int j = 0; j < informationMap[0].length; j++) {
					if (informationMap[i][j] >= 0) {
						// if this was discovered
						neighbours = game.getNeighbors(i, j, true);
						if (neighbours.size() == 1) {
							// if he has only 1 neighbor undiscovered
							if (informationMap[i][j] == 0) {
								// if he is 0
								Pair p = neighbours.get(0);
								int x = p.getFirst();
								int y = p.getSecond();
								if (!bombsFound.contains(new Pair(x, y))) {
									// the reason why this is a safe place is the same as above
									game.markButton(x, y, Color.GREEN);
									this.setStatus(Status.IDEA, "I found (" + x + ", " + y + ") as a safe place!");
									if (!game.reveal(x, y)) {
										this.setStatus(Status.HARDONE, "I failed! I'm so-so-sorry!");
										return;
									}

									// play again with the new board
									readInformations();
									replay = true;
									break outerloop2;
								}
							} else if (informationMap[i][j] >= 1) {
								// if it is larger or equal than 1 it means nearby it are some
								// bombs and because we decrement informationMap every time we find a bomb
								// it means that his neighbor is a bomb
								
								Pair p = neighbours.get(0);
								if (!bombsFound.contains(p)) {
									game.markButton(p.getFirst(), p.getSecond(), Color.RED);
									this.setStatus(Status.IDEA, "I found (" + p.getFirst() + ", " + p.getSecond() + ") as a bomb!");
									bombsFound.add(p);
									game.flagSetter(p.getFirst(), p.getSecond());

									// play again with the new board
									readInformations();
									replay = true;
									break outerloop2;
								}
							}
						}
					}
				}
			}
			
			if (replay)
				continue;
			
			// Third, we need to check if the game is done
			if (game.getGameStatus() != 0) {
				finished = true;
				break;
			} 
			
			// Lastly, if no one of above cases works, it means either the game is done
			// either we have not sufficient informations to reveal a new place
			// so we will need to randomly pick one
			int x;
			int y;
			
			do {
				x = (int)(Math.random() * game.getWidth());
				y = (int)(Math.random() * game.getHeight());
			} while (bombsFound.contains(new Pair(x, y)) || informationMap[x][y] >= 0);
			
			game.markButton(x, y, Color.GREEN);
			this.setStatus(Status.HARDONE, "I have to randomly pick again! :(");
			if (!game.reveal(x, y)) {
				this.setStatus(Status.HARDONE, "I failed! I'm so-so-sorry!");
				return;
			}
		}

		if (game.getGameStatus() == 1) {
			this.setStatus(Status.HAPPY, "I finished the game!");
			game.win();
		} else {
			this.setStatus(Status.HARDONE, "I put so many flags! I am so-so-sorry!");
			game.die();
		}
	}
}
