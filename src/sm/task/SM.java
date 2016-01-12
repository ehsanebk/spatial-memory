package sm.task;

import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import javax.sql.rowset.spi.TransactionalWriter;

import actr.model.Event;
import actr.model.Symbol;
import actr.task.Result;
import actr.task.Statistics;
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
	private int currentOrder;
	private double currentDelay;
	private double lastTime = 0;
	private TaskButton doneButton;
	private Random random = new Random();	
	private VisualObject pickedObject = null;
	
	private Values modelMeanDistnaces = new Values();
	private Values modelMeanRT = new Values();
	
	double [][] modelData = {           
			//setsize+order 500:mean-distance  mean-rt  1000:mean-distance  mean-rt  1500:mean-distance  mean-rt
			{        21,        0,             0,            0,             0,            0,             0       },
			{        22,        0,             0,            0,             0,            0,             0       },
			{        31,        0,             0,            0,             0,            0,             0       },
			{        32,        0,             0,            0,             0,            0,             0       },
			{        33,        0,             0,            0,             0,            0,             0       },
			{        41,        0,             0,            0,             0,            0,             0       },
			{        42,        0,             0,            0,             0,            0,             0       },
			{        43,        0,             0,            0,             0,            0,             0       },
			{        44,        0,             0,            0,             0,            0,             0       },
			{        51,        0,             0,            0,             0,            0,             0       },
			{        52,        0,             0,            0,             0,            0,             0       },
			{        53,        0,             0,            0,             0,            0,             0       },
			{        54,        0,             0,            0,             0,            0,             0       },
			{        55,        0,             0,            0,             0,            0,             0       }};
                     

	public SM() {
		super();
		setBackground(Color.gray);

		doneButton = new TaskButton("DONE", DISPLAY_WIDTH-100, DISPLAY_HEIGHT - 50, 80,40 ){
			@Override
			public void doClick() {
				if (phase == Phase.scan){
					currentDelay =DELAY_TIME[random.nextInt(3)];
					// making the changes after DELAY_TIME
					addEvent(new Event( SM.this.getModel().getTime()+ currentDelay, "task", "update") {
						@Override
						public void action() {
							recallPhase();
						}
					});

				}
				else
					scanPhase();
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
		add(new TaskCross(X_CENTER, Y_CENTER, 5));
	}

	private void scanPhase() {
		trial++;
		if (trial > NUMBER_OF_TRIALS){
			computeModelAverage();
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
		currentOrder = 1;
		phase = Phase.recall;
		for (int i = 0; i < visualObjects.size(); i++) {
			visualObjects.get(i).moveTo(new Point(100*(i+1), 750) );
		}
		repaint();
		processDisplay();
		processDisplayNoClear();
		getModel().getDeclarative().get(Symbol.get("goal")).set(Symbol.get("isa"),
				Symbol.get("recall"));
		//System.out.println(getModel().getDeclarative().get(Symbol.get("goal")).toString());
		repaint();
		processDisplay();
		
	}
	

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
				lastTime = getModel().getTime();
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
			double RT = getModel().getTime() - lastTime;
			allocateDistanceAndTimeForCurrentTria(pickedObject, RT ,numberOfObjects, currentOrder ,currentDelay);
			repaint();
			processDisplay();
			processDisplayNoClear();
			pickedObject = null;
			currentOrder++;
		}
			
	}

	private void stopModel() {
	
		// ordering the values in a list for getting correlation at the end 
		for (int i = 0; i < modelData.length; i++) {
			modelMeanDistnaces.add(modelData[i][1]);
			modelMeanRT.add(modelData[i][2]);
			modelMeanDistnaces.add(modelData[i][3]);
			modelMeanRT.add(modelData[i][4]);
			modelMeanDistnaces.add(modelData[i][5]);
			modelMeanRT.add(modelData[i][6]);
		}
		
		getModel().stop();
	}

	// run through the model data and sum up the distance order and response times to calculate the mean
	private void allocateDistanceAndTimeForCurrentTria(VisualObject v , double responseTime, int numberOfObjects, int currentOrder , double currentDelay){
		int setsize_order = numberOfObjects*10 + currentOrder;
		for (int i = 0; i < modelData.length; i++) {
			if (modelData[i][0] == setsize_order){
				System.out.println(setsize_order);
				 modelData[i][(int)currentDelay*4] += v.distanceFromOriginalPosition();
				 modelData[i][(int)currentDelay*4 +1] += v.distanceFromOriginalPosition();
			}	
		}
		
	}
	private void computeModelAverage(){
		for (int i = 0; i < modelData.length; i++) {
			for (int j = 1; j < modelData[i].length; j++) {
				modelData[i][j] /= modelData[i][j] / trial;
			} 	
		}
	}

	

	double [][] subjectData = {		
//	   setsize order 500:mean-distance  mean-rt  1000:mean-distance  mean-rt  1500:mean-distance  mean-rt
			{21,         48.98669,      2.118309,     51.06345,      1.977565,     49.39542,      2.108340},
			{22,         57.17774,      2.060967,     60.71604,      2.083069,     65.27009,      2.057485},
			{31,         52.59191,      2.089555,     57.68851,      2.120293,     59.14785,      2.114943},
			{32,         79.52583,      2.066912,     72.39516,      2.193382,     78.24223,      2.189131},
			{33,         91.71579,      1.975042,     83.36576,      2.035882,     86.20471,      2.061694},
			{41,         66.33435,      2.174735,     67.27975,      2.259349,     69.61219,      2.284546},
			{42,         79.94063,      2.094857,     84.32421,      2.191237,     84.53771,      2.189087},
			{43,         106.47135,     2.069533,     01.27530,      2.153021,     108.70950,     2.206206},
			{44,         106.77691,     1.916349,     10.63078,      1.945411,     113.19957,     1.999764},
			{51,         74.11933,      2.496001,     78.10549,      2.433900,     76.17092,      2.408664},
			{52,         91.23742,      2.225441,     98.99052,      2.285018,     103.93411,     2.357762},
			{53,         120.28720,     2.194071,     19.91568,      2.166547,     127.75890,     2.311127},
			{54,         133.92358,     2.168754,     37.39534,      2.092915,     136.47923,     2.113358},
			{55,         131.44671,     1.899707,     30.99772,      1.934896,     135.33160,     1.884930}};

	@Override
	public Result analyze(Task[] tasks, boolean output) {
		try {
			
			
			Values humanMeanDistances = new Values();
			Values humanMeanRT = new Values();
			
			for (int i = 0; i < subjectData.length; i++) {

				humanMeanDistances.add(subjectData[i][1]);
				humanMeanRT.add(subjectData[i][2]);
				humanMeanDistances.add(subjectData[i][3]);
				humanMeanRT.add(subjectData[i][4]);
				humanMeanDistances.add(subjectData[i][5]);
				humanMeanRT.add(subjectData[i][6]);
			}
			
			
			Values allModelMeanDistances = new Values();
			Values allModelMeanRT = new Values();
			
			int numberOfExperiments= 0;
			for (Task taskCast : tasks) {
				SM task = (SM) taskCast;
				allModelMeanDistances.add(task.modelMeanDistnaces);
				allModelMeanRT.add(task.modelMeanRT);
				numberOfExperiments++;
			}
			
			allModelMeanDistances.divide(numberOfExperiments);
			allModelMeanRT.divide(numberOfExperiments);

			DecimalFormat df1 = new DecimalFormat("#.0");
			DecimalFormat df3 = new DecimalFormat("#.000");

			getModel().output("\n=========  Results  ===========\n");

			if (output) {
				double rDistance = humanMeanDistances.correlation(allModelMeanDistances);
				double rRT = humanMeanRT.correlation(allModelMeanRT);
				getModel().output("\n=====\n");
				
				
				getModel().output("Correlation Value for Distances = " + String.format("%.2f", rDistance));
				getModel().output("Correlation Value for RTs       = " + String.format("%.2f", rRT));
				
			}

			
			getModel().output("Overall Location Error: " + df1.format(allModelMeanDistances.mean()));
			getModel().output("Overall Responces     : " + df1.format(allModelMeanRT.mean()));

			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new Result();
	}
}
