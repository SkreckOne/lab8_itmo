package org.lab6.gui.models;

import common.models.Organization;
import common.models.Coordinates;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Random;

public class CanvaModel extends JPanel {
    private List<Organization> organizations;
    private static final int SQUARE_SIZE = 50;

    public CanvaModel(List<Organization> organizations) {
        this.organizations = organizations;
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawOrganizations(g);
    }

    private void drawOrganizations(Graphics g) {
        Random random = new Random();
        for (Organization org : organizations) {
            Coordinates coords = org.getCoordinates();
            int x = coords.getX();
            int y = (int) coords.getY();

            if (x >= 0 && y >= 0) {
                g.setColor(getColorByOwnerId(org.getOwnerId(), random));

                g.fillRect(x, y, SQUARE_SIZE, SQUARE_SIZE);

                g.setColor(Color.BLACK);
                g.drawRect(x, y, SQUARE_SIZE, SQUARE_SIZE);

                g.setColor(Color.BLACK);
                g.drawString(org.getName(), x + 5, y + SQUARE_SIZE / 2);
            }
        }
    }

    private Color getColorByOwnerId(int ownerId, Random random) {
        return new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255));
    }

    public void updateOrganizations(List<Organization> organizations) {
        this.organizations = organizations;
        repaint();
    }
}
