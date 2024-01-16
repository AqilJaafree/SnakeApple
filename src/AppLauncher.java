import db.MyJDBC;
import guis.StartFormGUI;

import javax.swing.*;

public class AppLauncher {
    public static void main (String[] args){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run(){
                //instantiate a loginFormGUI obj and make it visible
                new StartFormGUI().setVisible(true);
                //check user test
               // System.out.println(MyJDBC.checkUser("username123"));

                //check register new user
                System.out.println(MyJDBC.register("username12", Double.parseDouble("2")));
            }


            }

        );
    }

}
