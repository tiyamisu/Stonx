package com.stonx.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Reusable Modern Card Panel.
 * Supports rounded corners, anti-aliasing, accent colored border/strip, and hover background transitions.
 */
public class ModernCardPanel extends JPanel {
    private final int cornerRadius;
    private Color normalBg;
    private Color hoverBg;
    private final Color borderColor;
    private Color accentStripColor;
    private boolean isHovered = false;
    private boolean hoverEnabled = true;

    public ModernCardPanel() {
        this(16);
    }

    public ModernCardPanel(int cornerRadius) {
        this.cornerRadius = cornerRadius;
        this.normalBg = new Color(25, 25, 25);
        this.hoverBg = new Color(32, 32, 32);
        this.borderColor = new Color(45, 45, 45);
        this.accentStripColor = null;

        setOpaque(false);
        setBackground(normalBg);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (hoverEnabled) {
                    isHovered = true;
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (hoverEnabled) {
                    isHovered = false;
                    repaint();
                }
            }
        });
    }

    public void setNormalBg(Color color) {
        this.normalBg = color;
        setBackground(color);
        repaint();
    }

    public void setHoverBg(Color color) {
        this.hoverBg = color;
    }

    public void setHoverEnabled(boolean enabled) {
        this.hoverEnabled = enabled;
    }

    public void setAccentStripColor(Color color) {
        this.accentStripColor = color;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Paint background
        g2.setColor(isHovered ? hoverBg : normalBg);
        g2.fillRoundRect(0, 0, width - 1, height - 1, cornerRadius, cornerRadius);

        // Paint border
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(0, 0, width - 1, height - 1, cornerRadius, cornerRadius);

        // Paint accent strip if present
        if (accentStripColor != null) {
            g2.setColor(accentStripColor);
            g2.fillRoundRect(0, 0, 6, height - 1, cornerRadius, cornerRadius);
            // Clean up back corners so accent is only on left
            g2.fillRect(3, 0, 3, height - 1);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}
