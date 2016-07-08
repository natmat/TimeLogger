package tasklogger;

import javax.swing.SwingUtilities;

public class TaskLogger {
	private static TLController controller;
	private static TLView view;
	private static TLModel model;

	public static void main(String[] args) {    
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	protected static void createAndShowGUI() {
		model = TLModel.getInstance();
		controller = TLController.getInstance();
		view = TLView.getInstance();
		
		controller.setView(view);
		controller.setModel(model);
		
		view.setTitle("Task tasker");   
		view.setVisible(true);
	}
}
