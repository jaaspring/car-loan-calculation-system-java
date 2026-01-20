/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package finalproject;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
/**
 *
 * @author Afifah
 */
public class Styles {

    // Gradient Background Panel
    public static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            // GOLD gradient colors
            Color color1 = new Color(174, 134, 37);  // #AE8625
            Color color2 = new Color(247, 239, 138); // #F7EF8A
            Color color3 = new Color(210, 172, 71);  // #D2AC47
            Color color4 = new Color(237, 201, 103); // #EDC967

            // Apply gradient from LEFT to RIGHT
            GradientPaint gradient = new GradientPaint(0, 0, color1, getWidth() / 3, 0, color2);
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth() / 3, getHeight());

            gradient = new GradientPaint(getWidth() / 3, 0, color2, 2 * getWidth() / 3, 0, color3);
            g2d.setPaint(gradient);
            g2d.fillRect(getWidth() / 3, 0, getWidth() / 3, getHeight());

            gradient = new GradientPaint(2 * getWidth() / 3, 0, color3, getWidth(), 0, color4);
            g2d.setPaint(gradient);
            g2d.fillRect(2 * getWidth() / 3, 0, getWidth() / 3, getHeight());
        }
    }
    
    // Rounded JButton with hover effect, 3D bevel, and gloss
    public static JButton createRoundedButton(String text) {
        return new RoundedButton(text);
    }

    // Custom Rounded Button Class with 3D Effect, Hover & Gloss
    static class RoundedButton extends JButton {

        private boolean hovered = false;

        public RoundedButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setForeground(Color.WHITE);  // White text
            setBackground(new Color(50, 50, 50));  // Dark Gray background
            setFont(new Font("Century Gothic", Font.BOLD, 14));
            setPreferredSize(new Dimension(130, 45)); // Adjust size

            // Mouse Listener for Hover Effect
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hovered = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int arc = height; 

            // Background color change on hover
            Color baseColor = hovered ? new Color(70, 70, 70) : getBackground();
            g2.setColor(baseColor);
            g2.fillRoundRect(0, 0, width, height, arc, arc);

            // Bevel Effect
            g2.setColor(new Color(255, 255, 255, 80)); // Light highlight (top-left)
            g2.drawRoundRect(2, 2, width - 5, height - 5, arc, arc);

            g2.setColor(new Color(0, 0, 0, 100)); // Dark shadow (bottom-right)
            g2.drawRoundRect(3, 3, width - 7, height - 7, arc, arc);

            // Glossy Effect
            GradientPaint gloss = new GradientPaint(0, 0, new Color(255, 255, 255, 50), 
                                                    0, height / 2, new Color(255, 255, 255, 0));
            g2.setPaint(gloss);
            g2.fillRoundRect(5, 5, width - 10, height / 2, arc, arc);

            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
        }
    }
    
    public static class RoundedGradientPanel extends JPanel {
        private int cornerRadius = 36; // 
        private int shadowSize = 8;   // 
        private boolean isHovered = false;

        public RoundedGradientPanel() {
            setOpaque(false); //
            setPreferredSize(new Dimension(320, 180)); 

            // Hover effect
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    isHovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    isHovered = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            // 3D Shadow
            g2d.setColor(new Color(0, 0, 0, 40)); 
            g2d.fillRoundRect(6, 6, width - shadowSize, height - shadowSize, cornerRadius, cornerRadius);

            // **Gradient Background
            GradientPaint gradient = new GradientPaint(0, 0, new Color(174, 134, 37),  // #AE8625
                                                       width, height, new Color(237, 201, 103)); // #EDC967
            g2d.setPaint(gradient);
            g2d.fillRoundRect(0, 0, width - shadowSize, height - shadowSize, cornerRadius, cornerRadius);

            // Hover Glow
            if (isHovered) {
                g2d.setColor(new Color(255, 255, 255, 50)); // Soft light effect
                g2d.fillRoundRect(0, 0, width - shadowSize, height - shadowSize, cornerRadius, cornerRadius);
            }

            g2d.dispose();
            super.paintComponent(g);
        }
    }
    
    public static class RoundedBevelPanel extends JPanel {
        private int cornerRadius = 25; // Roundness
        private int bevelDepth = 5; // 3D depth

        public RoundedBevelPanel() {
            setOpaque(false); // Allow custom painting
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            // 3D Bevel Effect
            g2d.setColor(new Color(255, 255, 255, 120)); // Light highlight (top-left)
            g2d.fillRoundRect(2, 2, width - 4, height - 4, cornerRadius, cornerRadius);

            g2d.setColor(new Color(0, 0, 0, 80)); // Dark shadow (bottom-right)
            g2d.fillRoundRect(bevelDepth, bevelDepth, width - bevelDepth * 2, height - bevelDepth * 2, cornerRadius, cornerRadius);

            // Panel Background Color
            g2d.setColor(getBackground()); // Original background color
            g2d.fillRoundRect(bevelDepth, bevelDepth, width - bevelDepth * 2, height - bevelDepth * 2, cornerRadius, cornerRadius);

            g2d.dispose();
            super.paintComponent(g);
        }
    }
}