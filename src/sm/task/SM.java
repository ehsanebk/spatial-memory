package sm.task;

import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import actr.model.Chunk;
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
	private int currentOrder;
	private int currentDelayIndex;
	private double lastTime = 0;
	private TaskButton doneButton;
	private Random random = new Random();	
	private VisualObject pickedObject = null;
	
	double [][] modelData = {           
			//setsize+order*500*:counter|distance|rt*1000*:counter|distance|rt*1500*:counter|distance|rt
			{        21,         0,      0,       0,       0,      0,       0,       0,      0,       0       },
			{        22,         0,      0,       0,       0,      0,       0,       0,      0,       0       },
			{        31,         0,      0,       0,       0,      0,       0,       0,      0,       0       },
			{        32,         0,      0,       0,       0,      0,       0,       0,      0,       0       },
			{        33,         0,      0,       0,       0,      0,       0,       0,      0,       0       },
			{        41,         0,      0,       0,       0,      0,       0,       0,      0,       0       },
			{        42,         0,      0,       0,       0,      0,       0,       0,      0,       0       },
			{        43,         0,      0,       0,       0,      0,       0,       0,      0,       0       },
			{        44,         0,      0,       0,       0,      0,       0,       0,      0,       0       },
			{        51,         0,      0,       0,       0,      0,       0,       0,      0,       0       },
			{        52,         0,      0,       0,       0,      0,       0,       0,      0,       0       },
			{        53,         0,      0,       0,       0,      0,       0,       0,      0,       0       },
			{        54,         0,      0,       0,       0,      0,       0,       0,      0,       0       },
			{        55,         0,      0,       0,       0,      0,       0,       0,      0,       0       }};
                     

	public SM() {
		super();
		setBackground(Color.gray);

		doneButton = new TaskButton("DONE", DISPLAY_WIDTH-100, DISPLAY_HEIGHT - 50, 80,40 ){
			@Override
			public void doClick() {
				if (phase == Phase.scan){
					currentDelayIndex =random.nextInt(3);
					// making the changes after DELAY_TIME
					addEvent(new Event( SM.this.getModel().getTime()+ DELAY_TIME[currentDelayIndex], "task", "update") {
						@Override
						public void action() {
							recallPhase();
						}
					});

				}
				else{
					scanPhase();
				}
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
			stopModel();	
		}
		if (visualObjects.size() > 0)
			for (VisualObject v : visualObjects)
				remove(v);
		visualObjects.clear();
		
		phase =Phase.scan;

		numberOfObjects = random.nextInt(4)+2;
	
		// making unique random number for the picking up the locations of the shapes
		ArrayList<Integer> listOfLocIndexes = new ArrayList<Integer>();
		for (int i=0; i<16; i++) {
			listOfLocIndexes.add(new Integer(i));
        }
        Collections.shuffle(listOfLocIndexes);
        
        // [ shapeIndex colourIndex sizeIndex patternIndex ]
        List<int []> listOfObjecsIndexes = new ArrayList<int []>();
        for (int s = 0; s < Shape.values().length; s++)
        	for (int c = 0; c < Colour.values().length; c++)
        		for (int h = 0; h < Hieght.values().length; h++) 
        			for (int p = 0; p < Pattern.values().length; p++){
        				listOfObjecsIndexes.add(new int[] {s,c,h,p} );
        				}
        Collections.shuffle(listOfObjecsIndexes);

        for (int i = 0; i < numberOfObjects; i++) {
        	VisualObject a = new VisualObject(this,LOC_ARRAY[listOfLocIndexes.get(i)], LOC_ARRAY[listOfLocIndexes.get(i)], 
        			Shape.values()[listOfObjecsIndexes.get(i)[0]] , // index 0 for shape
        			Colour.values()[listOfObjecsIndexes.get(i)[1]] , //index 1 for colour
        			Hieght.values()[listOfObjecsIndexes.get(i)[2]] , //index 2 for hieght
        			Pattern.values()[listOfObjecsIndexes.get(i)[3]]  //index 3 for pattern
         			);
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
	public double bind(Iterator<String> it) {
		try {
			it.next();
			String cmd = it.next();
			if (cmd.equals("noise")) {
				String name = it.next();
				Chunk plane = getModel().getDeclarative().get(Symbol.get(name));
				double activation = plane.getBaseLevel(); // getActivation();
				double threshold = -0.5;

				double base = .02;
				double scale = .18;
				double noise = base + scale * (activation >= threshold ? Math.exp(-activation + threshold) : 1);

				// double base = .0;
				// double scale = .1;
				// double noise = Math.min( Math.pow(NUMBER_OF_PLANES/2.0, 2) /
				// 100.0 , 0.20 );

				 System.out.println("noise = " + noise);
				 System.out.println("activation = " + activation);

				return noise;
			} else
				return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public void clickMouse() {
		
		double mouseX = getMouseX() ; // there is a xx pixels error for the X coordinates in the architecture
		double mouseY = getMouseY() ; // there is a xx pixels error for the Y coordinates in the architecture
		Point mousePoint = new Point((int)mouseX, (int)mouseY);
		Component[] components = getComponents();
		for (int i = 0; i < components.length; i++) {
			Component component = components[i];
			if ((component instanceof TaskButton) && component.isVisible()) {
				TaskButton taskButton = (TaskButton) component;
				Rectangle bounds = component.getBounds();
				if (bounds.contains(mousePoint)) {
					taskButton.doClick();
					processDisplay();
					return;
				}
			}
			else if ((component instanceof VisualObject) && component.isVisible() && mouseY>700){
				lastTime = getModel().getTime();
				VisualObject object = (VisualObject) component;
				Rectangle bounds = component.getBounds();
				if (bounds.contains(mousePoint)) {
					pickedObject = (VisualObject) component;
					processDisplay();
					return;
				}
			}
		}
		
		if (pickedObject != null && phase == Phase.recall && mouseY < 700){
			pickedObject.moveTo(mousePoint);
			processDisplay();
			double RT = getModel().getTime() - lastTime;
			allocateDistanceAndTimeForCurrentTria(pickedObject, RT ,numberOfObjects, currentOrder ,currentDelayIndex);
			pickedObject = null;
			currentOrder++;
		}
	}

	private void stopModel() {
		getModel().stop();
	}
	
	// run through the model data and sum up the distance order and response times to calculate the mean
	private void allocateDistanceAndTimeForCurrentTria(VisualObject v , double responseTime, int numberOfObjects, int currentOrder , int currentDelayIndex){
		int setsize_order = numberOfObjects*10 + currentOrder;
		for (int i = 0; i < modelData.length; i++) {
			if (modelData[i][0] == setsize_order){
				modelData[i][currentDelayIndex*3+1] ++; // adding to the counter 
				modelData[i][currentDelayIndex*3+2] += v.distanceFromOriginalPosition();
				modelData[i][currentDelayIndex*3+3] += responseTime;
			}	
		}
	}

	
	
	
	double [][] humanData = {		
//	   setsize order 500:mean-distance  mean-rt  1000:mean-distance  mean-rt  1500:mean-distance  mean-rt
			 {21,        48.98669,      2.118309,     51.06345,      1.977565,     49.39542,      2.108340},
			 {22,        57.17774,      2.060967,     60.71604,      2.083069,     65.27009,      2.057485},
			 {31,        52.59191,      2.089555,     57.68851,      2.120293,     59.14785,      2.114943},
			 {32,        79.52583,      2.066912,     72.39516,      2.193382,     78.24223,      2.189131},
			 {33,        91.71579,      1.975042,     83.36576,      2.035882,     86.20471,      2.061694},
			 {41,        66.33435,      2.174735,     67.27975,      2.259349,     69.61219,      2.284546},
			 {42,        79.94063,      2.094857,     84.32421,      2.191237,     84.53771,      2.189087},
			 {43,        106.47135,     2.069533,     101.27530,     2.153021,     108.70950,     2.206206},
			 {44,        106.77691,     1.916349,     110.63078,     1.945411,     113.19957,     1.999764},
			 {51,        74.11933,      2.496001,     78.10549,      2.433900,     76.17092,      2.408664},
			 {52,        91.23742,      2.225441,     98.99052,      2.285018,     103.93411,     2.357762},
			 {53,        120.28720,     2.194071,     119.91568,     2.166547,     127.75890,     2.311127},
			 {54,        133.92358,     2.168754,     137.39534,     2.092915,     136.47923,     2.113358},
			 {55,        131.44671,     1.899707,     130.99772,     1.934896,     135.33160,     1.884930}};
	
	double [][] experimentData = {           
			//setsize+order*500*:counter|distance|rt*1000*:counter|distance|rt*1500*:counter|distance|rt         
			{        21,         0,      0,       0,       0,      0,       0,       0,      0,       0       }, 
			{        22,         0,      0,       0,       0,      0,       0,       0,      0,       0       }, 
			{        31,         0,      0,       0,       0,      0,       0,       0,      0,       0       }, 
			{        32,         0,      0,       0,       0,      0,       0,       0,      0,       0       }, 
			{        33,         0,      0,       0,       0,      0,       0,       0,      0,       0       }, 
			{        41,         0,      0,       0,       0,      0,       0,       0,      0,       0       }, 
			{        42,         0,      0,       0,       0,      0,       0,       0,      0,       0       }, 
			{        43,         0,      0,       0,       0,      0,       0,       0,      0,       0       }, 
			{        44,         0,      0,       0,       0,      0,       0,       0,      0,       0       }, 
			{        51,         0,      0,       0,       0,      0,       0,       0,      0,       0       }, 
			{        52,         0,      0,       0,       0,      0,       0,       0,      0,       0       }, 
			{        53,         0,      0,       0,       0,      0,       0,       0,      0,       0       }, 
			{        54,         0,      0,       0,       0,      0,       0,       0,      0,       0       }, 
			{        55,         0,      0,       0,       0,      0,       0,       0,      0,       0       }};

	@Override
	public Result analyze(Task[] tasks, boolean output) {
		try {
			
			
			Values humanMeanDistances = new Values();
			Values humanMeanRT = new Values();
			
			Values humanCombinedDelaysMeanDistances = new Values();
			Values humanCombinedDalaysMeanRT = new Values();
			
			for (int i = 0; i < humanData.length; i++) {

				humanMeanDistances.add(humanData[i][1]);
				humanMeanRT.add(humanData[i][2]);
				humanMeanDistances.add(humanData[i][3]);
				humanMeanRT.add(humanData[i][4]);
				humanMeanDistances.add(humanData[i][5]);
				humanMeanRT.add(humanData[i][6]);
				// combined delays
				humanCombinedDelaysMeanDistances.add((humanData[i][1]+humanData[i][3]+humanData[i][5])/3);
				humanCombinedDalaysMeanRT.add((humanData[i][2]+humanData[i][4]+humanData[i][6])/3);
			}
			
			Values experimentMeanDistances = new Values();
			Values experimentMeanRT = new Values();
			
			Values experimentCombinedDelaysMeanDistances = new Values();
			Values experimentCombinedDelaysMeanRT = new Values();
			
			for (Task taskCast : tasks) {
				SM task = (SM) taskCast;
				for (int i = 0; i < task.modelData.length; i++) {
					for (int j = 1; j < task.modelData[i].length; j++) {
						experimentData[i][j] += task.modelData[i][j];
					}
				}
			}
		
			// computing the mean of distances and RTs with dividing them by the their counter
			for (int i = 0; i < experimentData.length; i++) {
				experimentData[i][2]/=experimentData[i][1];experimentData[i][3]/=experimentData[i][1];experimentData[i][1]=1;
				experimentMeanDistances.add(experimentData[i][2]);experimentMeanRT.add(experimentData[i][3]);
				experimentData[i][5]/=experimentData[i][4];experimentData[i][6]/=experimentData[i][4];experimentData[i][4]=1;
				experimentMeanDistances.add(experimentData[i][5]);experimentMeanRT.add(experimentData[i][6]);
				experimentData[i][8]/=experimentData[i][7];experimentData[i][9]/=experimentData[i][7];experimentData[i][7]=1;
				experimentMeanDistances.add(experimentData[i][8]);experimentMeanRT.add(experimentData[i][9]);
				// combined delays
				experimentCombinedDelaysMeanDistances.add((experimentData[i][2]+experimentData[i][5]+experimentData[i][8])/3);
				experimentCombinedDelaysMeanRT.add((experimentData[i][3]+experimentData[i][6]+experimentData[i][9])/3);
			}
			

			DecimalFormat df1 = new DecimalFormat("#.0");
			DecimalFormat df3 = new DecimalFormat("#.000");

			getModel().output("\n=========  Results  ===========");
			getModel().output("Create two lists of the subject and model data and then "
					+ "compute the correlation and mean deviation\n");
			if (output) {
				getModel().output("\n=========  Raw Distances Data  ===========");
				getModel().output("humanMeanDistances      :\t" + humanMeanDistances.toString(df1));
				getModel().output("experimentMeanDistances :\t" + experimentMeanDistances.toString(df1));
				getModel().output("\n=========  Raw RT Data  ===========");
				getModel().output("humanMeanRT             :\t" + humanMeanRT.toString(df1));
				getModel().output("experimentMeanRT        :\t" + experimentMeanRT.toString(df1));
				double rmseDistance = humanMeanDistances.rmse(experimentMeanDistances);
				double rmseRT = humanMeanRT.rmse(experimentMeanRT);
				getModel().output("\n");
				getModel().output("rmse Value for Distances =\t" + String.format("%.2f", rmseDistance));
				getModel().output("rmse Value for RTs       =\t" + String.format("%.2f", rmseRT));
				
				getModel().output("\n=========  Raw Combined Delays Data  ===========");
				getModel().output("Create the lists with the mean of the three different delays(0.5, 1.0 and 1.5)\n");
				getModel().output("setsize,order              :\t2,1 \t2,2 \t3,1 \t3,2 \t3,3 \t4,1 \t4,2 \t4,3 \t4,4 \t5,1 \t5,2 \t5,3 \t5,4 \t5,5" );
				getModel().output("----------------------------------------------------------------" 
						+ "------------------------------------------------------------------------------");
				getModel().output("Combined disntances(human) :\t" + humanCombinedDelaysMeanDistances.toString(df1));
				getModel().output("Combined disntances        :\t" + experimentCombinedDelaysMeanDistances.toString(df1));
				getModel().output("Combined RTs(human)        :\t" + humanCombinedDalaysMeanRT.toString(df1));
				getModel().output("Combined RTs               :\t" + experimentCombinedDelaysMeanRT.toString(df1));
				double rmseCombinedDistance = humanCombinedDelaysMeanDistances.rmse(experimentCombinedDelaysMeanDistances);
				double rmseCombinedRT = humanCombinedDalaysMeanRT.rmse(experimentCombinedDelaysMeanRT);
				getModel().output("\n");
				getModel().output("rmse Value for Distances =\t" + String.format("%.2f", rmseCombinedDistance));
				getModel().output("rmse Value for RTs       =\t" + String.format("%.2f", rmseCombinedRT));
//				getModel().output("R-squared Value for RTs       =\t" + String.format("%.2f", rmseCombinedRT));
//				getModel().output("R-squared Value for RTs       =\t" + String.format("%.2f", rmseCombinedRT));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return new Result();
	}
}
