package sm.task;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import actr.task.Task;
import actr.task.TaskComponent;

public class VisualObject extends JPanel implements TaskComponent {
	
	
	public enum Shape {
		CIRCLE, RECTANGLE, TRIANGLE
	};
	public enum  Colour{
		RED, BLUE, GREEN 
	};
	public enum Hieght {
		TALL, SHORT
	};
	public enum Pattern {
		SOLID, STIPPLED
	};

	private Task task;
	private Point point;
	private Point original;
	private Shape shape;
	private Colour colour;
	private Hieght hieght;
	private Pattern pattern;
	

	public VisualObject( Task task,Point original, Point point, Shape shape, Colour colour, Hieght hieght, Pattern pattern) {

		this.task = task;
		this.point = point;
		this.shape = shape;
		this.colour = colour;
		this.hieght = hieght;
		this.pattern = pattern;
		this.original =original;
		
		setBounds(point.getIntX(), point.getIntY(), 10, 10);
		
	}

//	@Override
//	public String getName() {
//		return "Object";
//	}
	
	public double distanceFromOriginalPosition (){
		return point.distance(original);
	}
	
	public void moveTo(Point point){
		
		setLocation(point.getIntX(), point.getIntY());
		setBounds(point.getIntX(), point.getIntY(), 10, 10);
		this.point = point;
	}

	public Point getPoint() {
		return point;
	}
	
	public Shape getShape() {
		return shape;
	}

	public Colour getColour() {
		return colour;
	}

	public Hieght getHieght() {
		return hieght;
	}

	public Pattern getPattern() {
		return pattern;
	}

	@Override
	public String getKind() {
		return "object";
	}

	@Override
	public String getValue() {
		return shape.toString() + " " + colour.toString() +" "+ hieght.toString() + " " + pattern.toString();
	}

	

	@Override
	public void paintComponent(Graphics g) {
		int x_offset = 0;
		int y_offset = 0;
		Color color;

		switch (hieght) {
		case TALL:
			x_offset = 10;
			y_offset = 30;
			break;
		case SHORT:
			x_offset = 30;
			y_offset = 10;
			break;
		default:
			break;
		} 

		switch (colour) {
		case RED:
			color = Color.RED;
			break;
		case BLUE:
			color = Color.BLUE;
			break;
		case GREEN:
			color = Color.GREEN;
			break;
		default:
			color = Color.BLACK;
			break;
		}	

		Graphics2D g2 = (Graphics2D) g;
		g2.setClip(-x_offset, -y_offset, 2*x_offset,2*y_offset );
		java.awt.Shape s;
		switch (shape) {
		case CIRCLE:
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setPaint(color);
			s = new Ellipse2D.Double(-x_offset, -y_offset , 2 * x_offset, 2 * y_offset);
			g2.draw(s);
			g2.fill(s);
			g2.dispose();
			break;
		case RECTANGLE:
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setPaint(color);
			s  = new Rectangle2D.Double(-x_offset, -y_offset , 2 * x_offset, 2 *y_offset);
			g2.draw(s);
			g2.fill(s);
			g2.dispose();
			break;
		case TRIANGLE:
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setPaint(color);
			GeneralPath triangle = new GeneralPath();;
			triangle.moveTo(-x_offset,y_offset );
			triangle.lineTo(0, -y_offset);
			triangle.lineTo(x_offset, y_offset);
			triangle.closePath();
			
			g2.draw(triangle);
			g2.fill(triangle);
			g2.dispose();
			break;
			
		default:
			break;
		}		
	}
}
