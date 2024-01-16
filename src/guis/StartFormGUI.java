package guis;

import constants.CommonConstants;
import db.MyJDBC;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class StartFormGUI extends Form {
    public StartFormGUI() {
        super("Snake");
        addGuiComponents();
    }

    private void addGuiComponents() {
        // create start label
        JLabel startLabel = new JLabel("Start");

        // configure components x, y position, width, and height relative to the GUI
        startLabel.setBounds(0, 25, 520, 100);

        // change the font color
        startLabel.setForeground(CommonConstants.TEXT_COLOR);

        // Change the font
        startLabel.setFont(new Font("Dialog", Font.BOLD, 40));

        // Center the text
        startLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Add the label to the form
        add(startLabel);

        // create playername label
        JLabel playernameLabel = new JLabel("Playername");
        playernameLabel.setBounds(30, 335, 400, 25);
        playernameLabel.setForeground(CommonConstants.TEXT_COLOR);
        playernameLabel.setFont(new Font("Dialog", Font.PLAIN, 18));

        // create username text field
        JTextField playernameField = new JTextField();
        playernameField.setBounds(30, 365, 450, 55);
        playernameField.setBackground(CommonConstants.SECONDARY_COLOR);
        playernameField.setForeground(CommonConstants.TEXT_COLOR);
        playernameField.setFont(new Font("Dialog", Font.PLAIN, 24));

        add(playernameLabel);
        add(playernameField);

        // create button start
        JButton startButton = new JButton("Start");
        startButton.setFont(new Font("Dialog", Font.BOLD, 18));

        // change the cursor to a hand when hover over the button
        startButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        startButton.setBackground(CommonConstants.TEXT_COLOR);
        startButton.setBounds(125, 520, 250, 50);
        startButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // get player username
                String username = playernameField.getText();

                // validate user input
                if (validateUserInput(username)) {
                    // Check if the username already exists
                    if (!MyJDBC.checkUser(username)) {
                        // If the username doesn't exist, register the user
                        if (MyJDBC.register(username, 0)) { // Assuming the initial score is 0
                            // Dispose of the current StartFormGUI
                            dispose();

                            // Create an instance of SnakeGame and set it up in a JFrame
                            SwingUtilities.invokeLater(() -> {
                                JFrame frame = new JFrame("Snake");
                                frame.setSize(800, 600);
                                frame.setLocationRelativeTo(null);
                                frame.setResizable(false);
                                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                                SnakeGame snakeGame = new SnakeGame(800, 600);
                                frame.add(snakeGame);
                                frame.pack();

                                // Make the frame visible
                                frame.setVisible(true);

                                // Request focus for keyboard input
                                snakeGame.requestFocus();

                                // Start background music
                                snakeGame.startBackgroundMusic();
                            });
                        } else {
                            // Handle registration failure (e.g., show an error message)
                            JOptionPane.showMessageDialog(
                                    StartFormGUI.this,
                                    "Failed to register user. Please try again.",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE
                            );
                        }
                    } else {
                        // Username already exists, display an appropriate message
                        JOptionPane.showMessageDialog(
                                StartFormGUI.this,
                                "Username already exists. Please choose a different username.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                } else {
                    // Display an error message for invalid input
                    JOptionPane.showMessageDialog(
                            StartFormGUI.this,
                            "Invalid username. Please enter a valid username.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //get player username
                String username = playernameField.getText();


            }
        });

        add(startButton);
    }
    private boolean validateUserInput(String username) {
        //all field must have a value
        if(username.length() == 0) return false;
        return true;
    }

}
