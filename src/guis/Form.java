package guis;

import constants.CommonConstants;

import javax.swing.*;
public class Form extends JFrame{
    //create constructor
    public Form (String title) {
     super(title);

     // set the size of the gui
        setSize(520,680);

     // configure gui to end process after closing
        setDefaultCloseOperation(EXIT_ON_CLOSE);

     // set layout to null for fixed poition
        setLayout(null);

     // load gui in the centre of the screen
        setLocationRelativeTo(null);

     // prevent gui from changing the size
        setResizable(false);

     // change the background color of the gui
        getContentPane().setBackground(CommonConstants.PRIMARY_COLOR);
    }
}
