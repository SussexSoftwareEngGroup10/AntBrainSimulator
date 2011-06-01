package gUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * A listener class to be attached to close buttons to close the window it is
 * a part of.
 * 
 * @author wjs25
 */
class CloseListener implements ActionListener {

	/**
	 * Closes the window.
	 * 
	 * @param e The triggering event.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		//Get the button
		JButton triggeringBtn = (JButton) e.getSource();
		//Get the frame the button was on
		JFrame window = (JFrame) SwingUtilities.getRoot(triggeringBtn);
		window.setVisible(false);
	}
}
