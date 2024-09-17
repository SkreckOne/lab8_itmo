package org.lab6.gui.models;

import common.models.Organization;
import common.transfer.Request;
import common.transfer.Response;
import common.utils.ArgumentType;
import org.lab6.Client;
import org.lab6.commands.commands.Show;
import org.lab6.utils.SessionHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CanvaModel extends JPanel {
    private final Map<Integer, Organization> drawnOrganizations = new HashMap<>();
    private ArrayList<Organization> organizations = new ArrayList<>();
    private static final int SQUARE_SIZE = 25;
    private static int canvasWidth = 1000;
    private static int canvasHeight = 1000;
    private final Client client;

    public CanvaModel(Client client) {
        this.client = client;
        setPreferredSize(new Dimension(canvasWidth, canvasHeight));
        setBackground(Color.WHITE);

        // Add a listener for window resizing
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                canvasWidth = getWidth();
                canvasHeight = getHeight();
                revalidate();
                repaint();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e.getX(), e.getY());
            }
        });

        startOrganizationUpdater();
    }

    private void startOrganizationUpdater() {
        new SwingWorker<List<Organization>, Void>() {

            @Override
            protected List<Organization> doInBackground() throws Exception {
                while (!isCancelled()) {
                    Thread.sleep(6000);
                    return fetchOrganizations();
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    List<Organization> updatedOrganizations = get();
                    if (updatedOrganizations != null) {
                        processOrganizationChanges(updatedOrganizations);
                    }
                } catch (InterruptedException | ExecutionException e) {
                    System.out.println(e.getMessage());
                }
                startOrganizationUpdater();
            }
        }.execute();
    }

    private void processOrganizationChanges(List<Organization> updatedOrganizations) {
        Set<Integer> updatedIds = new HashSet<>();

        for (Organization org : updatedOrganizations) {
            updatedIds.add(org.getId());
            if (!drawnOrganizations.containsKey(org.getId())) {
                drawNewOrganizationWithAnimation(org);
            } else if (!drawnOrganizations.get(org.getId()).equals(org)) {
                updateOrganization(org);
            }
        }

        for (Integer id : new HashSet<>(drawnOrganizations.keySet())) {
            if (!updatedIds.contains(id)) {
                removeOrganization(drawnOrganizations.get(id));
            }
        }

        drawnOrganizations.clear();
        for (Organization org : updatedOrganizations) {
            drawnOrganizations.put(org.getId(), org);
        }

        organizations = new ArrayList<>(updatedOrganizations);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (Organization org : organizations) {
            int originalX = org.getCoordinates().getX();
            long originalY = org.getCoordinates().getY();
            int x = (originalX % canvasWidth + canvasWidth) % canvasWidth;
            int y = (int) ((originalY % canvasHeight + canvasHeight) % canvasHeight);

            repaintOrganizationAt(x, y, org, g);
        }
    }

    private void drawNewOrganizationWithAnimation(Organization org) {
        int originalX = org.getCoordinates().getX();
        long originalY = org.getCoordinates().getY();
        int x = (originalX % canvasWidth + canvasWidth) % canvasWidth;
        int y = (int) ((originalY % canvasHeight + canvasHeight) % canvasHeight);

        new SwingWorker<Void, int[]>() {
            final int animationDuration = 4000;
            final int frameDelay = 100;
            final int totalFrames = animationDuration / frameDelay;

            @Override
            protected Void doInBackground() throws Exception {
                for (int frame = 0; frame < totalFrames; frame++) {
                    int offsetX = (frame % 2 == 0) ? -5 : 5;
                    int[] position = {x + offsetX, y};

                    publish(position);

                    Thread.sleep(frameDelay);
                }
                return null;
            }

            @Override
            protected void process(List<int[]> positions) {
                int[] currentPosition = positions.get(positions.size() - 1);
                int[] lastPosition = positions.size() > 1 ? positions.get(positions.size() - 2) : currentPosition;

                clearOrganizationAt(lastPosition[0], lastPosition[1]);
                repaintOrganizationWithoutText(currentPosition[0], currentPosition[1], org);
            }

            @Override
            protected void done() {
                clearOrganizationAt(x, y);
                repaintOrganizationAt(x, y, org);
            }
        }.execute();
    }

    private void repaintOrganizationWithoutText(int x, int y, Organization org) {
        Graphics g = getGraphics();
        if (g != null) {
            g.setColor(getColorByOwnerId(org.getOwnerId()));
            g.fillRect(x, y, SQUARE_SIZE, SQUARE_SIZE);
            g.setColor(Color.BLACK);
            g.drawRect(x, y, SQUARE_SIZE, SQUARE_SIZE);
        }
    }

    private void updateOrganization(Organization org) {
        int originalX = org.getCoordinates().getX();
        long originalY = org.getCoordinates().getY();
        int x = (originalX % canvasWidth + canvasWidth) % canvasWidth;
        int y = (int) ((originalY % canvasHeight + canvasHeight) % canvasHeight);

        repaintOrganizationAt(x, y, org);
    }

    private void clearOrganizationAt(int x, int y) {
        Graphics g = getGraphics();
        if (g != null) {
            g.clearRect(x, y, SQUARE_SIZE, SQUARE_SIZE);
        }
    }

    private void removeOrganization(Organization org) {
        int originalX = org.getCoordinates().getX();
        long originalY = org.getCoordinates().getY();
        int x = (originalX % canvasWidth + canvasWidth) % canvasWidth;
        int y = (int) ((originalY % canvasHeight + canvasHeight) % canvasHeight);

        Graphics g = getGraphics();
        if (g != null) {
            g.clearRect(x, y, SQUARE_SIZE, SQUARE_SIZE);
        }
    }

    private void repaintOrganizationAt(int x, int y, Organization org) {
        Graphics g = getGraphics();
        repaintOrganizationAt(x, y, org, g);
    }

    private void repaintOrganizationAt(int x, int y, Organization org, Graphics g) {
        if (g != null) {
            g.setColor(getColorByOwnerId(org.getOwnerId()));
            g.fillRect(x, y, SQUARE_SIZE, SQUARE_SIZE);
            g.setColor(Color.BLACK);
            g.drawRect(x, y, SQUARE_SIZE, SQUARE_SIZE);
            g.setColor(Color.BLACK);
            g.drawString(org.getFullName(), x + 5, y + SQUARE_SIZE / 2);
        }
    }

    private Color getColorByOwnerId(int ownerId) {
        return new Color((ownerId * 97) % 255, (ownerId * 43) % 255, (ownerId * 67) % 255);
    }

    private void handleMouseClick(int clickX, int clickY) {
        for (Organization org : organizations) {
            int originalX = org.getCoordinates().getX();
            long originalY = org.getCoordinates().getY();

            int x = (originalX % canvasWidth + canvasWidth) % canvasWidth;
            int y = (int) ((originalY % canvasHeight + canvasHeight) % canvasHeight);

            if (clickX >= x && clickX <= x + SQUARE_SIZE &&
                    clickY >= y && clickY <= y + SQUARE_SIZE) {
                JOptionPane.showMessageDialog(this, getOrganizationInfo(org), "Organization Info", JOptionPane.INFORMATION_MESSAGE);
                break;
            }
        }
    }

    private String getOrganizationInfo(Organization org) {
        return "Name: " + org.getName() +
                "\nCoordinates: " + org.getCoordinates().toString() +
                "\nAnnual Turnover: " + org.getAnnualTurnover() +
                "\nFull Name: " + org.getFullName() +
                "\nType: " + org.getType().toString();
    }

    private List<Organization> fetchOrganizations() {
        Map<ArgumentType, Object> args = new HashMap<>();
        args.put(ArgumentType.SESSION, SessionHandler.getSession());
        Request request = new Request(Request.RequestType.DEFAULT, new Show(), args);

        Response response = null;
        try {
            response = client.sendAndReceiveCommand(request);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error fetching organizations: " + e.getMessage());
        }

        if (response != null && response.getOrganizations() != null) {
            return new ArrayList<>(response.getOrganizations());
        } else {
            return new ArrayList<>();
        }
    }
}