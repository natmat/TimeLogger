package tasklogger;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

public class TLView extends JFrame implements PropertyChangeListener, ActionListener {
	private static final long serialVersionUID = 1L;
	private static JPanel mainPanel;
	private JButton newTaskButton;
	private static JPanel controlsPanel;
	private static JPanel taskPanel;
	private static JPanel pomodoroPanel;
	private static JTextField totalTimer;
	private static ArrayList<TaskView> taskViewList;
	private static TLView instance;
	final private Color saveColor = new Color(255, 255, 102);
	final private Color newTaskColor = new Color(255, 153, 51);
	final private Color resetColor = new Color(204,229,255);
	private static JTextArea infoArea;

	public static void main(String[] args) {
		TLView view = TLView.getInstance();
		view.setVisible(true);	
	}
	
	public static TLView getInstance() {
		if (instance == null) {
			instance = new TLView();
		}
		return (instance);
	}

	private TLView() {
		TLModel.addPropertyChangeListener(this);
		taskViewList = new ArrayList<TaskView>();
		setupFrame();
		//		setAlwaysOnTop(true);
		pack();
	}

	private void setupFrame() {
		add(new TLMenu());

		// Draw frame with top and bottom panels.
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		controlsPanel = new JPanel();
		controlsPanel.setLayout(new GridLayout(0, 2));		
		addResetButtonToControls();
		addTotalTimerFieldToControls();
		addNewTaskButtonToControls();
		addSaveButtonToControls();
		mainPanel.add(controlsPanel);

		taskPanel = new JPanel();
		taskPanel.setLayout(new GridLayout(0, 2));
		mainPanel.add(taskPanel);

		pomodoroPanel = new JPanel();
		pomodoroPanel.setLayout(new GridLayout(1, 2));
		addPomodoroToView();
		mainPanel.add(pomodoroPanel);

		infoArea = new JTextArea();
		infoArea.setRows(4);
		JScrollPane infoPane = new JScrollPane(
				infoArea, 
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		mainPanel.add(infoPane);

		Container container = this.getContentPane();
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(screenSize.width*3/4, screenSize.height/4);		
		setResizable(false);
		
		container.add(mainPanel);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				try {
					TLModel.exportCVSFile();
					System.out.println("exportCVSFile()");
					dispose();
					System.exit(0);
				} catch (IOException e) {
					// e.printStackTrace();
					if (JOptionPane.showConfirmDialog(
							null,
							"Export failed.\nQuit anyway?",
							"Export CSV logging", 
							JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
						dispose();
						System.exit(0);
					}
				}
			}
		});

		ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("tasklogger/pomodoro.png"));
		setIconImage(icon.getImage());
	}

	/**
	 * Write info string to the infoArea
	 * @param info string to prefix to infoArea
	 */
	public static void writeInfo(final String info) {
		System.out.println("TLView:" + info);
		if (null != TLView.infoArea) { 	
			TLView.infoArea.setForeground(Color.BLUE);
			TLView.infoArea.append(TLView.infoArea.getLineCount() + ": " + info + "\r\n");
		}
	}

	private void addPomodoroToView() {
		PomodoroTimer pomodoroTimer = PomodoroTimer.getInstance();
		// try {
		// Image img = ImageIO.read(new File("/resources/pomodoro.png"));
		// final URL img = this.getClass().getResource("pomodoro.png");
		// pomodoro.setIcon(new ImageIcon(img));
		// throw(new IOException());
		// } catch (IOException ex) {
		// ex.printStackTrace();
		// }

		//		URL url = getClass().getResource("/pomodoro.png");
		//		System.out.println(url.getPath());

		pomodoroPanel.add(pomodoroTimer.getButton());
		pomodoroPanel.add(pomodoroTimer.getProgressBar());
		mainPanel.add(pomodoroPanel);
	}

	private void addSaveButtonToControls() {
		JButton saveButton = new JButton("Save times to file");
		saveButton.setBackground(saveColor);
		saveButton.setOpaque(true);
		saveButton.setToolTipText("Save tasks and times to dated file");
		saveButton.setHorizontalAlignment(SwingConstants.CENTER);
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TLModel.writeTaskTimesToFile();
			}
		});
		controlsPanel.add(saveButton);
	}

	private void addTotalTimerFieldToControls() {
		totalTimer = new JTextField("00:00:00", 8);
		totalTimer.setHorizontalAlignment(JTextField.CENTER);
		totalTimer.setFont(new Font("monospaced", Font.PLAIN, 24));
		controlsPanel.add(totalTimer);
	}

	private void addResetButtonToControls() {
		JButton resetButton = new JButton("Reset");
		resetButton.setBackground(resetColor);
		resetButton.setOpaque(true);
		resetButton.setHorizontalAlignment(SwingConstants.LEFT);
		resetButton.setToolTipText("Reset to zero all task times");
		resetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!TLView.taskViewList.isEmpty()) {
					TLModel.reset();
				}
				else {
					TLModel.setTotalRunTimeInMs(0);
				}
			}
		});
		controlsPanel.add(resetButton);
	}

	private void addNewTaskButtonToControls() {
		newTaskButton = new JButton("Add New Task");
		newTaskButton.setToolTipText("Add a new task");
		newTaskButton.setHorizontalAlignment(SwingConstants.LEFT);
		newTaskButton.addActionListener(this);
		newTaskButton.setActionCommand("newTaskButtonPressed");
		newTaskButton.setBackground(newTaskColor);
		controlsPanel.add(newTaskButton);
	}

	public static void tickTimers(final TLTask inTask, long taskTimeInMs,
			long totalTimeInMs) {
		setTotalTimerInMs(totalTimeInMs);

		TaskView tv = TaskView.getTaskViewWithId(taskViewList, inTask.getTaskID());
		if (tv != null) {
			tv.getTimer().setText(TLUtilities.getHMSString(taskTimeInMs));
		}
	}

	public static void setTotalTimerInMs(long timeInMs) {
		totalTimer.setText(TLUtilities.getHMSString(timeInMs));
	}

	public static void addTask(int taskID) {
		if (null != TaskView.getTaskViewWithId(taskViewList, taskID)) {
			TLView.writeInfo("Duplicate task");
		} 
		else {
			// Add new task
			TaskView tv = new TaskView(taskID);
			taskViewList.add(tv);
			taskPanel.add(tv.getButton());
			taskPanel.add(tv.getTimer());
			getInstance().pack();
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		System.out.println("TLView PCE:" + evt);
		if (evt instanceof IndexedPropertyChangeEvent) {
			// Process per task pce's
			taskIndexedPropertyChange((IndexedPropertyChangeEvent) evt);
		}
		else {
			if (evt.getPropertyName().startsWith("totalRunTimeInMs")) {
				TLView.setTotalTimerInMs(((Number)evt.getNewValue()).longValue());
			} 
		}
	}

	private void taskIndexedPropertyChange(IndexedPropertyChangeEvent evt) {
		String name = evt.getPropertyName();
		int taskID = evt.getIndex();
		if (name.startsWith("taskStateChange")) {
			TLView.taskEvent(taskID, ((Boolean)evt.getNewValue()).booleanValue());
		} 
		else if (name.startsWith("activeTimeInMs")) {
			TLView.setActiveTimeInMs(taskID, ((Number)evt.getNewValue()).longValue());
		} 
	}

	public static void taskEvent(int taskID, Boolean taskRunning) {
		for (TaskView tv : taskViewList) {
			if (tv.getTaskID() == taskID) {
				if (taskRunning) {
					tv.getButton().start();
				} else {
					tv.getButton().stop();
				}
				return;
			}
		}
	}

	public static void deleteTask(int taskID) {
		for (TaskView tv : taskViewList) {
			if (tv.getTaskID() == taskID) {
				removeTaskViewFromPanel(tv);				
				tv.deleteTask();
				taskViewList.remove(tv);
				tv = null;
				return;
			}
		}
	}

	private static void removeTaskViewFromPanel(final TaskView tv) {
		taskPanel.remove(tv.getButton());
		taskPanel.remove(tv.getTimer());
		taskPanel.revalidate();
		getInstance().pack();
	}

	public static void setActiveTimeInMs(int taskId, long timeInMs) {
		for (TaskView tv : taskViewList) {
			if (tv.getTaskID() == taskId) {
				tv.getTimer().setText(TLUtilities.getHMSString(timeInMs));
			}
		}
	}

	public static Dimension getDimension() {
		Dimension dim = new Dimension();		
		return dim;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals("newTaskButtonPressed")) {
			newTaskButtonPressed();
		} 
		else if (command.equals("taskSelectorComboBox")) {
			String name = TLUtilities.getNewTaskName();
			System.out.println("NewTaskName=" + name);

			TLTask newTask = null;
			if (null != name) {
				newTask = TLModel.newTask(name);
				if (null != newTask) {
					TLView.writeInfo("Adding task " + name);
					addTask(newTask.getTaskID());
					TLController.newTask(name);
					TLController.taskButtonPressed(newTask.getTaskID());
				}
				else {
//					TLView.getInstance().setAlwaysOnTop(false);
					TLView.writeInfo("Task " + name + " already exists");
//					TLView.getInstance().setAlwaysOnTop(true);
				}
			}
		}
		else {
			System.out.println("ERROR");
			(new IOException()).printStackTrace();
		}
	}

	public static void newTaskButtonPressed() {
		TLUtilities.taskSelectorDialog(TLView.instance, TaskLoader.getTaskList());
	}

	public static void addModel(TLModel model) {
		// Add existing model to the view
		setTotalTimerInMs(TLTask.getTotalRunTimeInMs());
		for (TLTask t : model.getTaskArray()) {
			TLView.addTask(t.getTaskID());
		}
	}
}


