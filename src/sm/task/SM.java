package sm.task;

import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import actr.model.Event;
import actr.model.Symbol;
import actr.task.Result;
import actr.task.Task;
import actr.task.TaskButton;
import actr.task.TaskCross;
import sm.task.VisualObject.Colour;
import sm.task.VisualObject.Hieght;
import sm.task.VisualObject.Pattern;
import sm.task.VisualObject.Shape;

public class SM extends Task {
	public enum Phase {
		scan, recall
	};
	private final static int DISPLAY_WIDTH = 800;
	private final static int DISPLAY_HEIGHT = 800;
	private final static int X_CENTER = DISPLAY_WIDTH / 2 - 10 ;
	private final static int Y_CENTER = DISPLAY_HEIGHT / 2 - 60;
	private final static int INNER_RADIUS = 110;
	private final static int OUTER_RADIUS = 220;
	private final static int OFFSET = 40;
	private final static double[] DELAY_TIME = new double[]{0.5,1.0,1.5};
	private final int NUMBER_OF_TRIALS = 60;
	
	
	private static final Point[] LOC_ARRAY = new Point[] {
			new Point(X_CENTER, Y_CENTER-INNER_RADIUS-OFFSET),
			new Point(X_CENTER+INNER_RADIUS-OFFSET , Y_CENTER-INNER_RADIUS+OFFSET),
			new Point(X_CENTER+INNER_RADIUS+OFFSET , Y_CENTER),
			new Point(X_CENTER+INNER_RADIUS-OFFSET , Y_CENTER+INNER_RADIUS-OFFSET),
			new Point(X_CENTER, Y_CENTER+INNER_RADIUS+OFFSET),
			new Point(X_CENTER-INNER_RADIUS+OFFSET , Y_CENTER+INNER_RADIUS-OFFSET),
			new Point(X_CENTER-INNER_RADIUS-OFFSET , Y_CENTER),
			new Point(X_CENTER-INNER_RADIUS+OFFSET , Y_CENTER-INNER_RADIUS+OFFSET),

			new Point(X_CENTER, Y_CENTER-OUTER_RADIUS-OFFSET),
			new Point(X_CENTER+OUTER_RADIUS-OFFSET , Y_CENTER-OUTER_RADIUS+OFFSET),
			new Point(X_CENTER+OUTER_RADIUS+OFFSET , Y_CENTER),
			new Point(X_CENTER+OUTER_RADIUS-OFFSET , Y_CENTER+OUTER_RADIUS-OFFSET),
			new Point(X_CENTER, Y_CENTER+OUTER_RADIUS+OFFSET),
			new Point(X_CENTER-OUTER_RADIUS+OFFSET , Y_CENTER+OUTER_RADIUS-OFFSET),
			new Point(X_CENTER-OUTER_RADIUS-OFFSET , Y_CENTER),
			new Point(X_CENTER-OUTER_RADIUS+OFFSET , Y_CENTER-OUTER_RADIUS+OFFSET),
	};
	
	private Phase phase= Phase.scan;
	private int trial = 0;
	private List<VisualObject> visualObjects = new Vector<VisualObject>();
	private int numberOfObjects = 0;
	private TaskButton doneButton;
	
	private VisualObject pickedObject = null;
	
	//private int askedPlane = 0;
	private int[] responses = new int[NUMBER_OF_TRIALS];
	private double[] errors = new double[NUMBER_OF_TRIALS];

	private Random random = new Random();
	
	public SM() {
		super();
		setBackground(Color.gray);
		doneButton = new TaskButton("DONE", DISPLAY_WIDTH-100, DISPLAY_HEIGHT - 50, 80,40 ){
			@Override
			public void doClick() {
				switch (phase) {
				case scan:
					addEvent(new Event(DELAY_TIME[random.nextInt(3)], "task", "update") {
						@Override
						public void action() {
							recallPhase();
						}
					});
					
					break;
				case recall:
					scanPhase();
					break;
				default:
					break;
				}
			}
		};
		add(doneButton);
		add(new TaskCross(X_CENTER, Y_CENTER, 5));
	}

	@Override
	public void start() {
		
		addEvent(new Event(0, "task", "update") {
			@Override
			public void action() {
				scanPhase();
			}
		});
	}

