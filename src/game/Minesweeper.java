package game;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.MatteBorder;

import laghertha.*;


/**
 * 
 */

/**
 * @author Vali
 *
 */
public class Minesweeper {
	public static void main(String[] args) {
		new Lagertha(new Minesweeper(20, 20, 30, true)).doFirstMove();
	}
	/*
	 * Game design components
	 */
	protected JFrame frame;
	protected JButton[][] buttons;
	protected int[][] content;
	private JPanel grid;
	
	/**
	 * Lagertha's panel design components
	 */
	private Lagertha bot;
	private boolean withLagertha;
	private JPanel LagerthaPanel;
	private JLabel LagerthaFace;
	private JLabel LagerthaSpeech;
	private JLabel bombsCount;
	
	/**
	 * Game properties
	 */
	protected int width;
	protected int height;
	protected int bombsNumber;
	private boolean isGameRunning;
	
	public final int MINE = -1;
	public final int FLAG = -2;
	
	/**
	 * Used for MouseAdapter flagSetter
	 */
	private ArrayList<JButton> flagList;
	
	/**
	 * Used for Lagertha's marks
	 */
	private ArrayList<JButton> markedButtons;
	
	/**
	 * Advanced constructor, for explicit properties
	 * @param width
	 * @param height
	 * @param bombsNumber the number of randomly placed bombs
	 */
	public Minesweeper(int width, int height, int bombsNumber, boolean withLagertha) {
		this.width = width;
		this.height = height;
		this.bombsNumber = bombsNumber;
		this.withLagertha = withLagertha;
		frame = new JFrame();
		
		initialize();
	}

	/**
	 * Simple constructor, with basic options 20x20 cells with 30 randomly placed bombs
	 */
	public Minesweeper(boolean withLagertha) {
		this(20, 20, 30, false);
	}
	
	/**
	 * @return width
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * @return height
	 */
	public int getHeight() {
		return height;
	}
	
	/**
	 * Used by Lagertha to add listener on mouse for suggestions
	 * @return frame the frame of the game
	 */
	public JFrame getFrame() {
		return frame;
	}
	
	/**
	 * 
	 * @param bot the Lagertha bot
	 */
	public void setLagerthaBot(Lagertha bot) {
		this.bot = bot;
	}
	
	/**
	 * Used by Lagertha for her suggestions
	 * @return buttons the buttons two dimensional array
	 */
	public JButton[][] getGameButtons() {
		return buttons;
	}
	
