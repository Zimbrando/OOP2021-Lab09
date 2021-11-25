package it.unibo.oop.lab.reactivegui03;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


public class AnotherConcurrentGUI extends JFrame {

    private static final long serialVersionUID = 2L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    
    private final JLabel display = new JLabel();
    private final JButton stop = new JButton("stop");
    private final JButton up = new JButton("up");
    private final JButton down = new JButton("down");
    
    public AnotherConcurrentGUI() {
        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final JPanel panel = new JPanel();
        panel.add(display);
        panel.add(stop);
        panel.add(down);
        panel.add(up);
        this.getContentPane().add(panel);
        this.setVisible(true);
        //this.setLocationByPlatform(true);
        
        final Agent agent = new Agent();
        new Thread(agent).start();
        
        class AgentStopper implements Runnable {

            public void run() {
                try {
                    Thread.sleep(10000);
                    SwingUtilities.invokeLater(() -> {
                        agent.stopCounter();
                        up.setEnabled(false);
                        down.setEnabled(false);
                        stop.setEnabled(false);
                    });
                } catch (InterruptedException e) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(AnotherConcurrentGUI.this, e));                   
                }
            }
            
        }
        
        new Thread(new AgentStopper()).start();
        
        stop.addActionListener(e -> { 
            agent.stopCounter();
            up.setEnabled(false);
            down.setEnabled(false);
        });
        up.addActionListener(e -> agent.setUp(true));
        down.addActionListener(e -> agent.setUp(false));
    }
    
    
    private class Agent implements Runnable {
        
        private volatile boolean stop;
        private int counter;
        private volatile boolean up = true;
        
        public Agent() {
            
        }

        @Override
        public void run() {
            while(!this.stop) {
                
                try {
                    SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.display.setText(String.valueOf(this.counter)));
                    
                    this.counter += this.up ? 1 : -1;
                    Thread.sleep(500);
                    
                } catch (InvocationTargetException e) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(AnotherConcurrentGUI.this, e)); 
                } catch (InterruptedException e) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(AnotherConcurrentGUI.this, e)); 
                }
                
            }
        }
        
        public void stopCounter() {
            this.stop = true;
        }
        
        public void setUp(final boolean up) {
            this.up = up;
        }  
        
    }
}
