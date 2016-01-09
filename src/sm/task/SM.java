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
import actr.task.Result;
import actr.task.Task;
import actr.task.TaskButton;
import sm.task.VisualObject.Colour;
import sm.task.VisualObject.Hieght;
import sm.task.VisualObject.Pattern;
import sm.task.VisualObject.Shape;

public class SM extends Task {
	
	private final static int DISPLAY_WIDTH = 800;
	private final static int DISPLAY_HEIGHT = 800;
	private final static int X_CENTER = DISPLAY_WIDTH / 2 - 10 ;
	private final static int Y_CENTER = DISPLAY_HEIGHT / 2 - 60;
	private final static int INNER_RADIUS = 110;
	private final static int OUTER_RADIUS = 220;
	private final static int OFFSET = 40;
	
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
	
	private int trial = 0;
	private List<VisualObject> visualObjects = new Vector<VisualObject>();
	private int numberOfObjects = 0;
	private TaskButton doneButton;
	
	private VisualObject pickedObject;
	
	//private int askedPlane = 0;
	private int[] responses = new int[NUMBER_OF_TRIALS];
	private double[] errors = new double[NUMBER_OF_TRIALS];

	public SM() {
		super();
		setBackground(Color.gray);
		doneButton = new TaskButton("DONE", DISPLAY_WIDTH-100, DISPLAY_HEIGHT - 50, 80,40 ){
			@Override
			public void doClick() {
				System.out.println("recallphase");
				recallPhase();
			}
		};
		add(doneButton);
		
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

	private Random random = new Random();

	private void scanPhase() {
		trial++;
		if (trial > NUMBER_OF_TRIALS){
			stopModel();	
		}
		
		//removeAll();
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
        	VisualObject a = new VisualObject(LOC_ARRAY[listOfRandomIndexes.get(i)], randomShape, randomColour, randomHoieght, randomPattern);
    		visualObjects.add(a);
		}
        
		
		for (VisualObject v : visualObjects)
			add(v);
		processDisplay();
		repaint();

//		double timeDelta = 0.7 * numberOfObjects;
//		addEvent(new Event(getModel().getTime() + timeDelta, "task", "update") {
//			@Override
//			public void action() {
//				removeAll();
//				for (int i = 0; i < visualObjects.size(); i++) {
//					VisualObject v = visualObjects.get(i);
//		        	add(new VisualObject ( new Point(100*(i+1), 750) , v.getShape(), v.getColour(), v.getHieght(), v.getPattern()){
//		        		public void mouseReleased(java.awt.event.MouseEvent evt) {
//		        			pickedObject = v;
//		                    System.out.println("mouseReleased " + v.getValue());
//		                }
//		        	});
//				}
//				add(doneButton);
//				processDisplay();
//				repaint();
//				
//				
//				
//				
//			}
//		});
	}

	protected void recallPhase() {
		removeAll();
		for (int i = 0; i < visualObjects.size(); i++) {
			VisualObject v = visualObjects.get(i);
        	add(new VisualObject ( new Point(100*(i+1), 750) , v.getShape(), v.getColour(), v.getHieght(), v.getPattern()){
        		public void mouseReleased(java.awt.event.MouseEvent evt) {
        			pickedObject = v;
                    System.out.println("mouseReleased " + v.getValue());
                }
        	});
		}
		doneButton = new TaskButton("DONE", DISPLAY_WIDTH-100, DISPLAY_HEIGHT - 50, 80,40 ){
			@Override
			public void doClick() {
				System.out.println("scanphase");
				scanPhase();
			}
		};
		add(doneButton);
		processDisplay();
		repaint();
		
		
	}

	private void askPlane(int index) {
		//askedPlane = index;
		//addAural(0.0, "call-sign", "sound", planes.get(askedPlane).getValue());
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
			else if ((component instanceof VisualObject) && component.isVisible()){
				
			}
		}
		
		
		
	}
//	public void clickMouse() {
//		//double mouseX = random.nextGaussian() * NUMBER_OF_PLANES + getMouseX();
//		//double mouseY = random.nextGaussian() * NUMBER_OF_PLANES + getMouseY();
//
//		if (pickedObject != null){
//			Point loc = new Point(getMouseX(), getMouseY());
//			VisualObject original =null;
//			add(new VisualObject ( loc, 
//					pickedObject.getShape(), pickedObject.getColour(),
//					pickedObject.getHieght(), pickedObject.getPattern()));
//			
//			// Findding the original object for finding its original location
//			for (VisualObject v: visualObjects){
//				if (v.getShape() == pickedObject.getShape() &&
//						v.getColour() == pickedObject.getColour() &&
//						v.getHeight() == pickedObject.getHeight() &&
//						v.getPattern() == pickedObject.getPattern())
//					original = v;
//			}
//			
//			double errorDistance = loc.distance(original.getPoint().x, original.getPoint().y);// Finding the error distance 
//			System.out.println(errorDistance);
//			
//		}else if(pickedObject == null){
//			
//		}
//
//		//Plane nearest = getNearestPlane(mouseX, mouseY);
//		//boolean correct = (nearest == planes.get(askedPlane));
//		//responses[trial] = correct ? 1 : 0;
//		//errors[trial] = nearest.getLocation().distance(mouseX, mouseY);
//	}
	
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
