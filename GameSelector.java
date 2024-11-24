import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class GameSelector extends Frame {
    private Panel leftPanel, rightPanel,maiPanel;
    private Button hanoiButton, snakeButton;
    private Canvas hanoiCanvas, snakeCanvas;
    private BufferedImage hanoiImage, snakeImage;
    private static final int BUTTON_HEIGHT = 50;

    public GameSelector() {
        setTitle("Game Selector");
        setSize(1000,700);
        setLayout(new GridLayout(1, 2));

        // Load images
        try {
            hanoiImage = ImageIO.read(new File("hanoi.png"));
            snakeImage = ImageIO.read(new File("snake.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create image canvases
        hanoiCanvas = new Canvas() {
            @Override
            public void paint(Graphics g) {
                if (hanoiImage != null) {
                    g.drawImage(hanoiImage, 0, 0, getWidth(), getHeight() - BUTTON_HEIGHT, this);
                }
            }
        };

        snakeCanvas = new Canvas() {
            @Override
            public void paint(Graphics g) {
                if (snakeImage != null) {
                    g.drawImage(snakeImage, 150, 180, 300, 300 - BUTTON_HEIGHT, this);
                }
            }
        };
        
        // Create panels
        leftPanel = new Panel();
        rightPanel = new Panel();
        leftPanel.setLayout(new BorderLayout());
        rightPanel.setLayout(new BorderLayout());

        // Create buttons
        hanoiButton = createStyledButton("Start Tower of Hanoi");
        snakeButton = createStyledButton("Start Snake Game");

        // Add button action listeners
        hanoiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new TowerOfHanoiGame();
            }
        });

        snakeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                App.main(new String[]{});
            }
        });

        // Add mouse listeners to canvases for game launch
        hanoiCanvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new TowerOfHanoiGame();
            }
        });

        snakeCanvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                App.main(new String[]{});
            }
        });

        // Set cursor for canvases
        hanoiCanvas.setCursor(new Cursor(Cursor.HAND_CURSOR));
        snakeCanvas.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Create button panels
        Panel leftButtonPanel = new Panel();
        Panel rightButtonPanel = new Panel();
        
        leftButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        rightButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        
        leftButtonPanel.add(hanoiButton);
        rightButtonPanel.add(snakeButton);

        // Add components to panels
        leftPanel.add(hanoiCanvas, BorderLayout.CENTER);
        leftPanel.add(leftButtonPanel, BorderLayout.SOUTH);
        
        rightPanel.add(snakeCanvas, BorderLayout.CENTER);
        rightPanel.add(rightButtonPanel, BorderLayout.SOUTH);

        // Add panels to frame
        add(leftPanel);
        add(rightPanel);

        // Add window listener
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                dispose();
                System.exit(0);
            }
        });

        // Center the window
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setPreferredSize(new Dimension(200, BUTTON_HEIGHT));
        button.setBackground(new Color(50, 50, 50));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent me) {
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            public void mouseExited(MouseEvent me) {
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        return button;
    }

    public static void main(String[] args) {
        new GameSelector();
    }
}