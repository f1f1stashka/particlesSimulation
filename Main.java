import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

import static java.lang.Math.atan;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.cos;
import static java.lang.Math.abs;
import static java.lang.Math.asin;

public class Main {
    public static int _size = 800;
    static Particle[] particles = new Particle[30];

    public static void handleParticlesForces(){
        for (int i1 = 0; i1 < particles.length; i1++){
            Vector[] vectors = new Vector[particles.length];
            Vector averageVector = new Vector(0, 0);
            particles[i1].vector.value = 0;
            particles[i1].vector.angle = 0;
            for(int i2 = 0; i2 < particles.length; i2++){
                vectors[i2] = particles[i2].vector;
            }
            for(int j = 0; j < vectors.length; j++){
                averageVector.value += vectors[j].value;
                averageVector.angle += vectors[j].angle;
            }
            averageVector.value = averageVector.value/vectors.length;
            averageVector.angle = averageVector.angle/vectors.length;
            particles[i1].vector = averageVector;
        }
    }

    public static void tryToAttractParticlesPair(Particle p1, Particle p2) {
        int vx;
        int vy;
        if(isInRadius(p1, p2, 10)){
            // TODO: вынести в метод выбор vx и vy
            if(p2.x < p1.x){
                vx = 1;
            }else if (p2.x > p1.x){
                vx = -1;
            }else{
                vx = 0;
            }

            if(p2.y < p1.y){
                vy = 1;
            }else if (p2.y > p1.y){
                vy = -1;
            }else{
                vy = 0;
            }
        
            moveParticlesDependsOnCharge(p1, p2, vx, vy);
            repulseParticlesIfCollisioning(p1, p2);
        }
    }

    public static boolean isInRadius(Particle p1, Particle p2, int radius){
        return pow((p1.x-p2.x), 2) + pow((p1.y-p2.y), 2) <= radius*radius;
    }

    public static void moveParticlesDependsOnCharge(
        Particle p1, 
        Particle p2, 
        int vx, 
        int vy
    ){
        if(p1.charge == 0 && p2.charge == 0){
            p1.goTo(vx, vy);
            p2.goTo(vx, vy);
        }else if(p1.charge == 0 && p2.charge == 1){
            p1.goTo(vx, vy);
            p2.goTo(vx, vy);
        }else if(p1.charge == 0 && p2.charge == -1){
            p2.goTo(-vx, -vy);
        }else if(p1.charge == 1 && p2.charge == 1){
            p1.goTo(-vx, -vy);
            p2.goTo(-vx, -vy);
        }else if(p1.charge == 1 && p2.charge == -1){
            p1.goTo(vx, vy);
            p2.goTo(vx, vy);
        }else if(p1.charge == -1 && p2.charge == -1){
            p1.goTo(-vx, -vy);
            p2.goTo(-vx, -vy);
        }
    }

    public static void repulseParticlesIfCollisioning(Particle p1, Particle p2) {
        if(p1.x == p2.x){
            p2.goTo(getRandCharge(), getRandCharge());
        }
        if(p1.y == p2.y){
            p2.goTo(getRandCharge(), getRandCharge());
        }
    }

    public static void renderMap(JFrame canvas){
        BufferedImage img = new BufferedImage(_size, _size, BufferedImage.TYPE_INT_ARGB);
        Graphics imgG = img.getGraphics();
        imgG.setColor(Color.black);
        imgG.fillRect(0, 0, _size, _size);

        for(int j = 0; j < particles.length; j++){
            if (particles[j].charge == -1) {
                imgG.setColor(new Color(0x0000ff));
            }
            if (particles[j].charge == 1) {
                imgG.setColor(new Color(0xff0000));
            }
            if (particles[j].charge == 0) {
                imgG.setColor(new Color(0xffffff));
            }
            //imgG.drawOval(particles[j].x-30, particles[j].y-30, 60, 60);
            imgG.fillRect(particles[j].x-1, particles[j].y-1, 2, 2);
        }


        Graphics canvasG = canvas.getGraphics();
        canvasG.drawImage(img, 8, 31, null);
    }

    public static int getRandRange(int mn, int mx){
        return (int) (Math.random() * (mx - mn)) + mn;
    }

    public static int getRandCharge(){
        Random random = new Random();
        if(random.nextBoolean()){
            return 1;
        }
        return -1;
    }

    public static int getAngleFromTwoDots(int x1, int y1, int x2, int y2) {
        return (int) atan2(y2-y1, x2-x1);
    }


    public static void main(String[] args) throws InterruptedException {
        JFrame frame = new JFrame();
        Canvas canvas = new Canvas();
        frame.setSize(_size+16, _size+38);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        for(int i = 0; i < particles.length; i++){
            particles[i] = new Particle(400, 400, getRandRange(-1, 2), 1, new Vector(0, 0));
        }

        while(true){
            //Thread.sleep(100);
            renderMap(frame);
            handleParticlesForces();
        }
    }
}

class Particle{
    int x;
    int y;

    int charge;

    int mass;

    Vector vector;

    Particle(int x, int y, int charge, int mass, Vector vector){
        this.x = x;
        this.y = y;
        this.charge = charge;
        this.mass = mass;
        this.vector = vector;
        System.out.println(charge);
    }

    void goTo(){
        x += cos(toRadians(this.vector.angle))*5;
        y += sin(toRadians(this.vector.angle))*5;
    }
}

class Vector{
    int angle;
    int value;

    Vector(int angle, int value){
        this.angle = angle;
        this.value = value;
    }

    // TODO: \/
    // public static Vector calculateAvgVectorFromArray(Vector[] vectors) {}

    public static Vector calculateAvgVector(Vector vec1, Vector vec2) {
        float angleDif = abs(vec1.angle - vec2.angle);
        int resultVectorValue = (int) (
                pow(vec1.value, 2) + pow(vec2.value, 2) 
                - 2 * vec1.value * vec2.value * cos(180 - angleDif)
            ) / 2;
        int resultVectorAngle = 
            (int) asin(vec2.value * sin(180 - angleDif) / resultVectorValue);

        return new Vector(resultVectorAngle, resultVectorValue);
    }
}
