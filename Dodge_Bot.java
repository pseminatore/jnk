package jnk;
import robocode.*;
import robocode.util.*;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D

/**
 * Dodge_Bot - a robot by (Jack Kimball and Partrick)
 */
public class Dodge_Bot extends AdvancedRobot {
	
	double previousEnergy = 100;
    int movementDirection = 1;
    int radarDirection = 1;
    
	public void run() {
		setAdjustRadarForGunTurn(true);
		setColors(Color.green,Color.black,Color.white); // body,gun,radar
		setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
    }
	
	// An effective perfect locking radar. method is called
	// from onScannedRobot later.
	public void radar(ScannedRobotEvent e) {
		double absBearing = getHeadingRadians() + e.getBearingRadians();
		double radarTurn = absBearing - getRadarHeadingRadians();		
		setTurnRadarRightRadians(2.0 * Utils.normalRelativeAngle(radarTurn));
	}	
	
    public void onScannedRobot(ScannedRobotEvent e) {
        // Stay at right angles to the opponent
    	setTurnRight(e.getBearing() + 90 - 30 * movementDirection);
         
        // If the bot has small energy drop, assume it fired
		// then take proportional evasive action.
    	double changeInEnergy = previousEnergy - e.getEnergy();
    	if (changeInEnergy > 0 && changeInEnergy <= 3) {
    		movementDirection = -movementDirection;
    		setAhead((e.getDistance() / 4 + 25) * movementDirection);
    	}
		
    	radar(e);

		
		// with a power level proportional to the enemy's distance.
		//called Maximum Escape Angle (MEA)
		double bulletPower = Math.min(400 / e.getDistance(), 3);

    	//testing guess factor targeting
    	double enemyAbsoluteBearing = e.getBearingRadians() + getHeadingRadians();
    	double enemyLateralVelocity = e.getVelocity() * Math.sin(e.getHeadingRadians() - enemyAbsoluteBearing);
    	int lateralDirection = Math.sin(enemyLateralVelocity);
	double bearingOffset = normalRelativeAngle(...);
	double maxEscapeAngleClockwise = ...;
	double maxEscapeAngleCounterclockwise = ...;
 
	double maxEscapeAngle = (bearingOffset < 0) ?
    	maxEscapeAngleCounterclockwise : maxEscapeAngleClockwise;
	double guessFactor = lateralDirection * bearingOffset / maxEscapeAngle;
	if (getGunHeat() == 0) {
			setFire(bulletPower);
		}
	
    	// Tracks the energy level
    	previousEnergy = e.getEnergy();
  	}
	
	// Needed to reset the radar after a kill in meelee.
	public void onRobotDeath(RobotDeathEvent e) {
		setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
	}
	
	// Do a victory dance when you win.
	public void onWin(WinEvent e) {
		System.out.println("I am the victor! FEAR ME");
		for (int i = 0; i < 50; i++) {
			turnRight(40);
			turnLeft(40);
		}
	}
	// Calculates maximum escape angle of the enemy's linear path
	// with the law of sines and fires at the intersection point
	public double maximumEscapeAngle(){
		double headOnBearing = getHeadingRadians() + e.getBearingRadians();
    		double linearBearing = headOnBearing + Math.asin(e.getVelocity() / Rules.getBulletSpeed(bulletPower) * Math.sin(e.getHeadingRadians() - headOnBearing));
    		double maximumEscapeAngle = Utils.normalRelativeAngle(linearBearing - getGunHeadingRadians()));
    		return maximumEscapeAngle;
	}
	
}
class Wave {
	private double startX;
	private double startY;
	private double power;
	private double bearing;
	private int direction;
	private double timeOfFire;
	private int[] returnChunk;
	
	//set up new wave
	public Wave(double x, double y, double power, double bearing, int direction, double timeOfFire, int[] chunk){
		this.startX = x;
		this.startY = y;
		this.bearing = bearing;
		this.power = power;
		this.direction = direction;
		this.timeOfFire = timeOfFire;
		returnChunk = chunk;
	}
	public double maxEscapeAngle(){
		return Math.asin(8.0/getBulletSpeed()):
	}
	public boolean hit(double oppX, double oppY, double timeOfHit){
		//the wave will have hit the other robot when the radius of the wave exceeds or is equal to the 
		//distance from the starting coordinates to the enemy robot
		//					velocity*time=distance
		if(Point2D.distance(x, y, oppX, oppY)<=((timeOfHit-timeOfFire)*getBulletSpeed){
			float correctAngle = Math.atan(oppX-x, oppY-y);
			float angleOffset = (correctAngle-bearing)
			
			return true;
			
			}
	}
}
