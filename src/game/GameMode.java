/**
 * 
 */
package game;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

/**
 * @author Vali
 *
 */
public class GameMode {
	
	public static void main(String[] args) {
		final int width = 40;
		final int height = 40;
		final int bombs = (int)(0.75 * (width + height));
		
		JFrame frame = new JFrame("Minesweeper");
		frame.setSize(400, 400);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		
		JPanel headerPanel = new JPanel(new BorderLayout());
		
		JLabel title = new JLabel("MINESWEEPER");
		title.setFont(new Font("Ubuntu", Font.PLAIN, 32));
		title.setHorizontalAlignment(SwingConstants.CENTER);
		headerPanel.add(title, BorderLayout.NORTH);
		
		JLabel subtitle = new JLabel("GAME MODE SELECT");
		subtitle.setFont(new Font("Ubuntu", Font.PLAIN, 16));
		subtitle.setHorizontalAlignment(SwingConstants.CENTER);
		headerPanel.add(subtitle, BorderLayout.SOUTH);
		
		frame.add(headerPanel, BorderLayout.NORTH);
		
		JPanel middlePanel = new JPanel(new GridBagLayout());
		middlePanel.setBorder(
			BorderFactory.createCompoundBorder(
					new EmptyBorder(25, 25, 25, 25), 
					new MatteBorder(1, 1, 1, 1, Color.GRAY)));
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(5, 5, 5, 5);
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.gridx = 1;
		constraints.gridy = 0;
		
		JButton playAlone = new JButton("Play solo");		
		playAlone.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new Minesweeper(width, height, bombs, false);
			}
		});
		middlePanel.add(playAlone, constraints);
		
		JButton lagerthaPlay = new JButton("Let Lagertha play");
		lagerthaPlay.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//new Lagertha(new Minesweeper(20, 20, 30, true)).doFirstMove();
				new Minesweeper(width, height, bombs, true);
				//bot.doFirstMove();
			}
		});
		constraints.gridy = 2;
		constraints.anchor = GridBagConstraints.PAGE_END;
		middlePanel.add(lagerthaPlay, constraints);
		
		frame.add(middlePanel, BorderLayout.CENTER);
		
		JLabel credits = new JLabel("© Valentin Deaconu, OOP Project, 2018 - 2019");
		credits.setFont(new Font("Ubuntu", Font.PLAIN, 16));
		credits.setHorizontalAlignment(SwingConstants.CENTER);
		
		frame.add(credits, BorderLayout.SOUTH);
		
		// Minesweeper game = new Minesweeper(width, height, bombs);
		

		frame.setVisible(true);
	}

}
