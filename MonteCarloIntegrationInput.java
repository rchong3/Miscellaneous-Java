/*
Monte Carlo Integration
by Richard Chong
Method summary: choose random points in a certain area and then
approximate the area enclosed by seeing how many of these random
points are within the observed barrier.
*/

import java.util.*;
import java.io.*;
import java.awt.*;
import javax.script.*;

public class MonteCarloIntegrationInput {

	private static final int X = 500;
	private static final int Y = 500; //Dimensions of window
	private static final Map<String, Integer> priority = new HashMap<>() {{
		this.put("^", 1);
		this.put("/", 2);
		this.put("*", 2);
		this.put("+", 3);
		this.put("-", 3);
	}};

	private static int dots;
	private static int xMin;
	private static int xMax;
	private static int yMin;
	private static int yMax; //Domain and range
	private static Queue<String> f1;
	private static Queue<String> f2; //functions

	public static void main(String[] args) {
		Scanner console = new Scanner(System.in);
		System.out.println("You MUST include a space in between every number/operator");
		System.out.println("Equation 1: ");
		f1 = inToPost(console.nextLine());
		System.out.println("Equation 2: ");
		f2 = inToPost(console.nextLine());
		System.out.println("Min x: ");
		xMin = console.nextInt();
		System.out.println("Max x: ");
		xMax = console.nextInt();
		System.out.println("Min y: ");
		yMin = console.nextInt();
		System.out.println("Max y: ");
		yMax = console.nextInt();
		System.out.println("How many random points?");
		dots = console.nextInt();
		DrawingPanel panel = new DrawingPanel(X, Y);
		Graphics g = panel.getGraphics();
		drawAxis(g);
		System.out.println("Estimate: " + integrate(g));
		drawDatas(g);
	}

	public static void plot(Graphics g, double x, double y) {
		g.drawOval((int) Math.round((x - xMin) * X / (xMax - xMin)), (int) Math.round(Y - 1 - (y - yMin) * Y / (yMax - yMin)), 1, 1);
	}

	public static void drawAxis(Graphics g) {
		for (int i = 4; i > 0; i--) {
			g.drawLine(0, i * 100, 5, i * 100);
			g.drawString(String.format("%.2e", yMin + (5 - i) * (yMax - yMin) / 5.), 12, i * 100 + 5);
		}

		for (int i = 1; i < 5; i++) {
			g.drawLine(i * 100, 0, i * 100, 5);
			g.drawString(String.format("%.2e", xMin + i * (xMax - xMin) / 5.), i * 100 - 25, 20);
		}
	}

	public static double evaluate(Queue<String> function, double x) {
		Deque<Double> operands = new ArrayDeque<Double>();
		for (String s: function) {
			if (s.equals("x")) {
				operands.push(x);
				continue;
			}
			try {
				operands.push(Double.parseDouble(s));
			} catch (NumberFormatException e) {
				Double b = operands.pop();
				Double a = operands.pop();
				operands.push(simplify(a, b, s));
			}
		}
		return operands.pop();
	}

	public static Queue<String> inToPost(String function) {
		//uses shunting yard algorithm to convert infix to postfix
		String[] infix = function.split(" ");
		Queue<String> postfix = new ArrayDeque<String>();
		Deque<String> operators = new ArrayDeque<String>();
		Deque<Integer> blocked = new ArrayDeque<Integer>();
		blocked.push(0);
		for (String s: infix) {
			if (s.equals("x")) {
				postfix.add(s);
				continue;
			}
			try {
				postfix.add("" + Double.parseDouble(s));
			} catch (NumberFormatException e) {
				shunt(s, postfix, operators, blocked);
			}
		}
		while (operators.size() != 0) {
			postfix.add(operators.pop());
		}
		return postfix;
	}

	public static void shunt(String s, Queue<String> postfix, Deque<String> operators, Deque<Integer> blocked) {
		//decides
		if (s.equals("(")) {
			blocked.push(operators.size());
		} else if (s.equals(")")) {
			while (operators.size() > blocked.peek()) {
				postfix.add(operators.pop());
			}
			blocked.pop();
		} else {
			while (operators.size() > 
				blocked.peek() && 
				priority.get(operators.peek()) <= 
				priority.get(s)) {
				postfix.add(operators.pop());
			}
			operators.push(s);
		}
	}

	public static double simplify(double a, double b, String operator) {
		if (operator.equals("^")) {
			return Math.pow(a, b);
		} else if (operator.equals("+")) {
			return a + b;
		} else if (operator.equals("-")) {
			return a - b;
		} else if (operator.equals("*")) {
			return a * b;
		} else if (operator.equals("/")) {
			return a / b;
		} else {
			return 0; //Error
		}
	}

	public static void drawDatas(Graphics g) {
		// calls functions that graph the 2 functions onto the drawing panel
		boolean gap1 = true;
		boolean gap2 = true;
		for (double difference = (double) (xMax - xMin) / 500; (gap1 || gap2) && difference > Double.MIN_VALUE * 2; difference /= 2) {
			if (gap1) {
				g.setColor(Color.RED);
				gap1 = plotData(f1, difference, g);
			}
			if (gap2) {
				g.setColor(Color.BLUE);
				gap2 = plotData(f2, difference, g);
			}
		}
	}

	public static boolean plotData(Queue<String> function, double difference, Graphics g) {
		//plots function
		boolean gap = false;
		double past = evaluate(f1, xMin);
		double yDif = (double) (yMax - yMin) / 500;
		if (yDif < Double.MIN_VALUE) {
			yDif = Double.MIN_VALUE;
		}
		for (double i = xMin + difference / 2; i < xMax; i += difference) {
			// graphs a point
			double next = evaluate(function, i);
			if (Math.abs(next - past) > 2 * yDif && next > yMin && next < yMax) {
				gap = true;
			}
			past = next;
			if (next > yMin && next < yMax) {
				plot(g, i, past);
			}
		}
		return gap;
	}

	public static boolean randomPointIn(int x0, int y0, int x1, int y1, Graphics g) {
		/*
		x0, y0, x1, and y1 are always the maxima in the simplest
		form of integration

		if desired, they can be used to take a random point in a
		smaller area of the graph
		*/
		double x = Math.random() * (x1 - x0) + x0;
		double y = Math.random() * (y1 - y0) + y0;
		double a = evaluate(f1, x);
		double b = evaluate(f2, x);
		if (a > b && a > y && y > b || a < b && a < y && y < b) {
			g.setColor(Color.GREEN);
			plot(g, x, y);
			return true;
		} else {
			g.setColor(Color.GRAY);
			plot(g, x, y);
			return false;
		}
	}

	public static double integrate(Graphics g) {
		//for now, just between curve and x-axis
		int in = 0;
		for (int i = 0; i < dots; i++) {
			if (randomPointIn(xMin, yMin, xMax, yMax, g)) {
				in++;
			}
		}
		return (double) in / dots * (xMax - xMin) * (yMax - yMin);
	}
}