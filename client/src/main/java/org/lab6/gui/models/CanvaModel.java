package org.lab6.gui.models;

import common.models.Organization;

import common.transfer.Request;
import common.transfer.Response;
import common.utils.ArgumentType;
import org.lab6.Client;
import org.lab6.commands.commands.Show;
import org.lab6.commands.commands.Update;
import org.lab6.utils.SessionHandler;


import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CanvaModel extends JPanel {
    private ArrayList<Organization> organizations;
    private static final int SQUARE_SIZE = 50; // Size of each organization square
    private Client client;

    public CanvaModel(Client client) {
        this.client = client; // Initialize client
        fetchOrganizations(); // Fetch organizations during initialization
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawOrganizations(g);
    }

    private void drawOrganizations(Graphics g) {
        if (organizations == null) return;

        for (Organization org : organizations) {
            // Get coordinates for the object
            int x = org.getCoordinates().getX();
            int y = (int) org.getCoordinates().getY();

            // Ensure coordinates are valid before drawing
            if (x >= 0 && y >= 0) {
                // Set color based on the ownerId
                g.setColor(getColorByOwnerId(org.getOwnerId()));

                // Draw square for the organization
                g.fillRect(x, y, SQUARE_SIZE, SQUARE_SIZE);

                // Outline the square
                g.setColor(Color.BLACK);
                g.drawRect(x, y, SQUARE_SIZE, SQUARE_SIZE);

                // Display organization name inside the square
                g.setColor(Color.BLACK);
                g.drawString(org.getName(), x + 5, y + SQUARE_SIZE / 2);
            }
        }
    }

    // Generate a color based on the owner's ID to differentiate users
    private Color getColorByOwnerId(int ownerId) {
        return new Color((ownerId * 97) % 255, (ownerId * 43) % 255, (ownerId * 67) % 255);
    }

    // Method to fetch organizations using the client
    private void fetchOrganizations() {
        // Example request for fetching organizations
        Map<ArgumentType, Object> args = new HashMap<>();
        args.put(ArgumentType.SESSION, SessionHandler.getSession());

        // Creating a request to fetch organizations
        Request request = new Request(Request.RequestType.DEFAULT, new Show(), args);
        Response response = null;
        try {
            response = client.sendAndReceiveCommand(request);
        } catch (IOException | ClassNotFoundException e){System.out.println(e.getMessage());}

        if (response != null && response.getOrganizations() != null) {
            organizations = new ArrayList<>(response.getOrganizations()); // Assume the response contains the list of organizations
            repaint(); // Redraw the canvas with the new data
        } else {
            organizations = new ArrayList<>(); // Handle case when no organizations are returned
        }
    }

    // Optionally, a method to manually refresh the canvas
    public void refreshOrganizations() {
        fetchOrganizations(); // Refetch and repaint the organizations
    }
}
