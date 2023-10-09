import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 15;    // size of squares
    static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/UNIT_SIZE;
    static final int DELAY = 75;        // speed
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    int bodyParts = 2;
    int applesEaten = 0;
    int appleX;
    int appleY;
    char direction = 'R';   // start direction
    boolean running = false;
    Timer timer;
    Random random;
    boolean gameOverScreen = false;
    JButton restartButton;
    JButton exitButton;
    int selectedButtonIndex = 0;

    GamePanel(){
        random = new Random();
        Dimension dimention = new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT);
        this.setPreferredSize(dimention);
        this.setBackground(new Color(48, 48, 48));
        this.setFocusable(true);
        this.setLayout(null);       // for custom component positioning
        MyKeyAdapter myKeyAdapter = new MyKeyAdapter();
        this.addKeyListener(myKeyAdapter);
        startGame();
    }





    public void startGame(){
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void resetGame() {
        bodyParts = 2;
        applesEaten = 0;
        direction = 'R';
        running = false;
        timer.stop();
        newApple();
        // reset the positions of the snake
        for (int i = 0; i < bodyParts; i++) {
            x[i] = 0;
            y[i] = 0;
        }
        startGame();
        this.remove(restartButton); // Remove the reset button from the panel
        this.remove(exitButton);    // Remove the exit button from the panel
        gameOverScreen = false;     // Hide the game over screen
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g){
        if (running) {
            // show apple
            g.setColor(new Color(229, 184, 244));
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            // show snake
            for (int i = 0; i < bodyParts; i++) {
                // head
                if (i == 0) {
                    g.setColor(new Color(193, 73, 233));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                // body
                } else {
                    //g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
                    g.setColor(new Color(129, 12, 168));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }

            // show final score
            g.setColor(Color.red);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.setColor(Color.white);
            FontMetrics metrics2 = getFontMetrics(g.getFont());
            int scoreX = (SCREEN_WIDTH - metrics2.stringWidth("Score: " + applesEaten)) / 2;
            int scoreY = g.getFont().getSize();
            g.drawString("Score: " + applesEaten, 20, 30);
        }
        else {
            gameOver(g);
        }
    }

    public void newApple(){
        appleX = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE))*UNIT_SIZE;
        appleY = random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE))*UNIT_SIZE;
    }

    public void move(){
        for (int i = bodyParts; i>0; i--){
            x[i] = x[i-1];
            y[i] = y[i-1];
        }

        switch(direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }

    public void checkCollisions(){
        // checks if head collides with body
        for (int i = bodyParts; i>0; i--){
            if ((x[0] == x[i]) && (y[0] == y[i])){
                running = false;
            }
        }
        // checks if head touches boarders
        if (x[0] < 0){
            running = false;
        }
        if (x[0] > SCREEN_WIDTH){
            running = false;
        }
        if (y[0] < 0){
            running = false;
        }
        if (y[0] > SCREEN_HEIGHT){
            running = false;
        }

        if (!running){
            timer.stop();
        }

    }

    public void checkApple(){
        if ((x[0] == appleX) && (y[0] == appleY)){
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    public void gameOver(Graphics g){
        // show game over text
        g.setColor(Color.red);
        g.fillRect(0, 0, SCREEN_WIDTH, 150); // Position the banner at the top
        g.setFont(new Font("Arial", Font.BOLD, 75));
        g.setColor(Color.white);
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        int gameOverX = (SCREEN_WIDTH - metrics1.stringWidth("Game Over")) / 2;
        int gameOverY = 100;
        g.drawString("Game Over", gameOverX, gameOverY);



        // show final score
        g.setColor(Color.red);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        g.setColor(Color.white);
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        int scoreX = (SCREEN_WIDTH - metrics2.stringWidth("Score: " + applesEaten)) / 2;
        int scoreY = gameOverY + 100;
        g.drawString("Score: " + applesEaten, scoreX, scoreY);


        if (!gameOverScreen) {
            // Create the restart button
            restartButton = new JButton("Restart");
            restartButton.setSize(100, 50);
            restartButton.setForeground(Color.white);
            restartButton.setLocation(SCREEN_WIDTH / 2 - 50, scoreY + 250);


            // Create the exit button
            exitButton = new JButton("Exit");
            exitButton.setSize(100, 50);
            exitButton.setForeground(Color.white);
            exitButton.setLocation(SCREEN_WIDTH / 2 - 50, restartButton.getY() + restartButton.getHeight() + 10);


            // Add action listeners to the buttons
            restartButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    resetGame();
                }
            });

            exitButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });

            this.add(restartButton);
            this.add(exitButton);
            gameOverScreen = true;
        }

        // Draw a visual indication of the selected button
        if (selectedButtonIndex == 0) {
            restartButton.setBorder(BorderFactory.createLineBorder(Color.gray, 1));
            exitButton.setBorder(BorderFactory.createEmptyBorder());
        } else {
            exitButton.setBorder(BorderFactory.createLineBorder(Color.gray, 1));
            restartButton.setBorder(BorderFactory.createEmptyBorder());
        }

    }
    private void selectNextButton() {
        selectedButtonIndex = (selectedButtonIndex + 1) % 2;
    }
    private void selectPreviousButton() {
        selectedButtonIndex = (selectedButtonIndex - 1 + 2) % 2;
    }
    private void pressSelectedButton() {
        if (selectedButtonIndex == 0) {
            resetGame();
        } else {
            System.exit(0);
        }
    }






    @Override
    public void actionPerformed(ActionEvent e) {
        if (running){
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }
    public class MyKeyAdapter extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e){
            int keyCode = e.getKeyCode();
            if (gameOverScreen) {
                if (keyCode == KeyEvent.VK_UP) {
                    selectNextButton();
                } else if (keyCode == KeyEvent.VK_DOWN) {
                    selectPreviousButton();
                } else if (keyCode == KeyEvent.VK_ENTER) {
                    pressSelectedButton();
                }
                repaint();
            } else {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        if (direction != 'R') {
                            direction = 'L';
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (direction != 'L') {
                            direction = 'R';
                        }
                        break;
                    case KeyEvent.VK_UP:
                        if (direction != 'D') {
                            direction = 'U';
                        }
                        break;
                    case KeyEvent.VK_DOWN:
                        if (direction != 'U') {
                            direction = 'D';
                        }
                        break;
                    case KeyEvent.VK_ENTER:
                        resetGame();
                        break;
                }
            }
        }
    }
}
