package OptimizedQueueModel;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.JFrame;

import sim.display.*;
import sim.engine.SimState;
import sim.portrayal.*;
import sim.portrayal.continuous.ContinuousPortrayal2D;

public class MyQueuesWithUI extends GUIState {
    public Display2D display;
    public JFrame displayFrame;

    public static void main(String[] args) {
        MyQueuesWithUI vid = new MyQueuesWithUI();
        Console c = new Console(vid);
        c.setVisible(true);
    }

    public MyQueuesWithUI() {
        super(new MyQueues(System.currentTimeMillis()));
    }

    public MyQueuesWithUI(SimState state) {
        super(state);
    }

    public static String getName() {
        return "MyQueues";
    }

    public Object getSimulationInspectedObject() {
        return state;
    }

    public Inspector getInspector() {
        Inspector i = super.getInspector();
        i.setVolatile(true);
        return i;
    }

    public void start() {
        super.start();
        setupPortrayals();
    }

    public void load(SimState state) {
        super.load(state);
        setupPortrayals();
    }

    public void setupPortrayals() {
        MyQueues myQueues = (MyQueues) state;
        display.reset();
        display.setBackdrop(Color.white);
        // redraw the display
        display.repaint();
    }

    public void init(Controller c) {
        super.init(c);
        display = new Display2D(600, 600, this);
        display.setClipping(false);
        displayFrame = display.createFrame();
        displayFrame.setTitle("Optimized Queue Model Simulation");
        c.registerFrame(displayFrame);
    }

    public void quit() {
        super.quit();
        if (displayFrame != null) displayFrame.dispose();
        displayFrame = null;
        display = null;
    }

}
