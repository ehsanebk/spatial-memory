package sm.task;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import actr.task.Statistics;

public class Values {
	private ArrayList<Double> v;

	public Values() {
		v = new ArrayList<Double>();
	}

	public void add(double d) {
		v.add(d);
	}

	public void add(Values vals2) {
		for (int i = 0; i < vals2.size(); i++)
			v.add(vals2.get(i));
	}

	public double correlation(Values vals2) {
		double[] v1 = new double[v.size()];
		double[] v2 = new double[vals2.size()];
		if (v1.length == v2.length){
			for (int i = 0; i < v2.length; i++) {
				v1[i] = v.get(i);
				v2[i] = vals2.get(i);
			}
			return Statistics.correlation(v1, v2);
		
		}
		else{
			return 0;
		}
	}
	public double rmse(Values vals2) {
		double[] v1 = new double[v.size()];
		double[] v2 = new double[vals2.size()];
		if (v1.length == v2.length){
			for (int i = 0; i < v2.length; i++) {
				v1[i] = v.get(i);
				v2[i] = vals2.get(i);
			}
			return Statistics.rmse(v1, v2);
		
		}
		else{
			return 0;
		}
	}
	
	public void divide(double d) {
		for (int i = 0; i < v.size(); i++)
			v.set(i, v.get(i)/d);
	}
	public double get(int i) {
		return v.get(i);
	}

	public void removeLast() {
		if (v.size() > 0)
			v.remove(v.size() - 1);
	}

	public int size() {
		return v.size();
	}

	public double min() {
		if (v.size() == 0)
			return 0;
		double min = v.get(0);
		for (int i = 1; i < v.size(); i++)
			if (v.get(i) < min)
				min = v.get(i);
		return min;
	}

	public double max() {
		if (v.size() == 0)
			return 0;
		double max = v.get(0);
		for (int i = 1; i < v.size(); i++)
			if (v.get(i) > max)
				max = v.get(i);
		return max;
	}

	public double mean() {
		if (v.size() == 0)
			return 0;
		double sum = 0;
		for (int i = 0; i < v.size(); i++)
			sum += v.get(i);
		return sum / (1.0 * v.size());
	}

	public double average() {
		return mean();
	}

	public double stddev() {
		if (v.size() < 2)
			return 0;
		double mean = mean();
		double sum = 0;
		for (int i = 0; i < v.size(); i++)
			sum += Math.pow(v.get(i) - mean, 2);
		return Math.sqrt(sum / (1.0 * (v.size() - 1)));
	}

	public double stderr() {
		if (v.size() < 2)
			return 0;
		return stddev() / Math.sqrt(1.0 * v.size());
	}

	public double confint() {
		return 1.96 * stderr();
	}

	public double rmse(double expected) {
		if (v.size() == 0)
			return 0;
		double sum = 0;
		for (int i = 0; i < v.size(); i++)
			sum += Math.pow(v.get(i) - expected, 2);
		return Math.sqrt(sum / (1.0 * v.size()));
	}

	public double rmse() {
		return rmse(0);
	}

	public String toString(DecimalFormat df) {
		if (v.size() == 0)
			return "";
		String s = "";
		s += df.format(v.get(0));
		for (int i = 1; i < v.size(); i++)
			s += "\t" + df.format(v.get(i));
		return s;
	}

	private final DecimalFormat DEFAULT_FORMAT = new DecimalFormat("#.000");

	@Override
	public String toString() {
		return toString(DEFAULT_FORMAT);
	}
}
