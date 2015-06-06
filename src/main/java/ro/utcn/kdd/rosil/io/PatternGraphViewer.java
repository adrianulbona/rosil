package ro.utcn.kdd.rosil.io;

import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.swing.mxGraphComponent;
import org.jgrapht.DirectedGraph;
import org.jgrapht.ext.JGraphXAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.utcn.kdd.rosil.data.Pattern;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.atomic.AtomicBoolean;

public class PatternGraphViewer {
    private static final Logger LOGGER = LoggerFactory.getLogger(PatternGraphViewer.class);

    public void showGraphAndWait(DirectedGraph<Integer, Pattern> patternGraph) {
        final JFrame frame = createFrame(patternGraph);
        frame.setVisible(true);
        try {
            waitUntilClosed(frame);
        } catch (InterruptedException ignored) {
            LOGGER.warn("Interrupted while showing graph.");
        }
    }

    private void waitUntilClosed(JFrame frame) throws InterruptedException {
        final AtomicBoolean windowClosed = new AtomicBoolean(false);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                windowClosed.set(true);
            }
        });
        while (!windowClosed.get()) {
            Thread.sleep(1000);
        }
    }

    private JFrame createFrame(DirectedGraph<Integer, Pattern> patternGraph) {
        final JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);
        final mxGraphComponent graphComponent = new mxGraphComponent(createAdapter(patternGraph));
        frame.getContentPane().add(graphComponent);
        return frame;
    }

    private JGraphXAdapter<Integer, Pattern> createAdapter(DirectedGraph<Integer, Pattern> graph) {
        final JGraphXAdapter<Integer, Pattern> adapter = new JGraphXAdapter<>(graph);
        final mxFastOrganicLayout mxFastOrganicLayout = new mxFastOrganicLayout(adapter);
        mxFastOrganicLayout.setForceConstant(150);
        mxFastOrganicLayout.execute(adapter.getDefaultParent());
        return adapter;
    }
}