	/**
	 * Board initialize function
	 */
	public void initialize() {		
		this.isGameRunning = true;
		flagList = new ArrayList<>();
		markedButtons = new ArrayList<>();
		
		frame.setTitle("Minesweeper");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.setResizable(true);
		
		frame.setSize(1280, 1024);
		
		// Let's make it fullscreen
		//frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		//frame.setUndecorated(true);
		
		bombsCount = new JLabel("Bombs discovered: 0/" + bombsNumber);
		bombsCount.setHorizontalAlignment(SwingConstants.CENTER);
		bombsCount.setFont(new Font("Ubuntu", Font.PLAIN, 18));
		frame.getContentPane().add(bombsCount, BorderLayout.NORTH);
		
		grid = new JPanel(new GridLayout(20, 20));
		
		buttons = new JButton[width][height];
		content = new int[width][height];
		
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				buttons[i][j] = new JButton();
				buttons[i][j].addActionListener(new GameButton(this, i, j, flagList));
				buttons[i][j].addMouseListener(new FlagSetter(buttons[i][j]));
				buttons[i][j].setFont(new Font("Ubuntu", Font.PLAIN, 18));
				content[i][j] = 0;
				grid.add(buttons[i][j]);
			}
		}
		
		distributeBombs(bombsNumber);		
		
		frame.getContentPane().add(grid, BorderLayout.CENTER);
		
		if (withLagertha) {			
			LagerthaPanel = new JPanel();
			LagerthaPanel.setLayout(new GridBagLayout());
			
			LagerthaFace = new JLabel();
			LagerthaFace.setIcon(new ImageIcon("resources/presenting.png"));
			LagerthaFace.setMinimumSize(new Dimension(300, 300));
			LagerthaFace.setPreferredSize(new Dimension(300, 300));
	
			LagerthaSpeech = new JLabel("Hello!");
			LagerthaSpeech.setHorizontalAlignment(SwingConstants.CENTER);
			LagerthaSpeech.setFont(new Font("Ubuntu", Font.PLAIN, 14));
			LagerthaSpeech.setMinimumSize(new Dimension(300, 100));
			LagerthaSpeech.setPreferredSize(new Dimension(300, 100));
			
			JSplitPane spane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, LagerthaFace, LagerthaSpeech);	
			
			/*JButton startBot = new JButton("Let's go!");
			startBot.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					bot.doFirstMove();
				}
				
			});
			
			JSplitPane spane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, spane, startBot);
			*/
			
			LagerthaPanel.add(spane);
			
			frame.getContentPane().add(LagerthaPanel, BorderLayout.EAST);
		
			// Initialize the bot
			//this.bot = new Lagertha(this);
		}
		
		frame.setVisible(true);
	}
	
	/**
	 * Method for randomly place the bombs on the map
	 * @param bombs the bomb number
	 */
	public void distributeBombs(int bombs) {
		ArrayList<Pair> elem = new ArrayList<>();
		
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				elem.add(new Pair(i, j));
			}
		}
		
		int cnt = 0;
		do {
			int x = (int)(Math.random() * width);
			int y = (int)(Math.random() * height);
			Pair p = new Pair(x, y);
			if (elem.contains(p)) {
				content[x][y] = MINE;
				increaseNeighbours(x, y);
				++cnt;
				elem.remove(p);
			}
		} while (cnt < bombs);
	}
	
	/**
	 * Method that increase the value of all neighbours of a point
	 * @param x
	 * @param y
	 */
	public void increaseNeighbours(int x, int y) {
		int dx[] = {0, 1, 1, 1, 0, -1, -1, -1};
		int dy[] = {1, 1, 0, -1, -1, -1, 0, 1};
		
		for (int i = 0; i < 8; ++i) {
			int cx = x + dx[i];
			int cy = y + dy[i];
			
			if (!isInsideBoard(cx, cy))
				continue;
			
			if (content[cx][cy] == MINE)
				continue;
			
			content[cx][cy]++;
		}
	}

	/**
	 * Method to reset the game
	 */
	public void reset() {
		frame.getContentPane().removeAll();
		initialize();
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to stop the game by dying
	 */
	public void die() {
		this.isGameRunning = false;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				buttons[i][j].setEnabled(false);
				if (content[i][j] != 0 && content[i][j] != MINE)
					buttons[i][j].setText(content[i][j] + "");
				
				if (content[i][j] == MINE)
					buttons[i][j].setIcon(new ImageIcon("resources/bomb_icon.png"));
			}
		}
		
		
		
		/*int dialog = JOptionPane.showConfirmDialog(
				frame, 
				"You died. Do you want to play again?", 
				"Game over", 
				JOptionPane.YES_NO_CANCEL_OPTION, 
				0, 
				new ImageIcon("resources/game_over.png")); // icon
		
		if (dialog == JOptionPane.YES_OPTION) {
			reset();
		} else if (dialog == JOptionPane.NO_OPTION) {
			frame.dispose();
		}*/
	}
	
	/**
	 * Method to stop the game by finishing it
	 */
	public void win() {
		this.isGameRunning = false;
		/*int dialog = JOptionPane.showConfirmDialog(
				this.frame, 
				"Game finished. Do you want to play again?", 
				"Task completed", 
				JOptionPane.YES_NO_CANCEL_OPTION, 
				0, 
				new ImageIcon("resources/task_complete.png")); // icon
		
		if (dialog == JOptionPane.YES_OPTION) {
			reset();
		} else if (dialog == JOptionPane.NO_OPTION) {
			frame.dispose();
		}*/
	}
	
	/**
	 * Method to reveal a point
	 * @param x coordinate
	 * @param y coordinate
	 * @return if the game is still running or it is not
	 */
	public boolean reveal(int x, int y) {
		buttons[x][y].doClick();		
		return isGameRunning;
	}
	
	/**
	 * Getting the visible information of the board
	 * @return two dimensional array with visible content of the board
	 */
	public int[][] getVisibleContent() {
		int[][] info = new int[width][height];
		
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (!buttons[i][j].isEnabled()) {
					info[i][j] = content[i][j];
				} else {
					info[i][j] = -1; // Unknown
				}
			}
		}
		
		return info;
	}
	
	/**
	 * Method that returns a list of all or only unknown neighbor of a point
	 * @param x coordinate
	 * @param y coordinate
	 * @param unknownOnly
	 * @return
	 */
	public ArrayList<Pair> getNeighbors(int x, int y, boolean unknownOnly) {
		int dx[] = {0, 1, 1, 1, 0, -1, -1, -1};
		int dy[] = {1, 1, 0, -1, -1, -1, 0, 1};
		ArrayList<Pair> list = new ArrayList<>();
		
		for (int i = 0; i < 8; ++i) {
			int cx = x + dx[i];
			int cy = y + dy[i];
			if (!isInsideBoard(cx, cy))
				continue;
			
			if (unknownOnly) {
				if (!flagList.contains(buttons[cx][cy]))
					if (buttons[cx][cy].isEnabled())
						list.add(new Pair(cx, cy));
			} else {
				list.add(new Pair(cx, cy));
			}
		}
		
		return list;
	}
	
	/**
	 * Method that checks if a point has unknown neighbors
	 * @param x coordinate
	 * @param y coordinate
	 * @return if point (x, y) has unknown neighbors
	 */
	public boolean hasUnknownNeighbors(int x, int y) {
		ArrayList<Pair> neighbors = getNeighbors(x, y, true);
		
		if (neighbors.size() != 0)
			return true;
		
		return false;
	}
	
	/**
	 * Method that place a flag on a button by its coordinates
	 * @param x coordinate
	 * @param y coordinate
	 */
	public void flagSetter(int x, int y) {
		if (buttons[x][y].isEnabled()) {
			if (flagList.contains(buttons[x][y])) {
				buttons[x][y].setIcon(null);
				flagList.remove(buttons[x][y]);
			}
			else {
				buttons[x][y].setIcon(new ImageIcon("resources/flag_icon.png"));
				flagList.add(buttons[x][y]);
			}
			
			bombsCount.setText("Bombs discovered: " + flagList.size() + "/" + bombsNumber);
		}
	}
	
	/**
	 * Method that place a flag on a button
	 * @param button the button to put flag on
	 */
	public void flagSetter(JButton button) {
		if (button.isEnabled()) {
			if (flagList.contains(button)) {
				button.setIcon(null);
				flagList.remove(button);
			}
			else {
				button.setIcon(new ImageIcon("resources/flag_icon.png"));
				flagList.add(button);
			}
			
			bombsCount.setText("Bombs discovered: " + flagList.size() + "/" + bombsNumber);
		}
	}
	
	/**
	 * Method used by Lagertha to see if player marked with flag a pont
	 * @param x coordinate
	 * @param y coordinate
	 * @return true if the point is marked 
	 */
	public boolean isFlagged(int x, int y) {
		return flagList.contains(buttons[x][y]);
	}
	
	class FlagSetter extends MouseAdapter {
		private JButton button;
		public FlagSetter(JButton button) {
			this.button = button;
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			if (SwingUtilities.isRightMouseButton(e)) {
				flagSetter(button);
			}	
		}
	}
	
	/**
	 * Method that marks a button with a color to be more visible
	 * @param x coordinate
	 * @param y coordinate
	 * @param c color
	 */
	public void markButton(int x, int y, Color c) {
		buttons[x][y].setBorder(new MatteBorder(5, 5, 5, 5, c));
		markedButtons.add(buttons[x][y]);
	}
	
	/** 
	 * Method that remove all marks
	 */
	public void clearAllMarks() {
		for (JButton b : markedButtons) {
			b.setBorder(new MatteBorder(1, 1, 1, 1, Color.GRAY));
		}
		
		markedButtons = new ArrayList<>();
	}
	
	/**
	 * Method that returns the game status code
	 * 1 for game is done
	 * -1 for too many flags placed
	 * 0 for too little flags placed
	 * @return the code
	 */
	public int getGameStatus() {
		if (flagList.size() == bombsNumber)
			return 1;
		
		if (flagList.size() > bombsNumber)
			return -1;
		
		return 0;
	}
	
	/**
	 * Method that checks if a point is inside the board
	 * @param x coordinate
	 * @param y coordinate
	 * @return if the point is inside the board
	 */
	public boolean isInsideBoard(int x, int y) {
		if (x < 0 || y < 0 || x >= this.width || y >= this.height)
			return false;
		return true;
	}
	
	/**
	 * Method that notify the bot's panel about new changes
	 * @param lagertha the bot
	 */
	public void fireLagerthaPanelChanges(Lagertha lagertha) {
		LagerthaSpeech.setText(lagertha.getMessage());
		
		Status s = lagertha.getStatus();
		switch (s) {
			case NOTHING:
				LagerthaFace.setIcon(new ImageIcon("resources/presenting.png"));					
				break;
			case THINKING:
				LagerthaFace.setIcon(new ImageIcon("resources/thinking.png"));	
				break;
			case IDEA:
				LagerthaFace.setIcon(new ImageIcon("resources/ok.png"));	
				break;
			case HARDONE:
				LagerthaFace.setIcon(new ImageIcon("resources/crying.png"));
				break;
			case HAPPY:
				LagerthaFace.setIcon(new ImageIcon("resources/happy.png"));
				break;	
		}
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
