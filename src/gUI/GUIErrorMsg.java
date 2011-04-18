package gUI;

import javax.swing.JOptionPane;
/**
 * Simple class used to display error messages to the user.
 * 
 * @author will
 */
public class GUIErrorMsg {
	/**
	 * Pops up an error message box.
	 * 
	 * @param msg The error message to display.
	 */
	public static void displayErrorMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "Error!",
			    JOptionPane.ERROR_MESSAGE);
	}
}
