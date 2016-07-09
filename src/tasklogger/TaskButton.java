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
					System.out.println("CTRL pressed");
					editTaskNameView();
				}
				else if ((evt.getModifiers() & ActionEvent.META_MASK) > 0) {					
					System.out.println("CMD pressed");
					TLModel.deleteTask(taskID);
				}
				else {
					TLController.taskButtonPressed(taskID);
				}
			}

			private void editTaskNameView() {
				String dialogString = "Enter new task name";
				String taskName = JOptionPane.showInputDialog(dialogString, getText());
				if (TLUtilities.isValidName(taskName, dialogString)) {
					TLModel.setTaskName(taskID, taskName);
					setText(taskName);
				}
			}
		});
		
		TLModel.addPropertyChangeListener(this);
		stop();

	}

	public void start() {
		setButtonColor(Color.red);
		repaint();
	}

	public void stop() {
		setButtonColor(Color.green);
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
