package gUI;

import java.awt.event.*;
import javax.swing.*;

/**
 * A listener class to be attached to close buttons to close the window it is
 * a part of.
 * 
 * @author will
 */
public class CloseListener implements ActionListener {

	/**
	 * Closes the window.
	 */
	public void actionPerformed(ActionEvent e) {
		//Get the button
		JButton triggeringBtn = (JButton) e.getSource();
		//Get the frame the button was on
		JFrame window = (JFrame) SwingUtilities.getRoot(triggeringBtn);
		window.setVisible(false);
	}
}
