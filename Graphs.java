/*
 * This program prints out x y coordinates of projectiles
 */
import java.awt.*;
import java.util.*;

public class Graphs {
	public static void main(String[] args) {
		//Take input
		Scanner console = new Scanner(System.in);
		System.out.print("Please enter the start angle in degrees:\t");
		int startAngle = console.nextInt();
		System.out.print("Please enter the final angle in degrees:\t");
		int finalAngle = console.nextInt();
		System.out.print("Please enter the step size in degrees:\t\t");
		int step = console.nextInt();
		DrawingPanel panel = new DrawingPanel(1000, 600);
		Graphics g = panel.getGraphics();

		//Print text
		Font title = new Font("Serif", Font.BOLD, 32);
		g.setFont(title);
		g.drawString("X", 975, 540);
		g.drawString("Y", 20, 80);
		g.drawString("Projectile Motion", 180, 50);
		Font subtitle = new Font("Serif", Font.BOLD, 18);
		g.setFont(subtitle);
		g.drawString("Angles (degrees)", 680, 29);
		Font font = new Font("TimesRoman", Font.PLAIN, 12);
		g.setFont(font);
		for (int i = 0; i < 4; i++) {
			g.drawString(i * 30 + "", 557 + i * 120, 60);
		}

		//Create gradient
		int[][] colorCombos = new int[91][3];
		int red = 1;
		int green = 100;
		int blue = 1;
		for (int i = 0; i < 91; i++) {
			if (i < 45) {
				red += 5;
			}
			else {
				green -= 2;
				red -= 4;
			}
			if (i < 45) {
				blue += 5;
			}
			else {
				blue -= 1;
			}
			colorCombos[i][0] = red;
			colorCombos[i][1] = green;
			colorCombos[i][2] = blue;
			Color colorC = new Color(red, green, blue);
			g.setColor(colorC);
			g.fillRect(560 + 4 * i, 39, 4, 10);
		}

		String padding = "";
		for (int i = 23; i >= 0; i--) {
		//Prints y-axis labels and horizontal reference lines
			g.setColor(Color.black);
			if (i == 0 || i == 4) {
				padding += "  ";
			}
			g.drawString(padding + i * 20, 20, 550 - i * 20 + 5);
			Color colorC = new Color(200, 200, 200);
			g.setColor(colorC);
			g.drawLine(45, 550 - i * 20, 980, 550 - i * 20);
		}

		int adjustment = 0;
		for (int i = 0; i <= 23; i++) {
		//Prints x-axis labels and vertical reference lines
			if (i == 1 || i == 3) {
				adjustment += 1;
			}
			g.setColor(Color.black);
			g.drawString("" + i * 40, 47 + i * 40 - 3 * adjustment, 570);
			Color colorC = new Color(200, 200, 200);
			g.setColor(colorC);
			g.drawLine(50 + i * 40, 555, 50 + i * 40, 75);
		}

		//Create y-axis and x-axis
		g.setColor(Color.black);
		int[][] xPoints = {{45, 55, 50}, {975, 985, 975}};
		int[][] yPoints = {{80, 80, 70}, {545, 550, 555}};
		g.fillPolygon(xPoints[0], yPoints[0], 3);
		g.fillPolygon(xPoints[1], yPoints[1], 3);
		g.drawLine(50, 550, 50, 80);
		g.drawLine(50, 550, 975, 550);


		for (int j = startAngle; j <= finalAngle; j += step) {
		//Plots projectile paths for all intended angles
			int direction = 1;
			double[][] array = points(j);
			for (int i = 0; i < array.length; i++) {
				int size = 2;
				int adjust = 0;
				Color angleC = new Color(colorCombos[j][0], colorCombos[j][1], colorCombos[j][2]);
				g.setColor(angleC);
				//Sets color to match the corresponding angle
				if (Math.round(array.length/2) == i) {
				//When projectile reaches maximum height, plot a bigger point
					size = 10;
					adjust = 5;
				}
				g.fillOval((int) Math.round(array[i][0] * 10) + 50 - adjust, (int) Math.round(600 - 10 * array[i][1]) - 50 - adjust, size, size);
			}
		}
		g.setColor(Color.white);
		g.fillRect(590, 100, 360, 96);
		g.setColor(Color.black);
		g.drawRect(589, 99, 362, 98);
		g.setFont(subtitle);
		g.drawString("My Conjecture", 710, 120);
		g.setFont(font);
		g.drawString("The smoothly varying envelope which contains the trajectories of the", 600, 140);
		g.drawString("projectile resembles a parabola.", 600, 154);
		g.drawString("Meanwhile, the shape formed by the maximum heights for each starting", 600, 168);
		g.drawString("angle resembles an ellipse.", 600, 182);
	}
	public static double[][] points(int angle) {
		//Finds x and y values of projectile given the angle
		//and assuming the velocity is 30 m/s
		double radAngle = Math.toRadians(angle);
		double xVel = 30 * Math.cos(radAngle);
		double yVel = 30 * Math.sin(radAngle);
		double[][] coordinates = new double[(int) (yVel / 9.81 * 2 * 100)][2];
		for (int x = 0; x < coordinates.length; x++) {
			double t = x/100.;
			coordinates[x][0] = t * xVel;
			coordinates[x][1] = yVel * t - .5 * 9.81 * t * t;
			if (coordinates[x][1] < 0) {
				break;
			}
		}
		return coordinates;
	}
}