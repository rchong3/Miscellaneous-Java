/*
Monte Carlo Integration
Method summary: choose random points in a certain area and then
approximate the area enclosed by seeing how many of these random
points are within the observed barrier.
*/

import java.util.*;
import java.io.*;
import java.awt.*;

public class MonteCarloIntegration {

	public static final int X = 1000;
	public static final int Y = 1000;
	public static final int SIZE = 1000;

	public static void main(String[] args) throws Exception {
		Scanner console = new Scanner(System.in);
		//data0 > data1
		double[] data0 = getData(console);
		double[] data1 = getData(console);
		DrawingPanel panel = new DrawingPanel(X, Y);
		Graphics g = panel.getGraphics();
		System.out.println(integrate(data0, data1, g));
		g.setColor(Color.RED);
		drawData(g, data0);
		g.setColor(Color.BLUE);
		drawData(g, data1);
	}

	public static void drawData(Graphics g, double[] data) {
		// graphs all data stored in double array onto the drawing panel
		for (int i = 0; i < data.length; i++) {
			// graphs a point
			g.drawOval(i, (int) (Y - 1 - data[i]), 1, 1);
		}
	}

	public static double[] getData(Scanner console) throws Exception {
		// collects data from the following hardcoded .txt files:
		//"Xsquared.txt"
		//"Xaxis.txt"
		Scanner in = new Scanner(new File(console.next()));
		double[] data = new double[SIZE];
		for (int i = 0; i < SIZE; i++) {
			data[i] = in.nextDouble();
		}
		return data;
	}

	public static boolean randomPointIn(double[] data0, double[] data1, int x0, int y0, int x1, int y1, Graphics g) {
		int x = (int) (Math.random() * (x1 - x0) + x0);
		double y = Math.random() * (y1 - y0) + y0;
		g.drawOval(x, (int) (Y - 1 - y), 1, 1);
		double a = data1[x];
		double b = data0[x];
		if (a > b && a > y && y > b || a < b && a < y && y < b) {
			return true;
		} else {
			return false;
		}
	}

	public static double integrate(double[] data0, double[] data1, Graphics g) {
		//for now, just between curve and x-axis
		int in = 0;
		int total = 1000;
		for (int i = 0; i < total; i++) {
			if (randomPointIn(data0, data1, 0, 0, X, Y, g)) {
				in++;
			}
		}
		return (double) in / total * 100 * 10000;
	}
}