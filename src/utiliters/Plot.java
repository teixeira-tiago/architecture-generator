package utiliters;

import com.panayotis.gnuplot.JavaPlot;
import com.panayotis.gnuplot.terminal.ImageTerminal;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class Plot {

    public void Graph(String source, String graph, String color, int type, int lines) {
        Utiliter util = new Utiliter();
        JavaPlot p = new JavaPlot();
        final ImageTerminal img = new ImageTerminal();
        p.setTerminal(img);
        p.set("terminal", "png giant size 800 600");
        p.setTitle("Variation Statistics between the Generations");
        p.set("xlabel", "\"Generations\"");
        p.set("ylabel", "\"Fitness Values\"");
        p.set("key", "off");
        lines -= 2;
        switch (type) {
            case 0:
                if (!color.equals("")) {
                    color = "lc " + color;
                }
                p.addPlot("\"" + source + "\" u 1:2 w points " + color);
                int i;
                for (i = 3; i <= lines; ++i) {
                    p.addPlot("\"" + source + "\" u 1:" + i + " w points " + color);
                }
                break;
            case 1:
                p.addPlot("\"" + source + "\" u 1:" + (lines + 1) + ":" + (lines + 2) + " w filledcu lc 0");
                break;
        }
        p.addPlot("\"" + source + "\" u 1:" + (lines + 1) + " w lines lc -1");
        p.addPlot("\"" + source + "\" u 1:" + (lines + 2) + " w lines lc -1");
        p.plot();
        util.generatePlotFile(source, graph.substring(0, graph.lastIndexOf(".")) + ".plot", graph, lines, color, false, false);
        util.generatePlotFile(source, graph.substring(0, graph.lastIndexOf(".")) + ".plot", graph, lines, color, true, false);
        try {
            BufferedImage bi = img.getImage();
            ImageIO.write(bi, "png", new File(graph));
        } catch (Exception ex) {
        }
    }
}
