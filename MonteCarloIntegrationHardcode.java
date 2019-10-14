/*
Monte Carlo Integration
Method summary: choose random points in a certain area and then
approximate the area enclosed by seeing how many of these random
points are within the observed barrier.
*/

import java.util.*;
import java.io.*;
import java.awt.*;

public class MonteCarloIntegrationHardcode {

	public static final int X = 500;
	public static final int Y = 500;
	public static final int SIZE = 500;

	public static void main(String[] args) {
		// Scanner console = new Scanner(System.in);
		DrawingPanel panel = new DrawingPanel(X, Y);
		Graphics g = panel.getGraphics();
		System.out.println(integrate(g));
		drawData(g);
	}

	public static double function1(double x) {
		return x;
	}

	public static double function2(double x) {
		return 250 + 40 * Math.sin(x / 10);
	}

	public static void drawData(Graphics g) {
		// graphs onto the drawing panel
		g.setColor(Color.RED);
		for (int i = 0; i < X; i++) {
			// graphs a point
			g.drawOval(i, (int) (Y - 1 - function1(i)), 1, 1);
		}
		g.setColor(Color.BLUE);
		for (int i = 0; i < X; i++) {
			// graphs a point
			g.drawOval(i, (int) (Y - 1 - function2(i)), 1, 1);
		}
	}

	public static boolean randomPointIn(int x0, int y0, int x1, int y1, Graphics g) {
		int x = (int) (Math.random() * (x1 - x0) + x0);
		double y = Math.random() * (y1 - y0) + y0;
		g.drawOval(x, (int) (Y - 1 - y), 1, 1);
		double a = function1(x);
		double b = function2(x);
		if (a > b && a > y && y > b || a < b && a < y && y < b) {
			return true;
		} else {
			return false;
		}
	}

	public static double integrate(Graphics g) {
		//for now, just between curve and x-axis
		int in = 0;
		int total = 1000;
		for (int i = 0; i < total; i++) {
			if (randomPointIn(0, 0, X, Y, g)) {
				in++;
			}
		}
		return (double) in / total * 100 * 10000;
	}
}