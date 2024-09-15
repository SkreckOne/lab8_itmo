package org.lab6.utils;

import javax.swing.*;
import javax.swing.SpringLayout;
import java.awt.*;

public class SpringUtilities {
    public static void makeCompactGrid(JPanel parent, int rows, int cols, int initialX, int initialY, int xPad, int yPad) {
        SpringLayout layout;
        try {
            layout = (SpringLayout) parent.getLayout();
        } catch (ClassCastException exc) {
            System.err.println("The first argument to makeCompactGrid must use SpringLayout.");
            return;
        }

        Spring xPadSpring = Spring.constant(xPad);
        Spring yPadSpring = Spring.constant(yPad);
        Spring initialXSpring = Spring.constant(initialX);
        Spring initialYSpring = Spring.constant(initialY);
        int max = rows * cols;

        Spring maxWidthSpring = layout.getConstraints(parent.getComponent(0)).getWidth();
        Spring maxHeightSpring = layout.getConstraints(parent.getComponent(0)).getHeight();
        for (int i = 1; i < max; i++) {
            SpringLayout.Constraints cons = layout.getConstraints(parent.getComponent(i));

            maxWidthSpring = Spring.max(maxWidthSpring, cons.getWidth());
            maxHeightSpring = Spring.max(maxHeightSpring, cons.getHeight());
        }

        for (int i = 0; i < max; i++) {
            SpringLayout.Constraints cons = layout.getConstraints(parent.getComponent(i));

            cons.setX(Spring.sum(initialXSpring, Spring.scale(xPadSpring, i % cols)));
            cons.setY(Spring.sum(initialYSpring, Spring.scale(yPadSpring, i / cols)));

            cons.setWidth(maxWidthSpring);
            cons.setHeight(maxHeightSpring);
        }
    }
}