	private void scanPhase() {
		trial++;
		if (trial > NUMBER_OF_TRIALS){
			stopModel();	
		}
		if (visualObjects.size() > 0)
			for (VisualObject v : visualObjects)
				remove(v);
		visualObjects.clear();
		
		phase =Phase.scan;

		numberOfObjects = random.nextInt(4)+2;
	
		// making unique random number for the picking up the locations of the shapes
		ArrayList<Integer> listOfRandomIndexes = new ArrayList<Integer>();
		for (int i=0; i<16; i++) {
			listOfRandomIndexes.add(new Integer(i));
        }
        Collections.shuffle(listOfRandomIndexes);
        
        
        Shape randomShape;
        Colour randomColour;
        Hieght randomHoieght;
        Pattern randomPattern;
       
        for (int i = 0; i < numberOfObjects; i++) {
        	randomShape = Shape.values()[random.nextInt(Shape.values().length)];
        	randomColour = Colour.values()[random.nextInt(Colour.values().length)];
        	randomHoieght = Hieght.values()[random.nextInt(Hieght.values().length)];
        	randomPattern = Pattern.values()[random.nextInt(Pattern.values().length)];
        	VisualObject a = new VisualObject(this,LOC_ARRAY[listOfRandomIndexes.get(i)], LOC_ARRAY[listOfRandomIndexes.get(i)], randomShape, randomColour, randomHoieght, randomPattern);
    		visualObjects.add(a);
		}
        
		
		for (VisualObject v : visualObjects)
			add(v);
		processDisplay();
		repaint();

		getModel().getDeclarative().get(Symbol.get("goal")).set(Symbol.get("isa"),
				Symbol.get("scan"));
	}

	private void recallPhase() {
		
		phase = Phase.recall;
		for (int i = 0; i < visualObjects.size(); i++) {
			visualObjects.get(i).moveTo(new Point(100*(i+1), 750) );
		}
		repaint();
		processDisplay();
		processDisplayNoClear();
		getModel().getDeclarative().get(Symbol.get("goal")).set(Symbol.get("isa"),
				Symbol.get("recall"));
		System.out.println(getModel().getDeclarative().get(Symbol.get("goal")).toString());
		
	}
	
//	private Plane getNearestPlane(double x, double y) {
//		Plane nearest = null;
//		double nearestDistance = 0;
//		for (Plane plane : planes) {
//			Point fp = plane.getLocation();
//			double fd = fp.distance(x, y);
//			if (nearest == null || fd < nearestDistance) {
//				nearest = plane;
//				nearestDistance = fd;
//			}
//		}
//		return nearest;
//	}

	@Override
	public void clickMouse() {
		
		double mouseX = getMouseX();
		double mouseY = getMouseY();
		Point mousePoint = new Point((int)mouseX, (int)mouseY);
		Component[] components = getComponents();
		for (int i = 0; i < components.length; i++) {
			Component component = components[i];
			if ((component instanceof TaskButton) && component.isVisible()) {
				TaskButton taskButton = (TaskButton) component;
				Rectangle bounds = component.getBounds();
				if (bounds.contains(mousePoint)) {
					taskButton.doClick();
					repaint();
					return;
				}
			}
			else if ((component instanceof VisualObject) && component.isVisible() && mouseY>700){
				VisualObject object = (VisualObject) component;
				Rectangle bounds = component.getBounds();
				if (bounds.contains(mousePoint)) {
					pickedObject = (VisualObject) component;
					repaint();
					processDisplay();
					processDisplayNoClear();
					return;
				}
			}
		}
		
		if (pickedObject != null && phase == Phase.recall && mouseY < 700){
			pickedObject.moveTo(mousePoint);
			repaint();
			processDisplay();
			processDisplayNoClear();
			pickedObject = null;
		}
			
	}
	
	private void stopModel() {
		getModel().stop();
		
	}

	@Override
	public Result analyze(Task[] tasks, boolean output) {
//		try {
//			Values allResponses = new Values();
//			Values allErrors = new Values();
//			Values[] responseValues = new Values[NUMBER_OF_TRIALS];
//			Values[] errorValues = new Values[NUMBER_OF_TRIALS];
//			for (int i = 0; i < NUMBER_OF_TRIALS; i++) {
//				responseValues[i] = new Values();
//				errorValues[i] = new Values();
//			}
//			for (Task taskCast : tasks) {
//				SM task = (SM) taskCast;
//				for (int i = 0; i < NUMBER_OF_TRIALS; i++) {
//					allResponses.add(task.responses[i]);
//					responseValues[i].add(task.responses[i]);
//					if (task.responses[i] > 0) {
//						allErrors.add(task.errors[i]);
//						errorValues[i].add(task.errors[i]);
//					}
//				}
//			}
//
//			DecimalFormat df1 = new DecimalFormat("#.0");
//			DecimalFormat df3 = new DecimalFormat("#.000");
//
//			getModel().output("\n=========  Results  ===========\n");
//
//			getModel().output("Overall Correctness: " + df3.format(allResponses.mean()));
//			getModel().output("Overall Location Error: " + df1.format(allErrors.mean()));
//
//			getModel().output("\nCorrectness by Trial:");
//			String s = "";
//			for (int i = 0; i < NUMBER_OF_TRIALS; i++)
//				s += df3.format(responseValues[i].mean()) + "\t";
//			getModel().output(s.trim());
//
//			getModel().output("\nLocation Error by Trial:");
//			s = "";
//			for (int i = 0; i < NUMBER_OF_TRIALS; i++)
//				s += df1.format(errorValues[i].mean()) + "\t";
//			getModel().output(s.trim());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
		return new Result();
	}
}
