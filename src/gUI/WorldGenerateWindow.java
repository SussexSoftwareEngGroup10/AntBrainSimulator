package gUI;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import utilities.ErrorEvent;

/**
 * This class displays the window which allows the user to specify how a world 
 * is generated. The user can either pick a contest style mode, or select their 
 * own parameters for world generation.
 * 
 * @author wjs25
 */
public class WorldGenerateWindow {
	MainWindow mainWindow; //The parent window
	
	//Swing components which need to be accessed by the button listeners
	JFrame window;
	JRadioButton contestBtn;
	JRadioButton standardBtn;
	JLabel rowsSelectLbl;
	JLabel colsSelectLbl;
	JLabel rocksSelectLbl;
	JTextField rowsSelectText;
	JTextField colsSelectText;
	JTextField rocksSelectText;
	
	/**
	 * Constructs a new instance of this window.  This draws the window to the 
	 * screen.
	 * 
	 * @param mainWindow The main window which is the parent of this.
	 */
	public WorldGenerateWindow(MainWindow mainWindow) {
		this.mainWindow = mainWindow;
		
		drawGUI();
	}
	
	/*
	 * Method which draws the GUI elements.
	 */
	private void drawGUI() {
		//Set up the JFrame
		this.window = new JFrame("World Generator");
		this.window.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		Container pane = this.window.getContentPane();
		pane.setLayout(new BorderLayout());
		
		//Description panel to display advice to the user
		JPanel descriptionPanel = new JPanel();
		descriptionPanel.setLayout(new FlowLayout());
		
		JLabel descriptionLbl = 
				new JLabel("Choose whether you want to play on a standard " +
						   "tournament world, or if you would like to " +
						   "specify your own world parameters.");
		descriptionPanel.add(descriptionLbl);
		pane.add(descriptionPanel, BorderLayout.NORTH);
		
		//Panel to give radio buttons used for selecting either a contest style
		//world, or a custom world
		JPanel radioBtnPanel = new JPanel();
		radioBtnPanel.setLayout(new FlowLayout());
		
		//Create the radio buttons, and group them
		ButtonGroup radioBtnGroup = new ButtonGroup();
		this.contestBtn = new JRadioButton("Contest Style World", true);
		this.contestBtn.addItemListener(new ContestBtnListener());
		this.standardBtn = new JRadioButton("Custom World", false);
		radioBtnGroup.add(this.contestBtn);
		radioBtnGroup.add(this.standardBtn);
		//Add the radio buttons to the JPanel, and then the window
		radioBtnPanel.add(this.contestBtn);
		radioBtnPanel.add(this.standardBtn);
		pane.add(radioBtnPanel, BorderLayout.CENTER);
		
		//Panel to hold the input areas for the custom worl parameter, as well 
		//as the generate world button
		JPanel worldParamsAndGeneratePanel = new JPanel();
		worldParamsAndGeneratePanel.setLayout(new BorderLayout());
		
		//The panel holding the parameter inputs
		JPanel worldParametersPanel = new JPanel();
		worldParametersPanel.setLayout(new FlowLayout());
		
		//Panel for selecting number of rows, made up of a descriptive label,
		//and a text field for the user to enter the desired value
		JPanel rowsSelectPanel = new JPanel();
		rowsSelectPanel.setLayout(new FlowLayout());
		
		this.rowsSelectLbl = new JLabel("Number of rows: ");
		this.rowsSelectLbl.setEnabled(false);
		this.rowsSelectText = new JTextField(5);
		this.rowsSelectText.setEnabled(false);
		rowsSelectPanel.add(this.rowsSelectLbl);
		rowsSelectPanel.add(this.rowsSelectText);
		
		worldParametersPanel.add(rowsSelectPanel);
		
		//Panel for selecting number of columns
		JPanel colsSelectPanel = new JPanel();
		colsSelectPanel.setLayout(new FlowLayout());
		
		this.colsSelectLbl = new JLabel("Number of columns: ");
		this.colsSelectLbl.setEnabled(false);
		this.colsSelectText = new JTextField(5);
		this.colsSelectText.setEnabled(false);
		rowsSelectPanel.add(this.colsSelectLbl);
		rowsSelectPanel.add(this.colsSelectText);
		
		worldParametersPanel.add(colsSelectPanel);
		
		//Panel for selecting number of rocks
		JPanel rocksSelectPanel = new JPanel();
		rocksSelectPanel.setLayout(new FlowLayout());
		
		this.rocksSelectLbl = new JLabel("Number of rocks: ");
		this.rocksSelectLbl.setEnabled(false);
		this.rocksSelectText = new JTextField(5);
		this.rocksSelectText.setEnabled(false);
		rocksSelectPanel.add(this.rocksSelectLbl);
		rocksSelectPanel.add(this.rocksSelectText);
		
		worldParametersPanel.add(rocksSelectPanel);
		worldParamsAndGeneratePanel.add(
					worldParametersPanel, BorderLayout.NORTH);
		//Panel to display the two buttons, to generate the world or cancel, at
		//the bottom of the window
		JPanel generatePanel = new JPanel();
		generatePanel.setLayout(new FlowLayout());
		JButton generateBtn = new JButton("Generate");
		generateBtn.addActionListener(new GenerateListener());
		JButton cancelBtn = new JButton("Cancel");
		cancelBtn.addActionListener(new CloseListener());
		generatePanel.add(generateBtn);
		generatePanel.add(cancelBtn);
		worldParamsAndGeneratePanel.add(generatePanel, BorderLayout.CENTER);
		
		pane.add(worldParamsAndGeneratePanel, BorderLayout.SOUTH);
		
		//set window properties, and draw to screen
		this.window.pack();
		this.window.setLocationRelativeTo(null);
		this.window.setResizable(false);
		this.window.setVisible(true);
	}
	
	 
	 /**
	  * Attached to the contest radio button to turn on and off the
	  * parameter selection depending on if it is selected or not.
	  * 
	  * @author wjs25
	  */
	public class ContestBtnListener implements ItemListener {
		
