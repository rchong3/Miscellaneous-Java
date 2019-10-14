/*
Richard Chong

			SAVITZKY-GOLAY DIGITAL DERIVATIVE APPLIED TO KEELING CURVE

Apply a Savitzky-Golay differentiation filter to the analysis of the Keeling Curve.
Your goal is to reveal the rate of change of CO2 accumulation in the atmosphere over
time. One particular event of interest is the 1991 eruption of Mt. Pinatubo in the
Philippines, which impacted the global climate for several years. In particular, it left
its signature on the Keeling curve, namely a short-lived but significant decrease in
the rate of change.

This slope change, subtle and easily overlooked in the raw data, lends itself to analysis
using your Savitzky-Golay differentiation program. It is suggested that you make a
copy of your SavitzkyGolayFilterTest program and rename it SavitzkyGolayFilterKeeling.
Your new program will use all the features of your test program, with the additional
feature of a moving average to eliminate the seasonal variation in CO2 concentration.

The Keeling Curve input data will be provided to you in a text file, KeelingDataSavGol.txt,
which will replace your ParabolaPlusGaussian.txt file as input to your program.
The first data point, 315.71, is for March, 1958, followed by monthly readings
through 2016, for a total of 697 data points (missing points in the original data set
have been filled in with interpolated values). As before, include tic marks for time on
your horizontal axis, one tic mark every two years starting in 1960.

In order that your plot fills up your 800 x 400 panel, subtract 310 from all data points
and scale by a factor of 4. As before, with your origin at (0, 399) your plot should nicely
fill your panel.

Since we are interested in the slope change over a multi-year period, it will be
helpful to first create a trend line free of the seasonal slope variations in the raw
Keeling Curve data. This can be done using a moving average, where each data point is
replaced with the average over a window of time centered about the given month.

Allow the user to input the width of this window in months. Experiment with the number of
months needed to produces a smooth trend line without over-smoothing a local trend spanning
a few years - such as the Mt. Pinatubo feature.

As before, to avoid array-out-of-bounds complications at the beginning and end of your data,
either when finding the moving average or when filtering, simply iterate over a range
which stays a "safe" distance from the endpoints of your plot, i.e. at least half the width
of your moving average or filter. Leaving a few points un-smoothed and/or unfiltered at
either end of your plot will have little or no effect on inboard points.

The first derivative of your smoothed data using, for example, filter 8, should then show
a pronounced slope change in the time period corresponding to Mt. Pinatubo. As before, scale
your differentiated data by a factor of 4 for better visualization.
*/

import java.util.Scanner;
import java.io.*;
import java.awt.*;

public class SavitzkyGolayFilterKeeling {
	private static int[][] filters = {
		{0, 0, -3, 12, 17, 12, -3, 0, 0},
		{0, -2, 3, 6, 7, 6, 3, -2, 0},
		{-21, 14, 39, 54, 59, 54, 39, 14, -21},
		{0, 5, -30, 75, 131, 75, -30, 5, 0},
		{15, -55, 30, 135, 179, 135, 30, -55, 15},
		{0, 0, 0, -1, 0, 1, 0, 0, 0},
		{0, 0, -2, -1, 0, 1, 2, 0, 0},
		{0, -3, -2, -1, 0, 1, 2, 3, 0},
		{-4, -3, -2, -1, 0, 1, 2, 3, 4},
		{0, 0, 1, -8, 0, 8, -1, 0, 0},
		{0, 22, -67, -58, 0, 58, 67, -22, 0},
		{86, -142, -193, -126, 0, 126, 193, 142, -86}
	};

	public static void main(String[] args) throws Exception {
		String file = "KeelingDataSavGol.txt";
		int size = 697;
		// ^hardcoded file information
		Scanner in = new Scanner(new File(file));
		double[] data = getData(in, size);
		int filterKey = inputFilterKey(in);
		int window = inputMovingAverageWindow(in);
		DrawingPanel panel = new DrawingPanel(800, 400);
		Graphics g = panel.getGraphics();
		drawTime(g);
		drawData(g, data);
		data = smoothData(data, window);
		drawData(g, data);
		double[] derivative = filter(data, filterKey);
		drawData(g, derivative);
	}

