package tasklogger;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

public class TaskButton extends JButton implements PropertyChangeListener {

	private static final long serialVersionUID = -9193221835511157635L;
	private int taskID;
	private String taskName;
	final private Color redColor = new Color(255,99,71); 
	final private Color greenColor = new Color(124,252,0);
	final private Color yellowColor = new Color(255,255,51);

	public TaskButton(final int id) {
		super();
		taskID = id;
		taskName = TLModel.getTaskName(taskID);
		setText(taskName);
		setHorizontalAlignment(SwingConstants.LEFT);

		setActionCommand("taskButton");
		addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				if ((evt.getModifiers() & ActionEvent.CTRL_MASK) > 0) {
					final String dialogString = "Enter new task name";
					String taskName = JOptionPane.showInputDialog(null,
							"Enter new task name", "Edit task name",
							JOptionPane.QUESTION_MESSAGE);
					if (TLUtilities.isValidName(taskName, dialogString)) {
						TLModel.setTaskName(taskID, taskName);
						setText(taskName);
					}
				} else if ((evt.getModifiers() & ActionEvent.ALT_MASK) > 0) {
					int dialogResult = JOptionPane.showConfirmDialog(null,
							"Delete task " + TaskButton.this.taskName + "?",
							"Delete?", JOptionPane.YES_NO_OPTION);
					if (dialogResult == JOptionPane.YES_OPTION) {
						TLModel.deleteTask(taskID);
					}
				} else {
					TLController.taskButtonPressed(taskID);
				}
			}
		});

		TLModel.addPropertyChangeListener(this);
		stop();

	}

	public void start() {
		setButtonColor(yellowColor);
		repaint();
	}

	public void stop() {
		setButtonColor(greenColor);
		repaint();
	}

	private void setButtonColor(Color buttonColor) {
		setBackground(buttonColor);
		setOpaque(true);
		setBorderPainted(true);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// TODO
	}
}
