import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.Random;

import static java.lang.Math.abs;
import static java.lang.Math.pow;

public class Main {
    public static int _size = 800;
    static Particle[] particles = new Particle[30];

    public static void gravity(){
        int vx;
        int vy;
        for (int i1 = 0; i1 < particles.length; i1++){
            for(int i2 = 0; i2 < particles.length; i2++){
                if(i1 == i2){
                    continue;
                }
                if(isInRadius(particles[i1], particles[i2], 10)){
                    if(particles[i2].x < particles[i1].x){
                        vx = 1;
                    }else if (particles[i2].x > particles[i1].x){
                        vx = -1;
                    }else{
                        vx = 0;
                    }
                    if(particles[i2].y < particles[i1].y){
                        vy = 1;
                    }else if (particles[i2].y > particles[i1].y){
                        vy = -1;
                    }else{
                        vy = 0;
                    }

                    moveParticlesDependsOnCharge(particles[i1], particles[i2], vx, vy);
                        
                    if(particles[i1].x == particles[i2].x){
                        particles[i2].goTo(getRandCharge(), getRandCharge());
                    }
                    if(particles[i1].y == particles[i2].y){
                        particles[i2].goTo(getRandCharge(), getRandCharge());
                    }
                }
            }
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
            return 2;
        }
        return -2;
    }

    public static void main(String[] args) throws InterruptedException {
        JFrame frame = new JFrame();
        Canvas canvas = new Canvas();
        frame.setSize(_size+16, _size+38);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        for(int i = 0; i < particles.length; i++){
            particles[i] = new Particle(400, 400, getRandRange(-1, 2));
        }

        while(true){
            //Thread.sleep(100);
            renderMap(frame);
            gravity();

        }
    }
}

class Particle{
    int x;
    int y;

    int charge = -1;

    Particle(int x, int y, int charge){
        this.x = x;
        this.y = y;
        this.charge = charge;
        System.out.println(charge);
    }

    void goTo(int vX, int vY){
        x -= vX;
        y -= vY;
        if(x > Main._size){
            x = Main._size;
        }
        if(x < 5){
            x = 5;
        }
        if(y > Main._size){
            y = Main._size;
        }
        if(y < 5){
            y = 5;
        }
    }
}