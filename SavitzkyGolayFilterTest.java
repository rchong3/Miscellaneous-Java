/*
Richard Chong P2

					SAVITSKY-GOLAY DIGITAL DERIVATIVE

When analyzing trends in real-world data, it is often useful to follow the rate of
change, or first derivative. Examples include the employment in a growing (or failing)
company, statistical data for athletes, and trends in carbon emmissions. Real world
data can sometimes be difficult to fit to a differentiable analytical function. Methods
exist, however, to differentiate a digital data set as long as it is of sufficient
signal-to-noise ratio.

One such method uses a Savitsky-Golay* differentiation filter to directly differentiate
a digital data set. Write a program called SavitskyGolayFilterTest to implement this
method using a test data set, which by design has perfectly smooth behavior. We will
subsequently apply this method to the analysis of real-world data.

Generate the test data set using this differentiable analytic function:

y = 12(x/150)^2 - 5e^(-((x-400)/20)^2)

It can be seen that this is the sum of a parabola and a Gaussian, for which an analytic
derivative can easily be found for comparison with the output of your program.

Generate an 800 point test data set and save it in a file called ParabolaPlusGaussian.txt.
One way to do this is to enter the function into Excel, then copy and paste 800 data
points corresponding to x = 0 to 799 into your .txt file. Your program can be hardcoded
to take in this .txt file without any user input.

Use Drawing Panel to display your results. Use an 800 x 400 panel, corresponding to
0 <= x <= 799 and 0 <= y <= 399. With the origin of your graph at (0,399), your plot
should fill the Drawing Panel nicely. Suggestion: for data point symbols, use drawOval
with a width and height of 1.

Savitsky-Golay convolution coefficients, appropriate for least-squares smoothing or
differentiation of evenly spaced digital data over a finite window, are shown below.
There are 12 different sets of coefficients, the largest of which contain 9 coefficients.
Load these coefficients into a 12 x 9 2-D array, padding the 3-, 5-, and 7-coefficient
sets with leading and trailing zeros as necessary to bring the coefficient count to 9 for
all sets. Your program should allow the user to choose any one of these sets by entering
a row index from 0 to 11. You are welcome to experiment with any of these but we will be
using filter 8 for this exercise.

Coefficients for smoothing (not needed for test data)
	quadratic or cubic
0		{-3,12,17,12,-3}
1		{-2,3,6,7,6,3,-2}
2		{-21,14,39,54,59,54,39,14,-21}
	quartic or quintic
3		{5,-30,75,131,75,-30,5}
4		{15,-55,30,135,179,135,30,-55,15}

Coefficients for 1st derivative
	linear or quadratic
5		{-1,0,1}
6		{-2,-1,0,1,2}
7		{-3,-2,-1,0,1,2,3}
8		{-4,-3,-2,-1,0,1,2,3,4}
	cubic or quartic
9		{1,-8,0,8,-1}
10		{22,-67,-58,0,58,67,-22}
11		{86,-142,-193,-126,0,126,193,142,-86}

Provide the user with a prompt to help select the desired set. You are welcome to use
this code, which includes zero padding, for the user prompt:

****************

		// provide user prompts to specify Savitsky-Golay coefficients
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

****************

To avoid array-out-of-bounds complications at the beginning and end of your data,
filter your data starting at x = 5 and ending at 794.

For filter 8, scale your differentiated data by a factor of 4 so it can be visualized
on the same Drawing Panel plot, i.e. your end result should contain two curves: the
original data and its scaled first derivative.

Anticipating analysis of real-world data, assume that the horizontal axis is time,
that each unit in x represents one month, and that the first data point corresponds
to March 1958. Provide tick marks every two years starting in 1960, which should
result in the last tick mark corresponding to 2024.

* https://en.wikipedia.org/wiki/Savitzky%E2%80%93Golay_filter
*/

import java.util.Scanner;
import java.io.*;
import java.awt.*;

public class SavitzkyGolayFilterTest {

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
		String file = "ParabolaPlusGaussian.txt";
		int size = 800;
		// ^hardcoded file information
		Scanner in = new Scanner(new File(file));
		double[] data = getData(in, size);
		int filterKey = input(in);
		DrawingPanel panel = new DrawingPanel(800, 400);
		Graphics g = panel.getGraphics();
		drawData(g, data);
		double[] derivative = filter(data, filterKey);
		drawData(g, derivative);
	}

	public static int input(Scanner console) {
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

	public static double[] getData(Scanner in, int size) throws Exception {
		// collects data from hardcoded .txt file
		double[] data = new double[size];
		for (int i = 0; i < size; i++) {
			in.next();
			data[i] = in.nextDouble();
		}
		return data;
	}

	public static void drawData(Graphics g, double[] data) {
		// graphs all data stored in double array onto the drawing panel
		for (int i = 0; i < data.length; i++) {
			// graphs a point
			g.drawOval(i, (int) (399 - data[i]), 1, 1);
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
			derivative[i] = weightedSum * 4; //sum is scaled by 4
		}
		for (int i = 0; i < 5; i++) {
			// not enough data to take derivative of these endpoints
			// setting the data to -5 makes it not appear in graph
			derivative[i] = -5;
			derivative[i + 795] = -5;
		}
		return derivative;
	}
}

