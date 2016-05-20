package jnk;
import robocode.*;
import robocode.util.*;
import java.awt.Color;

/**
 * Dodge_Bot - a robot by (Jack Kimball and Patrick and Connor)
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

		// Calculates maximum escape angle of the enemy's linear path
		// with the law of sines and fires at the intersection point
		// with a power level proportional to the enemy's distance.
		double bulletPower = Math.min(400 / e.getDistance(), 3);
    	double headOnBearing = getHeadingRadians() + e.getBearingRadians();
    	double linearBearing = headOnBearing + Math.asin(e.getVelocity() / Rules.getBulletSpeed(bulletPower) * Math.sin(e.getHeadingRadians() - headOnBearing));
    	setTurnGunRightRadians(Utils.normalRelativeAngle(linearBearing - getGunHeadingRadians()));
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
}
