package guis;
import db.MyJDBC;
import constants.CommonConstants;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.sound.sampled.*;

public class SnakeGame extends JPanel implements ActionListener, KeyListener, MouseListener {
    private MyJDBC myJDBC;
    private boolean gameStarted = false;
    private String currentPlayerUsername;
    private class Tile {
        int x;
        int y;

        Tile(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    int boardWidth;
    int boardHeight;
    int tileSize = 25;

    Tile snakeHead;
    ArrayList<Tile> snakeBody;

    Tile food;
    Random random;

    int velocityX;
    int velocityY;
    Timer gameLoop;

    boolean gameOver = false;

    JButton pauseButton;
    JButton resumeButton;

    private Image backgroundImage;
    private Clip backgroundMusic;
    private long currentFramePosition; // Variable to store the current frame position of the background music

    public SnakeGame(int boardWidth, int boardHeight) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
        setBackground(Color.black);
        addKeyListener(this);
        addMouseListener(this);
        setFocusable(true);

        myJDBC = new MyJDBC();  // Instantiate MyJDBC
        snakeHead = new Tile(5, 5);
        snakeBody = new ArrayList<>();

        food = new Tile(10, 10);
        random = new Random();
        placeFood();

        velocityX = 1;
        velocityY = 0;

        gameLoop = new Timer(144, this);
        gameLoop.start();

        // Load background image
        backgroundImage = new ImageIcon(this.getClass().getResource("/resources/background.png")).getImage();

        setLayout(null);

        int buttonWidth = 100;
        int buttonHeight = 30;
        int buttonMargin = 5;

        // Right top corner
        int rightX = boardWidth - buttonWidth - buttonMargin;

        pauseButton = new JButton("Pause");
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pauseGame();
            }
        });
        pauseButton.setBounds(rightX, buttonMargin, buttonWidth, buttonHeight);
        add(pauseButton);

        resumeButton = new JButton("Resume");
        resumeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resumeGame();
            }
        });
        resumeButton.setBounds(rightX, buttonMargin + buttonHeight + buttonMargin, buttonWidth, buttonHeight);
        add(resumeButton);

        // Load background music
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(this.getClass().getResource("/resources/taytay.wav"));
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioInputStream);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // Scale the background image to fit the panel size
        g.drawImage(backgroundImage, 0, 0, boardWidth, boardHeight, this);

        // Draw the apple image at the food coordinates with a larger size
        ImageIcon appleIcon = new ImageIcon(this.getClass().getResource("/resources/apple.png"));
        Image appleImage = appleIcon.getImage();
        int appleSize = tileSize + 10; // Adjust the size of the apple
        g.drawImage(appleImage, food.x * tileSize, food.y * tileSize, appleSize, appleSize, this);

        // Set color for snake head
        g.setColor(Color.black);
        g.fillRect(snakeHead.x * tileSize, snakeHead.y * tileSize, tileSize, tileSize);

        // Set color for snake body
        g.setColor(Color.green);
        for (int i = 0; i < snakeBody.size(); i++) {
            Tile snakePart = snakeBody.get(i);
            g.fillRect(snakePart.x * tileSize, snakePart.y * tileSize, tileSize, tileSize);
        }

        // Set color for score font
        g.setColor(Color.black);
        g.setFont(new Font("Chalkboard", Font.PLAIN, 21));
        if (gameOver) {
            g.setColor(Color.red);
            g.drawString("Game Over: " + String.valueOf(snakeBody.size()), tileSize - 20, tileSize);
            g.drawString("Try Again, Loser ", (boardWidth - g.getFontMetrics().stringWidth("Try Again")) / 2,
                    boardHeight / 2);
        } else {
            g.drawString("Score: " + String.valueOf(snakeBody.size()), tileSize - 16, tileSize);
        }
    }

    public void placeFood() {
        food.x = random.nextInt(boardWidth / tileSize);
        food.y = random.nextInt(boardHeight / tileSize);
    }

    public void move() {
        if (collision(snakeHead, food)) {
            snakeBody.add(new Tile(food.x, food.y));
            placeFood();
        }

        for (int i = snakeBody.size() - 1; i >= 0; i--) {
            Tile snakePart = snakeBody.get(i);
            if (i == 0) {
                snakePart.x = snakeHead.x;
                snakePart.y = snakeHead.y;
            } else {
                Tile prevSnakePart = snakeBody.get(i - 1);
                snakePart.x = prevSnakePart.x;
                snakePart.y = prevSnakePart.y;
            }
        }

        snakeHead.x += velocityX;
        snakeHead.y += velocityY;

        for (int i = 0; i < snakeBody.size(); i++) {
            Tile snakePart = snakeBody.get(i);
            if (collision(snakeHead, snakePart)) {
                gameOver = true;
            }
        }

        if (snakeHead.x * tileSize < 0 || snakeHead.x * tileSize > boardWidth
                || snakeHead.y * tileSize < 0 || snakeHead.y * tileSize > boardHeight) {
            gameOver = true;
        }
    }

    public boolean collision(Tile tile1, Tile tile2) {
        return tile1.x == tile2.x && tile1.y == tile2.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            gameLoop.stop();
            stopBackgroundMusic();

            // Debug statement
            System.out.println("Updating score for user: " + currentPlayerUsername);

            // Update the score in the database when the game is over
            MyJDBC.updateScore(currentPlayerUsername, snakeBody.size());
        }

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameOver) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                restartGame();
                repaint();
            }
        } else {
            if (e.getKeyCode() == KeyEvent.VK_UP && velocityY != 1) {
                velocityX = 0;
                velocityY = -1;
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN && velocityY != -1) {
                velocityX = 0;
                velocityY = 1;
            } else if (e.getKeyCode() == KeyEvent.VK_LEFT && velocityX != 1) {
                velocityX = -1;
                velocityY = 0;
            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && velocityX != -1) {
                velocityX = 1;
                velocityY = 0;
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (!gameStarted) {
            // Get player username when typing for the first time
            currentPlayerUsername = getPlayerUsername();
            // Register the player in the database
            MyJDBC.register(currentPlayerUsername, 0); // Initial score is set to 0
        }
    }


    // Add a method to get player username using JOptionPane
    private String getPlayerUsername() {
        String username = JOptionPane.showInputDialog(this, "Enter your username:", "Player Registration", JOptionPane.PLAIN_MESSAGE);

        // Check for null or empty username
        if (username == null || username.trim().isEmpty()) {
            // Handle this case appropriately, e.g., by prompting the user again
            System.out.println("Invalid username entered.");
            return getPlayerUsername();
        }

        return username.trim();
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    public void restartGame() {
        snakeHead = new Tile(5, 5);
        snakeBody.clear();
        placeFood();
        velocityX = 1;
        velocityY = 0;
        gameOver = false;
        gameLoop.restart();
        startBackgroundMusic();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (!gameStarted) {
            // Start the game when clicked for the first time
            gameStarted = true;
            requestFocusInWindow();
        } else if (gameOver) {
            // Restart the game when clicked after game over
            restartGame();
            repaint();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }


    public void pauseGame() {
        gameLoop.stop();
        stopBackgroundMusic();
    }

    public void resumeGame() {
        if (!gameOver) {
            gameLoop.start();
            startBackgroundMusic();
            requestFocusInWindow();
        }
    }

    // Add a method to stop background music
    public void stopBackgroundMusic() {
        if (backgroundMusic.isRunning()) {
            currentFramePosition = backgroundMusic.getMicrosecondPosition(); // Store the current frame position
            backgroundMusic.stop();
        }
    }

    // Add a method to start background music
    public void startBackgroundMusic() {
        if (!backgroundMusic.isRunning()) {
            backgroundMusic.setMicrosecondPosition(currentFramePosition); // Set the frame position to the stored
            // position
            backgroundMusic.start(); // Start the music from the stored position
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    private void updateScoreInDatabase(String username, int score) {


        // Check if the username exists in the database
        if (myJDBC.checkUser(username)) {
            // If the username exists, update the score
            myJDBC.updateScore(username, score);
        } else {
            // Handle the case where the username does not exist (optional)
            System.out.println("Username not found in the database.");
        }
    }



}
