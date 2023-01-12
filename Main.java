import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

import static java.lang.Math.*;
import static java.util.Random.*;

public class Main {
    public static int _size = 800;
    static Particle[] particles = new Particle[15];

    public static void goAllParticles(){
        for(int i = 0; i < particles.length; i++){
            particles[i].goTo();
        }
    }

    public static void handleParticlesForces(){
        for (int i1 = 0; i1 < particles.length; i1++){
            for(int i2 = 0; i2 < particles.length; i2++){
                if(i2 == i1){
                    continue;
                }
                if(isInRadius(particles[i1], particles[i2], 30)){

                    //particles[i1].vector = Vector.getAverageVector(particles[i2].vector, particles[i1].vector);
                    if(particles[i1].charge == -1 && particles[i2].charge == 1) {
                        particles[i1].vector.angle = getAngleFromDots(particles[i2].x - particles[i1].x, particles[i2].y - particles[i1].y)-270;
                        particles[i1].vector.value = 0.5;
                        particles[i1].goTo();
                        particles[i1].vector.value = 1;
                    } else if(particles[i2].charge == -1 && particles[i1].charge == 1){
                        particles[i2].vector.angle = getAngleFromDots(particles[i1].x - particles[i2].x, particles[i1].y - particles[i2].y)-270;
                        particles[i2].vector.value = 0.5;
                        particles[i2].goTo();
                        particles[i2].vector.value = 1;
                    }
                }
            }

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
            p1.goTo();
            p2.goTo();
        }else if(p1.charge == 0 && p2.charge == 1){
            p1.goTo();
            p2.goTo();
        }else if(p1.charge == 0 && p2.charge == -1){
            p2.goTo();
        }else if(p1.charge == 1 && p2.charge == 1){
            p1.goTo();
            p2.goTo();
        }else if(p1.charge == 1 && p2.charge == -1){
            p1.goTo();
            p2.goTo();
        }else if(p1.charge == -1 && p2.charge == -1){
            p1.goTo();
            p2.goTo();
        }
    }

    public static void repulseParticlesIfCollisioning(Particle p1, Particle p2) {
        if(p1.x == p2.x){
            p2.goTo();
        }
        if(p1.y == p2.y){
            p2.goTo();
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
            imgG.drawOval(particles[j].x-30, particles[j].y-30, 60, 60);
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

    public static double getAngleFromDots(int x, int y){
        try{
            return (double) (acos(y/sqrt(x*x+y*y))*y/abs(y)*180)/PI;
        }catch (ArithmeticException e){
            return 90;
        }

    }

    //public static int getLengthFromDots(int x1, int y1, int x2, int y2){

    //}


    public static void main(String[] args) throws InterruptedException {

        JFrame frame = new JFrame();
        Canvas canvas = new Canvas();
        frame.setSize(_size+16, _size+38);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        for(int i = 0; i < particles.length; i++){
            particles[i] = new Particle(getRandRange(100, 700), getRandRange(100, 700), getRandCharge(), 1, new Vector(getRandRange(0, 360), 5));
        }
        while(true){
            Thread.sleep(100);
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
        System.out.println(vector.angle);
    }

    void goTo(){
        if(vector.angle > 360){
            vector.angle -= 360;
        }else if(vector.angle < 0){
            vector.angle += 360;
        }else if(Double.isNaN(vector.angle)){
            vector.angle = 0;
        }
        x += sin(toRadians(this.vector.angle))*this.vector.value;
        y += cos(toRadians(this.vector.angle))*this.vector.value;
    }
}

class Vector{
    double angle;

    double value;

    Vector(double angle, double value){
        this.angle = angle;
        this.value = value;
    }

    public static Vector getAverageVector(Vector vector1, Vector vector2){
        double jAngle = (int) (pow(abs(vector1.angle), 2) + pow(abs(vector2.angle), 2) - 2 * abs(vector1.angle) * abs(vector2.angle) * cos(toRadians(180-(vector2.angle-vector1.angle))));
        double jValue = vector2.value + vector1.value;
        return new Vector(jAngle, jValue);
    }
}