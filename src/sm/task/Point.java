package sm.task;

import java.awt.geom.Point2D;

public class Point extends Point2D.Double {

	public Point(double x, double y) {
		super(x, y);
	}

	public int getIntX() {
		return (int) Math.round(x);
	}

	public int getIntY() {
		return (int) Math.round(y);
	}

	public Point translateByHeading(double distance, double heading) {
		return new Point(getX() + distance * Math.cos(heading), getY() + distance * Math.sin(heading));
	}

	public double heading(Point p) {
		return Math.atan2(p.getY() - y, p.getX() - x);
	}
}