		/**
		 * Turns on or off the parameter selection text fields and labels
		 * based on the state of the contest button.
		 * 
		 * @param e The triggering event.
		 */
		@Override
		public void itemStateChanged(ItemEvent e) {
			//If contest button is selected, deselect parameter selection 
			//options otherwise, enable them
			if (e.getStateChange() == ItemEvent.SELECTED) {
				WorldGenerateWindow.this.rowsSelectLbl.setEnabled(false);
				WorldGenerateWindow.this.colsSelectLbl.setEnabled(false);
				WorldGenerateWindow.this.rocksSelectLbl.setEnabled(false);
				
				WorldGenerateWindow.this.rowsSelectText.setEnabled(false);
				WorldGenerateWindow.this.colsSelectText.setEnabled(false);
				WorldGenerateWindow.this.rocksSelectText.setEnabled(false);
			} else {
				WorldGenerateWindow.this.rowsSelectLbl.setEnabled(true);
				WorldGenerateWindow.this.colsSelectLbl.setEnabled(true);
				WorldGenerateWindow.this.rocksSelectLbl.setEnabled(true);
				
				WorldGenerateWindow.this.rowsSelectText.setEnabled(true);
				WorldGenerateWindow.this.colsSelectText.setEnabled(true);
				WorldGenerateWindow.this.rocksSelectText.setEnabled(true);
			}
		}
	}
	
	/**
	 * Listener to attach to the generate button.
	 * 
	 * @author wjs25
	 */
	public class GenerateListener implements ActionListener {
		
		/**
		 * Will either generate a contest style world or a custom world 
		 * depending on what is selected.
		 * 
		 * @param e The triggering event.
		 */
		@Override
		public void actionPerformed(@SuppressWarnings("unused") ActionEvent e) {
			if (WorldGenerateWindow.this.contestBtn.isSelected()) {
				try {
					WorldGenerateWindow.this.mainWindow.setupNewContestWorld();
					WorldGenerateWindow.this.window.setVisible(false);
				} catch (ErrorEvent eE) {
					GUIErrorMsg.displayErrorMsg(
							"Error in generating contest world!");
				}
			} else {
				try {
					//Try converting parameters to integers
					int rows = Integer.parseInt(
							WorldGenerateWindow.this.rowsSelectText.getText());
					int cols = Integer.parseInt(
							WorldGenerateWindow.this.colsSelectText.getText());
					int rocks = Integer.parseInt(
							WorldGenerateWindow.this.rocksSelectText.getText());
					
					//Validate user input complies to this games rules
					if (rows > 140 || cols > 140) {
						GUIErrorMsg.displayErrorMsg("World dimensions must " +
													"not exceed 140!");
					} else if (rows < 1 || cols < 1 || rocks < 1) {
						GUIErrorMsg.displayErrorMsg("Values cannot be " +
													"negative!");
					} else {
						try {
							WorldGenerateWindow.this
									.mainWindow.setupNewWorldStandardWorld(
									rows, cols, rocks);
							WorldGenerateWindow.this.window.setVisible(false);
						} catch (ErrorEvent eE) {
							GUIErrorMsg.displayErrorMsg(
									"World is not a legal standard world!");
						}
					}
				} catch (NumberFormatException nFE) {
					GUIErrorMsg.displayErrorMsg(
							"Parameters must be an integer!");
				}
			}
		}
	}
}
