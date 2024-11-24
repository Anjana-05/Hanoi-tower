import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Stack;
import java.util.ArrayList;

public class TowerOfHanoiGame extends JFrame {
    private int numDisks = 3;
    private int moves = 0;
    private Stack<Integer>[] rods;
    private JLabel moveCounter;
    private int dragFromRod = -1;
    private int draggingDisk = -1;
    private int mouseX, mouseY;
    private boolean isDragging = false;
    private boolean isSolving = false;  // For automatic solution

    public TowerOfHanoiGame() {
        setTitle("Tower of Hanoi Game");
        setSize(1000,700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        GamePanel gamePanel = new GamePanel();
        setContentPane(gamePanel);
        setVisible(true);
    }

    private void initializeGame() {
        rods = new Stack[3];
        for (int i = 0; i < 3; i++) rods[i] = new Stack<>();
        for (int i = numDisks; i > 0; i--) rods[0].push(i);
        moves = 0;
        dragFromRod = -1;
        draggingDisk = -1;
        isDragging = false;
        isSolving = false;
        repaint();
    }

    private class GamePanel extends JPanel {
        private JTextField numDisksInput;

        public GamePanel() {
            setLayout(null);
            setBackground(new Color(240, 248, 255));

            JLabel title = new JLabel("Tower of Hanoi", JLabel.CENTER);
            title.setFont(new Font("Serif", Font.BOLD, 36));
            title.setBounds(300, 20, 400, 40);
            add(title);

            JLabel label = new JLabel("Enter number of disks:");
            label.setBounds(380, 100, 200, 30);
            label.setFont(new Font("Serif", Font.PLAIN, 18));
            add(label);

            numDisksInput = new JTextField("3");
            numDisksInput.setBounds(560, 100, 50, 30);
            numDisksInput.setFont(new Font("Serif", Font.PLAIN, 16));
            add(numDisksInput);

            numDisksInput.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        onResetButtonClicked();
                    }
                }
            });

            JButton resetButton = new JButton("Reset");
            resetButton.setBounds(170, 150, 100, 30);
            resetButton.setForeground(Color.BLUE);
            resetButton.addActionListener(e -> onResetButtonClicked());
            add(resetButton);

            JButton solveButton = new JButton("Solve");
            solveButton.setBounds(350, 150, 100, 30);
            solveButton.setForeground(Color.GREEN);
            solveButton.addActionListener(e -> startSolving());
            add(solveButton);

            JButton instructionsButton = new JButton("Instructions");
            instructionsButton.setBounds(690, 150, 150, 30);
            instructionsButton.addActionListener(e -> showInstructions());
            add(instructionsButton);

            moveCounter = new JLabel("Moves: 0");
            moveCounter.setFont(new Font("Serif", Font.PLAIN, 24));
            moveCounter.setBounds(470, 550, 200, 30);
            add(moveCounter);

            initializeGame();

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (isSolving) return;

                    int rodIndex = getRodIndex(e.getX());
                    if (rodIndex != -1 && !rods[rodIndex].isEmpty()) {
                        dragFromRod = rodIndex;
                        draggingDisk = rods[rodIndex].peek();
                        if (draggingDisk != -1) {
                            isDragging = true;
                            mouseX = e.getX();
                            mouseY = e.getY();
                        }
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (isDragging && dragFromRod != -1 && draggingDisk != -1) {
                        int rodIndex = getRodIndex(e.getX());
                        if (rodIndex != -1 && isValidMove(dragFromRod, rodIndex)) {
                            rods[rodIndex].push(rods[dragFromRod].pop());
                            moves++;
                            moveCounter.setText("Moves: " + moves);
                            checkWin();
                        }
                        draggingDisk = -1;
                        dragFromRod = -1;
                        isDragging = false;
                        repaint();
                    }
                }
            });

            addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (isDragging) {
                        mouseX = e.getX();
                        mouseY = e.getY();
                        repaint();
                    }
                }
            });
        }

        private void showWinDialog() {
            JDialog winDialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), "Congratulations!", true);
            winDialog.setLayout(new BorderLayout());
            winDialog.setSize(400, 200);
            winDialog.setLocationRelativeTo(this);

            JPanel messagePanel = new JPanel();
            JLabel messageLabel = new JLabel("Congratulations! You've solved the puzzle in " + moves + " moves!");
            messageLabel.setFont(new Font("Serif", Font.BOLD, 16));
            messagePanel.add(messageLabel);

            JPanel buttonPanel = new JPanel();
            JButton continueButton = new JButton("Continue");
            JButton homeButton = new JButton("Go to Home");

            continueButton.addActionListener(e -> {
                winDialog.dispose();
                initializeGame(); // Reset the game for continuing
            });

            homeButton.addActionListener(e -> {
                winDialog.dispose();
                TowerOfHanoiGame.this.dispose(); // Close the game window
            });

            buttonPanel.add(continueButton);
            buttonPanel.add(homeButton);

            winDialog.add(messagePanel, BorderLayout.CENTER);
            winDialog.add(buttonPanel, BorderLayout.SOUTH);
            winDialog.setVisible(true);
        }

        private void onResetButtonClicked() {
            try {
                numDisks = Integer.parseInt(numDisksInput.getText());
                if (numDisks < 3 || numDisks > 8) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid number of disks (3-8).");
                    return;
                }
                initializeGame();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number.");
            }
        }

        private void showInstructions() {
            String instructions = """
                    Tower of Hanoi Rules:
                    1. Only one disk can be moved at a time.
                    2. A disk can only be placed on a larger disk or an empty rod.
                    3. Goal: Move all disks from the leftmost rod to the rightmost rod.
                    
                    Controls:
                    - Click and drag the top disk to another rod.
                    - Press 'Reset' to start over.
                    - Press 'Solve' to see the automatic solution.
                    
                    Good luck!
                    """;
            JOptionPane.showMessageDialog(this, instructions, "Instructions", JOptionPane.INFORMATION_MESSAGE);
        }

        private int getRodIndex(int x) {
            int rodIndex = (x - 200) / 200;
            return (rodIndex >= 0 && rodIndex < 3) ? rodIndex : -1;
        }

        private boolean isValidMove(int from, int to) {
            return to != from && (rods[to].isEmpty() || rods[from].peek() < rods[to].peek());
        }

        private void checkWin() {
            if (rods[2].size() == numDisks) {
                showWinDialog();
            }
        }

        private void startSolving() {
            isSolving = true;
            ArrayList<String> movesList = solveTowerOfHanoi(numDisks, 0, 2, 1);
            new Thread(() -> {
                for (String move : movesList) {
                    if (!isSolving) break;
                    String[] parts = move.split(",");
                    int from = Integer.parseInt(parts[0]);
                    int to = Integer.parseInt(parts[1]);

                    rods[to].push(rods[from].pop());
                    moves++;
                    moveCounter.setText("Moves: " + moves);
                    repaint();
                    try {
                        Thread.sleep(1000);  // Pause to visualize the move
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
                isSolving = false;
            }).start();
        }

        private ArrayList<String> solveTowerOfHanoi(int n, int from, int to, int aux) {
            ArrayList<String> steps = new ArrayList<>();
            if (n == 1) {
                steps.add(from + "," + to);
                return steps;
            }
            steps.addAll(solveTowerOfHanoi(n - 1, from, aux, to));
            steps.add(from + "," + to);
            steps.addAll(solveTowerOfHanoi(n - 1, aux, to, from));
            return steps;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            int rodWidth = 5;
            int rodHeight = 250;
            int xOffset = 300;
            int yPosition = 500;

            for (int i = 0; i < 3; i++) {
                int xPosition = (i * 200) + xOffset;
                g.setColor(Color.DARK_GRAY);
                g.fillRect(xPosition, yPosition - rodHeight, rodWidth, rodHeight);

                Stack<Integer> rod = rods[i];
                int diskY = yPosition;
                for (int disk : rod) {
                    if (isDragging && i == dragFromRod && disk == draggingDisk) {
                        continue;
                    }
                    drawDisk(g, xPosition, diskY, disk);
                    diskY -= 25;
                }
            }

            if (isDragging && draggingDisk != -1) {
                drawDisk(g, mouseX, mouseY, draggingDisk);
            }
        }

        private void drawDisk(Graphics g, int x, int y, int diskSize) {
            int diskWidth = diskSize * 30;
            g.setColor(new Color(100 + diskSize * 15, 100, 200 - diskSize * 15));
            g.fillRoundRect(x - diskWidth / 2, y - 10, diskWidth, 20, 10, 10);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TowerOfHanoiGame::new);
    }
}