package game;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 * @author Vali
 *
 */
public class GameButton implements ActionListener {

	private Minesweeper game;
	private int x;
	private int y;
	private ArrayList<JButton> list;
	
	public GameButton(Minesweeper game, int x, int y, ArrayList<JButton> list) {
		this.game = game;
		this.x = x;
		this.y = y;
		this.list = list;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		game.buttons[x][y].setEnabled(false);
		
		if (game.content[x][y] == game.MINE) {
			game.buttons[x][y].setIcon(new ImageIcon("resources/bomb_icon.png"));
			game.die();
		} else {			
			// Reveal 0 neighbours
			Queue<Pair> queue = new LinkedList<>();
			int dx[] = {0, 1, 0, -1 };
			int dy[] = {1, 0, -1, 0 };
			boolean[][] viz = new boolean[game.width][game.height];
			queue.add(new Pair(x, y));
			viz[x][y] = true;
			while (!queue.isEmpty()) {
				Pair p = queue.remove();
				
				int ax = p.getFirst();
				int ay = p.getSecond();
				game.buttons[ax][ay].setEnabled(false);
				game.buttons[ax][ay].setText("");
				
				for (int i = 0; i < 4; ++i) {
					int cx = p.getFirst() + dx[i];
					int cy = p.getSecond() + dy[i];
					
					if (cx < 0 || cy < 0 || cx >= game.width || cy >= game.height)
						continue;
					
					if (!viz[cx][cy]) {
						viz[cx][cy] = true;
						if (game.content[cx][cy] == 0) {
							// zero neighbour
							queue.add(new Pair(cx, cy));
						} else if (game.content[cx][cy] != game.MINE) {
							if (list.contains(game.buttons[cx][cy])) {
								game.buttons[cx][cy].setIcon(null);
								list.remove(game.buttons[cx][cy]);
							}
							
							game.buttons[cx][cy].setText(game.content[cx][cy] + "");	
							game.buttons[cx][cy].setEnabled(false);
						}
					}
					
					if (!viz[cx][cy] && game.content[cx][cy] != game.MINE && game.content[cx][cy] != 0) {
						viz[cx][cy] = true;
					}
				}
			}
			
			if (game.content[x][y] != 0)
				game.buttons[x][y].setText(game.content[x][y] + "");
		}
	}
	
}
