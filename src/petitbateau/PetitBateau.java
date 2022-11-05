
package petitbateau;

import gfx.Screen;
import gfx.SpriteSheet;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import javax.swing.JFrame;

/**
 *
 * @author rotab
 */
public class PetitBateau extends Canvas implements Runnable{
    
    private static final int WIDTH = 180;
    private static final int HEIGHT = WIDTH / 12 * 9;
    private static final int SCALE = 3;
    public static final String NAME = "Little Boat";
       
    
    
    private JFrame frame;
    
    private boolean running = false;
    public int tickCount =0;
    
   
    private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    private int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
    
    private Screen screen;
    
    public PetitBateau() {
        setMinimumSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
        setMaximumSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
        setPreferredSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
       
        frame = new JFrame(NAME);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        
        frame.add(this, BorderLayout.CENTER);
        frame.pack();
        
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    public void init() {
        screen = new Screen(WIDTH, HEIGHT, new SpriteSheet("/spritesheet.png"));
    }
    
        
    public synchronized void start() {
        new Thread(this).start();
        running = true;
    }
    public synchronized void stop() {
        running = false;
    }
    
    
    public void run() {
        
        long lastTime = System.nanoTime();
        double nsPerTick = 10e8/60; // ns : nano second 
        
        int ticks = 0;
        int frames = 0;
        
        long lastTimer = System.currentTimeMillis();
        double delta = 0; //nombre de ns écoulées
        
        
        init();
        
        
        while (running) {
            //System.out.println("hello");
            
            long now = System.nanoTime();
            delta += (now - lastTime)/nsPerTick; //temps e
            lastTime = now;
            boolean shouldRender = true;
            
            while(delta >= 1) {
                ticks++;
                tick();
                delta -= 1;
                shouldRender = true;
            }
            
            try {
                Thread.sleep(2);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            } 
            
            if (shouldRender) {
                frames++;
                render();
            }
            
            if (System.currentTimeMillis() - lastTimer >= 1000) {
                lastTimer += 1000;
                System.out.println(frames+" frames, "+ticks+" ticks");
                frames = 0;
                ticks = 0;
            }
            
        }

    }
    
    public void tick() { //update la logique de jeu, ticks: updates/s
        tickCount++;
        
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = i + tickCount;
        }
    } 
    
    public void render() { //update le rendu du jeu 
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }
        
        
        //screen.render(pixels, 0 , WIDTH);
        
        
        Graphics g = bs.getDrawGraphics();
        
        
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight()); 
        
        g.drawImage(image, 0,0,getWidth(), getHeight(), null);
        
        
        g.dispose();
        bs.show();
    } 
    
    public static void main(String[] args) {
        new PetitBateau().start();
    }
    
}
