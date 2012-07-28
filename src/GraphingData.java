import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import javax.swing.*;

public class GraphingData extends JPanel {

    int[] data;
	GraphingData(double[] array){
		this.data = new int[array.length];
                for(int i=0;i<array.length;i++){
		this.data[i]=(int)array[i];
            }
	}
    final int PAD = 40;
	int xtic = 60*3;
	int ytic =10;

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();
        // Draw ordinate.
        g2.draw(new Line2D.Double(1.4*PAD, PAD, 1.4*PAD, h-PAD));
        // Draw abcissa.
        g2.draw(new Line2D.Double(1.4*PAD, h-PAD, 1.4*(w-PAD), h-PAD));
        // Draw labels.
        g2.setFont(new Font("SansSerif",0,11));
        Font font = g2.getFont();

        FontRenderContext frc = g2.getFontRenderContext();
        LineMetrics lm = font.getLineMetrics("0", frc);
        float sh = lm.getAscent() + lm.getDescent();
		final int SPAD = 2;

        g2.setPaint(Color.RED.red.darker());
        // Ordinate label.
        String s = "Score";
        float sy = PAD + ((h - 2*PAD) - s.length()*sh)/2 + lm.getAscent();
		double xInc = (double)(w - 2*PAD)/(data.length-1);
        double scale = (double)(h - 2*PAD)/getMax();
        for(int i = 0; i < s.length(); i++) {
            String letter = String.valueOf(s.charAt(i));
            float sw = (float)font.getStringBounds(letter, frc).getWidth();
            float sx = (PAD - sw)/3 ;
            g2.drawString(letter, sx, sy);
            sy += sh;
        }
        // Abcissa label.
        s = "Time";
        sy = (float)(h - PAD + 3*(PAD - sh)/4 + 1.5*lm.getAscent());
        float sw = (float)font.getStringBounds(s, frc).getWidth();
        float sx = (w - sw)/2;
        g2.drawString(s, sx, sy);
        // Draw lines.

        g2.setPaint(Color.green.darker());
        for(int i = 0; i < data.length-1; i++) {
            double x1 = 1.4*PAD + i*xInc;
            double y1 = h - PAD - scale*(int)data[i];
            double x2 = 1.4*PAD + (i+1)*xInc;
            double y2 = h - PAD - scale*(int)data[i+1];
            g2.draw(new Line2D.Double(x1, y1, x2, y2));
        }
        // Mark data points.
        g2.setPaint(Color.red);
        for(int i = 0; i < data.length; i++) {
            /*double x = PAD + i*xInc;
            double y = h - PAD - scale*(int)data[i];
            g2.fill(new Ellipse2D.Double(x-2, y-2, 4, 4));*/

			if((i+1) % xtic ==0){
				s = String.valueOf((i+1)/60);
				sw = (float)font.getStringBounds(s, frc).getWidth();
				sy = h - PAD + lm.getAscent();
				// For the ordinate you count from the bottom up
				// (component/graphics origin is at upper left).
				// The actual value location on the ordinate will be
				// y = h - PAD - scale*data[i]
				// which you would use if you wanted to draw a tick mark on the
				// ordinate.
				// Offset this to locate the text origin. This usually requires
				// some experimenting. To start you can try something like
				sx = (float)(1.4*PAD + xInc*i);
				g2.drawString(s, sx, sy);
			}
        }

		for (int i=0;i<ytic;i++){
			s = String.valueOf(i*getMax()/ytic);
			sw = (float)font.getStringBounds(s, frc).getWidth();
			sx = (float)(1.4*PAD - sw - SPAD);
			// For the ordinate you count from the bottom up
			// (component/graphics origin is at upper left).
			// The actual value location on the ordinate will be
			// y = h - PAD - scale*data[i]
			// which you would use if you wanted to draw a tick mark on the
			// ordinate.
			// Offset this to locate the text origin. This usually requires
			// some experimenting. To start you can try something like
			sy = (float)(h - PAD - scale*i*getMax()/ytic + lm.getAscent()/2);
			g2.drawString(s, sx, sy);
		}
		// For the x location you start with PAD to move
		// to the graph origin and count "xInc" intervals
		// to the desired value location:
	//	x = PAD + x_axisDataIndex * xInc;
		// The vertical positioning is the same as used to
		// draw the abcissa label:
		//sy = h - PAD + (PAD - sh)/2 + lm.getAscent();
		// Space between axis and label.
    }

    private double getMax() {
        double max = -Integer.MAX_VALUE;
        for(int i = 0; i < data.length; i++) {
            if(data[i] > max)
                max = data[i];
        }
        return max;
    }
}