	public static int inputFilterKey(Scanner console) {
		// provide user prompts to specify Savitsky-Golay coefficients
		console = new Scanner(System.in);
		System.out.println("select Savitsky-Golay filter: ");
		System.out.println("smoothing");
		System.out.println(" quadratic or cubic");
		System.out.println("  0   {  0, 0,-3,12,17,12,-3, 0,  0}");
		System.out.println("  1   {  0,-2, 3, 6, 7, 6, 3,-2,  0}");
		System.out.println("  2   {-21,14,39,54,59,54,39,14,-21}");

		System.out.println(" quartic or quintic");
		System.out.println("  3   { 0,  5,-30, 75,131, 75,-30,  5, 0}");
		System.out.println("  4   {15,-55, 30,135,179,135, 30,-55,15}");

		System.out.println();

		System.out.println("1st derivative");
		System.out.println(" linear or quadratic");
		System.out.println("  5   { 0, 0, 0,-1,0,1,0,0,0}");
		System.out.println("  6   { 0, 0,-2,-1,0,1,2,0,0}");
		System.out.println("  7   { 0,-3,-2,-1,0,1,2,3,0}");
		System.out.println("  8   {-4,-3,-2,-1,0,1,2,3,4}");		

		System.out.println(" cubic or quartic");
		System.out.println("  9   { 0,   0,   1,  -8,0,  8, -1,  0,  0}");
		System.out.println("  10  { 0,  22, -67, -58,0, 58, 67,-22,  0}");
		System.out.println("  11  {86,-142,-193,-126,0,126,193,142,-86}");

		// select filter
		System.out.print("Enter an integer 0 - 11 corresponding to desired filter: ");
		int filterKey = console.nextInt();
		System.out.println();
		return filterKey;
	}

	public static int inputMovingAverageWindow(Scanner console) {
		// provide user prompts to specify Savitsky-Golay coefficients
		console = new Scanner(System.in);
		System.out.println("Enter an integer from 1 - 697 corresponding to the size of");
		System.out.print("the size of the moving average's window in months: ");
		int window = console.nextInt();
		System.out.println();
		return window;
	}

	public static void drawTime(Graphics g) {
		for (int i = 0; i < 697; i += 24) {
			g.drawLine(i, 400, i, 395);
		}
	}

	public static double[] getData(Scanner in, int size) throws Exception {
		// collects data from hardcoded .txt file
		double[] data = new double[size];
		for (int i = 0; i < size; i++) {
			data[i] = in.nextDouble();
		}
		return data;
	}

	public static double[] smoothData(double[] data, int window) {
		if (window == 1) {
			return data;
		}
		double[] smoothedData = new double[data.length];
		for (int i = window / 2; i < data.length - window / 2; i++) {
			double average = 0;
			for (int j = - window / 2; j < window / 2; j++) {
				average += data[i + j];
			}
			smoothedData[i] = average / window;
		}
		return smoothedData;
	}

	public static void drawData(Graphics g, double[] data) {
		// graphs all data stored in double array onto the drawing panel
		for (int i = 0; i < data.length; i++) {
			// graphs a point
			g.drawOval(i, (int) (399 - (data[i] - 310) * 4), 1, 1);
		}
	}

	public static double[] filter(double[] data, int filterKey) {
		double[] derivative = new double[data.length];
		for (int i = 5; i < data.length - 4; i++) {
			double weightedSum = 0;
			for (int j = 0; j < 9; j++) {
				// pass each data point through the filter
				weightedSum += data[i - 5 + j] * filters[filterKey][j];
			}
			derivative[i] = (weightedSum * 4 + 310); //sum is scaled by 4
		}
		for (int i = 0; i < 5; i++) {
			// not enough data to take derivative of these endpoints
			// setting the data to -5 makes it not appear in graph
			derivative[i] = -5;
			derivative[i + data.length - 5] = -5;
		}
		return derivative;
	}

}