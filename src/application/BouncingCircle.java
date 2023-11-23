package application;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
public class BouncingCircle extends Application {
	
    private static final int WIDTH = 600;
    private static final int HEIGHT = 600;
    private static final int OUTER_CIRCLE_RADIUS = 290;
    private static int INNER_CIRCLE_RADIUS = 20;
    private double centerX = WIDTH / 2.0;
    private double centerY = HEIGHT / 2.0;
    private double circleX = centerX;
    private double circleY = centerY - OUTER_CIRCLE_RADIUS + INNER_CIRCLE_RADIUS;
    private double speedX = 3;
    private double speedY = 3;
    private GraphicsContext gc;
    private AudioClip dingSound;
    private Timer timer = new Timer();
    private Color ballColor = Color.WHITE;
    private List<Double> trailX = new ArrayList<>();
    private List<Double> trailY = new ArrayList<>();
    private List<Color> trailColors = new ArrayList<>();
    private Color trailColor = getRandomColor();
    private List<AudioClip> sounds1 = new ArrayList<>();
    private List<AudioClip> currentSounds;
    private int currentSoundIndex = 0;
    private int sequenceCount = 0;
    
    

    public static void main(String[] args) {
        launch(args);
    }
   
    public void start(Stage primaryStage) {
    	 sounds1.add(new AudioClip(getClass().getResource("fa.wav").toString()));
         sounds1.add(new AudioClip(getClass().getResource("la.wav").toString()));
         sounds1.add(new AudioClip(getClass().getResource("si.wav").toString()));
         sounds1.add(new AudioClip(getClass().getResource("do.wav").toString()));
         sounds1.add(new AudioClip(getClass().getResource("mi.wav").toString()));
         sounds1.add(new AudioClip(getClass().getResource("do.wav").toString()));
         sounds1.add(new AudioClip(getClass().getResource("si.wav").toString()));
         sounds1.add(new AudioClip(getClass().getResource("la.wav").toString()));
         // Load the sounds for the second sequence
      
         
         currentSounds = sounds1;
        primaryStage.setTitle("Bouncing Circle");
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        gc = canvas.getGraphicsContext2D();
        dingSound = new AudioClip(getClass().getResource("ding.mp3").toString());
        StackPane root = new StackPane();
        root.getChildren().add(canvas);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();  double angle = Math.random() * 2 * Math.PI;
        // Generate a random radius within the bounds of the outer circle
        double radius = Math.random() * OUTER_CIRCLE_RADIUS;
        // Calculate the starting position of the ball
        circleX = centerX + radius * Math.cos(angle);
        circleY = centerY + radius * Math.sin(angle);
        
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                draw();
            }
        }.start();
    }
    private Color getRandomColor() {
        return Color.color(Math.random(), Math.random(), Math.random());
    }
    private void update() {
    	double oldX = circleX;
        double oldY = circleY;
        circleX += speedX;
        circleY += speedY;
        // Check for collision with the outer circle
        double dx = circleX - centerX;
        double dy = circleY - centerY;
        double distance = Math.sqrt(dx*dx + dy*dy);
        if (distance > OUTER_CIRCLE_RADIUS - INNER_CIRCLE_RADIUS) {
            // Current speed
            double v = Math.sqrt(speedX*speedX + speedY*speedY);
            // Angle from center of large circle to center of small circle,
            // which is the same as angle from center of large circle
            // to the collision point
            double angleToCollisionPoint = Math.atan2(-dy, dx);
            // Angle of the current movement
            double oldAngle = Math.atan2(-speedY, speedX);
            // New angle
            double newAngle = 2 * angleToCollisionPoint - oldAngle;
            // New x/y speeds, using current speed and new angle
            speedX = -v * Math.cos(newAngle);
            speedY = v * Math.sin(newAngle);
            // Add a small random acceleration
            speedX += (Math.random() * 1) * 0.8;
            speedY += (Math.random() * 1) * 0.8;
            
          
            // Increase the radius of the inner circle
            INNER_CIRCLE_RADIUS+=3;
            // Adjust position to be on the boundary of the outer circle
            double angle = Math.atan2(dy, dx);
            circleX = centerX + (OUTER_CIRCLE_RADIUS - INNER_CIRCLE_RADIUS) * Math.cos(angle);
            circleY = centerY + (OUTER_CIRCLE_RADIUS - INNER_CIRCLE_RADIUS) * Math.sin(angle);
            // Play ding sound
            sounds1.get(currentSoundIndex).play();
            // Move to the next sound
            currentSoundIndex++;
            // If we've played all the sounds in the current sequence
            if (currentSoundIndex >= sounds1.size()) {
                // Reset the sound index
                currentSoundIndex = 0;
                // Increase the sequence count
                sequenceCount++;
                // If we've played the current sequence twice
                if (sequenceCount == 2) {
                    // Shuffle the sounds
                    Collections.shuffle(sounds1);
                    // Reset the sequence count
                    sequenceCount = 0;
                }
            }            // Move to the next sound
           

            trailColor = getRandomColor();
            

        }
        trailX.add(oldX);
        trailY.add(oldY);
        trailColors.add(trailColor);

    }
    
    private void draw() {
        // Set background color to black
        gc.setFill(Color.BLACK);
        // Clear the canvas
        gc.fillRect(0, 0, WIDTH, HEIGHT);
        // Set line width for trails
        gc.setLineWidth(3.0);
        // Draw trails
        for (int i = 0; i < trailX.size(); i++) {
            gc.setStroke(trailColors.get(i));
            if (i < trailX.size() - 1) {
                gc.strokeLine(trailX.get(i), trailY.get(i), trailX.get(i+1), trailY.get(i+1));
            }
        }
        // Set stroke color to white for outer circle
        gc.setStroke(Color.WHITE);
        // Draw outer circle
        gc.strokeOval(centerX - OUTER_CIRCLE_RADIUS, centerY - OUTER_CIRCLE_RADIUS, OUTER_CIRCLE_RADIUS * 2, OUTER_CIRCLE_RADIUS * 2);
        // Draw inner circle
        gc.setFill(ballColor);
        gc.fillOval(circleX - INNER_CIRCLE_RADIUS, circleY - INNER_CIRCLE_RADIUS, INNER_CIRCLE_RADIUS * 2, INNER_CIRCLE_RADIUS * 2);
    }
}