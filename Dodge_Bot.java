package jnk; 
 import robocode.*; 
 import robocode.util.*; 
 import java.awt.Color; 
 import java.awt.Graphics2D; 
 import java.awt.geom.Point2D ;
 import java.util.*; 
 
 
/** 
10  * Dodge_Bot - a robot by (Jack Kimball and Partrick) 
11  */ 
 public class Dodge_Bot extends AdvancedRobot { 
	//number of guess factors wewant to be able to compare 
 	//must be odd number so that 0 is in the middle 
 	public final static int BUCKETS = 31; 
 	double previousEnergy = 100; 
     	int movementDirection = 1; 
     	int radarDirection = 1; 
     	ArrayList<Wave> Waves = new ArrayList<Wave>();
     	static int[] buckets = new int[BUCKETS]; 
      
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
 
 		 
 	// Fire with a power level proportional to the enemy's distance. 
        double bulletPower = Math.min(400 / e.getDistance(), 3); 
        
     	//testing guess factor targeting 
     	double absBearing = e.getBearingRadians() + getHeadingRadians(); 
     	double enemyX = getX() + Math.sin(absBearing) * e.getDistance(); 
 	double enemyY = getY() + Math.cos(absBearing) * e.getDistance(); 
 	for (int i=0; i<Waves.size(); i++){ 
 		Wave currentWave = (Wave)Waves.get(i); 
 		if (currentWave.hit(enemyX, enemyY, getTime())){ 
 			Waves.remove(currentWave); 
 			i--; 
 		} 
 	} 
 	if (e.getVelocity() !=0){ 
 		if((Math.sin(e.getHeadingRadians()-absBearing*e.getVelocity())) < 0){ 
 			radarDirection = -1; 
 		}else{ 
 			radarDirection = 1; 
 		} 
 	} 
 	int[]currentStats = buckets; 
 	//create a new wave object 
 	Wave newWave = new Wave(getX(), getY(), bulletPower, absBearing, radarDirection, getTime(), currentStats); 
 	//best guess will always start out at (BUCKETS-1)/2, or a guess factor of 0, or straight ahead (synonyms) 
 	int bestGuess = 15; 
 	//stepping through and finding the best index in the array 
 	for (int i=0; i<BUCKETS; i++){ 
 		if (currentStats[bestGuess]<currentStats[i]){ 
 			bestGuess=i; 
 		} 
 	} 
 	//undoing the math done in the Wave object to "unpackage" the guess factor 
 	double guessFactor =(double)(bestGuess-(currentStats.length-1)/2) / ((currentStats.length - 1)/2); 
 	double angleOffset=radarDirection*guessFactor*newWave.maxEscapeAngle(); 
 	double gunAdjust = (Utils.normalRelativeAngle(absBearing-getGunHeadingRadians()+angleOffset)); 
 	//set the gun to the newly calculated angle 
 	setTurnGunRightRadians(gunAdjust); 
 	//only fireif the gun is able to 
 	//Also add the new wave to the data 
 	if (getGunHeat() == 0) { 
 		if (setFireBullet(bulletPower) != null){ 
 			setFireBullet(bulletPower); 
 			Waves.add(newWave); 
 		} 
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
        public double getBulletSpeed(double power){
            return 20-power*3;
        }
 	// Calculates maximum escape angle of the enemy's linear path 
 	public double maximumEscapeAngle(ScannedRobotEvent e){ 
                double bulletPower = Math.min(400 / e.getDistance(), 3); 
 		double maximumEscapeAngle = Math.asin(8/getBulletSpeed(bulletPower));
     		return maximumEscapeAngle; 
 	} 
 	public void onPaint(Graphics2D g) { 
 		// Set the paint color to red 
 		g.setColor(new Color(0xff, 0x00, 0x00, 0x80)); 
 		for (Wave w : Waves) { 
 			w.draw(g, getTime()); 
 		} 
 		for (int i = 0; i<=31; i++){ 
 			 
 		} 
 
 		System.out.println("Waves: " + Waves.size()); 
 	 
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
 	public Wave(double startX, double startY, double power, double bearing, int direction, double timeOfFire, int[] chunk){ 
 		this.startX = startX; 
 		this.startY = startY; 
 		this.bearing = bearing; 
 		this.power = power; 
 		this.direction = direction; 
 		this.timeOfFire = timeOfFire; 
 		this.returnChunk = chunk; 
 	} 
                public double getBulletSpeed(){
            return 20-power*3;
        }
 	public double maxEscapeAngle(){ 
		return Math.asin(8.0/getBulletSpeed()); 
 	} 
 	public boolean hit(double oppX, double oppY, double timeOfHit){ 
 		//the wave will have hit the other robot when the radius of the wave exceeds or is equal to the  
 		//distance from the starting coordinates to the enemy robot 
 		//					velocity*time=distance 
 		if(Point2D.distance(startX, startY, oppX, oppY)<=((timeOfHit-timeOfFire)*getBulletSpeed())){ 
 			float correctAngle = (float)Math.atan2(oppX-startX, oppY-startY); 
 			float angleOffset = (float)(correctAngle-bearing); 
 			float guessFactor= (float)Math.max((float)-1.0, Math.min(1, angleOffset/maxEscapeAngle())) * direction;
 			int index = (int)Math.round((returnChunk.length -1)/2 *(guessFactor +1)); 
 			returnChunk[index]++; 
 			return true; 
 			} 
 		return false; 
 	} 
 	public void draw(Graphics2D g, long time){ 
 		int r=(int)((time-timeOfFire)*getBulletSpeed()); 
 		int x=(int)startX-r;
 		int y= (int)startY-r; 
 		g.drawOval(x, y, 2*r, 2*r); 
 		g.drawLine((int)startX, (int)startY, (int)(startX + r * Math.sin(bearing)), (int)(startY + r * Math.cos(bearing))); 
 
 	} 
 } 
 }
