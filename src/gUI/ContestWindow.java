package gUI;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;

import utilities.IOEvent;
import utilities.Logger;

/**
 * This class displays the window that allows the user to set up a contest.
 * Once the contest has been run, results are displayed.
 * 
 * @author will
 */
public class ContestWindow {
	private int numOfPlayers;
	//Holds the file paths of the ant brains
	private String[] brainPaths;
	
	//Stores arrays of GUI components
	private JButton[] browseBtns;
	private JTextArea[] brainPathLbls;
	
	/**
	 * Constructs a new ContestWindow and draws it to the screen.
	 * 
	 * @param numOfPlayers The number of contest participants.
	 */
	public ContestWindow(int numOfPlayers) {
		this.numOfPlayers = numOfPlayers;
		brainPaths = new String[numOfPlayers];
		drawGUI();
	}
	
	/*
	 * This method draws the elements of the GUI to the screen
	 */
	private void drawGUI() {
		//Set up the main frame with a border layout
		JFrame window = new JFrame("Contest Mode");
		window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Container pane = window.getContentPane();
		pane.setLayout(new BorderLayout());
		
		//Panel to display the grid where each row allows a contestant to
		//upload a brain, and show the wins and losses for that brain
		JPanel brainUploadPanel = new JPanel();
		brainUploadPanel.setLayout(new GridLayout(numOfPlayers + 1, 5, 10, 10));

		//Labels that are the column headings
		JLabel blankLbl1 = new JLabel("");
		JLabel blankLbl2 = new JLabel("");
		JLabel brainLocLbl = new JLabel("Brain Location");
		JLabel winsLbl = new JLabel("Wins");
		JLabel lossesLbl = new JLabel("Losses");
		
		brainUploadPanel.add(blankLbl1);
		brainUploadPanel.add(brainLocLbl);
		brainUploadPanel.add(blankLbl2);
		brainUploadPanel.add(winsLbl);
		brainUploadPanel.add(lossesLbl);
		
		//Arrays of the components to be added on each row
		JLabel[] nameLbls = new JLabel[numOfPlayers];
		browseBtns = new JButton[numOfPlayers];
		brainPathLbls = new JTextArea[numOfPlayers];
		JLabel[] winsLbls = new JLabel[numOfPlayers];
		JLabel[] lossesLbls = new JLabel[numOfPlayers];
		
		//Loop displays each row for uploading a brain for each player
		for (int i = 0; i < numOfPlayers; i++) { 
			nameLbls[i] = new JLabel("Player"  + (i + 1));
			browseBtns[i] = new JButton("Browse");
			browseBtns[i].addActionListener(new brainBrowseListener());
			brainPathLbls[i] = new JTextArea();
			winsLbls[i] = new JLabel("");
			lossesLbls[i] = new JLabel("");
			
			brainUploadPanel.add(nameLbls[i]);
			brainUploadPanel.add(browseBtns[i]);
			brainUploadPanel.add(brainPathLbls[i]);
			brainUploadPanel.add(winsLbls[i]);
			brainUploadPanel.add(lossesLbls[i]);
		}
		
		JScrollPane scrollPanel = new JScrollPane(brainUploadPanel);
		//Only add scroll bars by limiting the preferred size of the scroll panel if the number 
		//of players is above 7 (the amount that fits into 300 pixels).
		if (numOfPlayers > 7) {
			scrollPanel.setPreferredSize(new Dimension(500, 300));
		}
		else {
			//If there won't be scrolling, remove the border of the scroll panel
			scrollPanel.setBorder(null);
		}
		scrollPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		pane.add(scrollPanel, BorderLayout.NORTH);
		
		JPanel goPanel = new JPanel();
		goPanel.setLayout(new FlowLayout());
		JButton goBtn = new JButton("Go");
		JButton cancelBtn = new JButton("Cancel");
		cancelBtn.addActionListener(new CloseListener());
		goPanel.add(goBtn);
		goPanel.add(cancelBtn);
		pane.add(goPanel, BorderLayout.SOUTH);
		
		//Pack the window so that the size varies based on the number of
		//contestants
		window.pack();
		window.setLocationRelativeTo(null);
		window.setResizable(false);
		window.setVisible(true);
	}
	
	/**
	 * Attached to the buttons which need to bring up a file browser window.
	 * 
	 * @author will
	 */
	public class brainBrowseListener implements ActionListener 
	{
		/**
		 * Displays the file chooser box when the browse button is 
		 * clicked, the file path is displayed in the corresponding text box.
		 * 
		 * @param e The triggering event.
		 */
		public void actionPerformed(ActionEvent e)
		{
			//Set the initial directory to the current project dir
			JFileChooser fileChooser = new JFileChooser(
					System.getProperty("user.dir"));
			try {
				//Show the dialog
				fileChooser.showOpenDialog(null);
				String path = "";
				//Get the path from the selected file if one was selected
				File file = fileChooser.getSelectedFile();
				if (file != null) {
					path = file.getAbsolutePath();
					//Validate the file is of the correct format
					if (!path.contains(".brain")) {
						GUIErrorMsg.displayErrorMsg(
								"Invalid file format, .brain file expected.");
					}
					else {
						//Search for the index of the JButton clicked
						JButton clickedBtn = (JButton) e.getSource();
						int index = 0;
						int i = 0;
						while (i < numOfPlayers && index == 0) {
							if (browseBtns[i] == clickedBtn) {
								index = i;
							}
							i++;
						}
						//Update to selected path
						brainPathLbls[index].setText(path);
						brainPaths[index] = path;
					}
				}
				
			}
			//If the user does not have permission to access the file
			catch (SecurityException sE) {
				Logger.log(new IOEvent(
						"Security violation with file!", sE));
			}
		}
	}
}
