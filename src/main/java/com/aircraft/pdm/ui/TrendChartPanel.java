package com.aircraft.pdm.ui;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayDeque;
import java.util.Deque;

public class TrendChartPanel extends JPanel {
    private final Deque<Double> tempSeries = new ArrayDeque<>();
    private final Deque<Double> vibrationSeries = new ArrayDeque<>();
    private final int maxPoints = 80;

    public TrendChartPanel() {
        setPreferredSize(new Dimension(500, 220));
        setBackground(Color.WHITE);
    }

    public void append(double temp, double vibration) {
        appendValue(tempSeries, temp);
        appendValue(vibrationSeries, vibration * 180); // Scaled for visual alignment.
        repaint();
    }

    private void appendValue(Deque<Double> series, double value) {
        series.addLast(value);
        if (series.size() > maxPoints) {
            series.removeFirst();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int pad = 24;
        g2.setColor(new Color(235, 239, 245));
        g2.drawRect(pad, pad, w - 2 * pad, h - 2 * pad);

        drawSeries(g2, tempSeries, new Color(220, 53, 69), pad, w, h);
        drawSeries(g2, vibrationSeries, new Color(0, 123, 255), pad, w, h);

        g2.setColor(new Color(60, 72, 88));
        g2.drawString("Temp (red) | Vibration scaled (blue)", pad, 16);
        g2.dispose();
    }

    private void drawSeries(Graphics2D g2, Deque<Double> series, Color color, int pad, int w, int h) {
        if (series.size() < 2) {
            return;
        }
        double min = series.stream().mapToDouble(Double::doubleValue).min().orElse(0);
        double max = series.stream().mapToDouble(Double::doubleValue).max().orElse(1);
        if (max - min < 0.0001) {
            max += 1;
        }

        int plotW = w - 2 * pad;
        int plotH = h - 2 * pad;
        double stepX = plotW / (double) (maxPoints - 1);

        g2.setColor(color);
        g2.setStroke(new BasicStroke(2f));

        Double[] arr = series.toArray(Double[]::new);
        for (int i = 1; i < arr.length; i++) {
            int x1 = (int) (pad + (i - 1) * stepX);
            int x2 = (int) (pad + i * stepX);
            int y1 = (int) (h - pad - ((arr[i - 1] - min) / (max - min)) * plotH);
            int y2 = (int) (h - pad - ((arr[i] - min) / (max - min)) * plotH);
            g2.drawLine(x1, y1, x2, y2);
        }
    }
}
