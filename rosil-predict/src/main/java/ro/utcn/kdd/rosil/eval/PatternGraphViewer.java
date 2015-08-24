package ro.utcn.kdd.rosil.eval;

import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import org.jgrapht.DirectedGraph;
import org.jgrapht.ext.JGraphXAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.utcn.kdd.rosil.predict.match.MatchedPattern;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.atomic.AtomicBoolean;

public class PatternGraphViewer<V extends MatchedPattern, E> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PatternGraphViewer.class);

    public void showGraphAndWait(DirectedGraph<V, E> patternGraph) {
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

    private JFrame createFrame(DirectedGraph<V, E> patternGraph) {
        final JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setSize(1200, 800);
        final JGraphXAdapter<V, E> adapter = createAdapter(patternGraph);
        patternGraph.vertexSet().stream().filter(node -> node.type == MatchedPattern.Type.BEGIN).forEach(node -> {
            adapter.setCellStyles(mxConstants.STYLE_FILLCOLOR, "A9A9A9", new Object[]{adapter.getVertexToCellMap().get(node)});
        });
        patternGraph.vertexSet().stream().filter(node -> node.type == MatchedPattern.Type.END).forEach(node -> {
            adapter.setCellStyles(mxConstants.STYLE_FILLCOLOR, "FF7F50", new Object[]{adapter.getVertexToCellMap().get(node)});
        });
        final mxGraphComponent graphComponent = new mxGraphComponent(adapter);
        frame.getContentPane().add(graphComponent);
        return frame;
    }

    private JGraphXAdapter<V, E> createAdapter(DirectedGraph<V, E> graph) {
        final JGraphXAdapter<V, E> adapter = new JGraphXAdapter<>(graph);
        final mxFastOrganicLayout mxFastOrganicLayout = new mxFastOrganicLayout(adapter);
        mxFastOrganicLayout.setForceConstant(150);
        mxFastOrganicLayout.execute(adapter.getDefaultParent());
        return adapter;
    }
}
