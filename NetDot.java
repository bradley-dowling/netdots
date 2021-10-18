// *********************************
// ** Bradley Dowling - CSE223 - PA5 - 6/2/2021
// **
// ** NetDot.java is a game of Dots and Boxes played between
// ** two players (client and host) over a network connection.
// ** NetDot.java comprises the main JFrame code, and creates the
// ** threads for the players after they start the game. Thread
// ** behavior is controlled by MyThread.java. Box.java and MyPanel.java
// ** are almost identical to PA4. The game is over when someone
// ** wins or presses the "Quit" button.


import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.border.BevelBorder;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.JTextArea;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;

public class NetDot extends JFrame {

	public JPanel contentPane;
	public MyPanel board;
	public JTextField player1NameField;
	public JTextField player2NameField;
	public JTextField connectionField;
	public JLabel player1Score;
	public JLabel player2Score;
	public JLabel player1ScoreLabel;
	public JLabel player2ScoreLabel;
	public JRadioButton rdbtnServer;
	public JRadioButton rdbtnClient;
	public JLabel connectionLabel;
	public JButton startButton;
	public JTextArea messageField;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	
	
	public MyThread mt = new MyThread(this);

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					NetDot frame = new NetDot();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public NetDot() {
		setResizable(false);
		
		setTitle("Dots");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 524, 675);
		contentPane = new JPanel();
		contentPane.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		// board is the actual game board that the user will play on:
		board = new MyPanel();
		board.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		board.setBounds(12, 12, 500, 500);
		contentPane.add(board);
		
		// player information components:
		player1NameField = new JTextField();
		player1NameField.setBounds(12, 532, 85, 20);
		contentPane.add(player1NameField);
		player1NameField.setColumns(10);
		
		JLabel player1Label = new JLabel("Player 1:");
		player1Label.setBounds(12, 515, 66, 15);
		contentPane.add(player1Label);
		
		JLabel player2Label = new JLabel("Player 2:");
		player2Label.setBounds(12, 559, 66, 15);
		contentPane.add(player2Label);
		
		player2NameField = new JTextField();
		player2NameField.setBounds(12, 576, 85, 20);
		contentPane.add(player2NameField);
		player2NameField.setColumns(10);
		
		// score information components:
		JLabel lblScore = new JLabel("SCORE");
		lblScore.setFont(new Font("Dialog", Font.BOLD, 16));
		lblScore.setBounds(259, 524, 66, 15);
		contentPane.add(lblScore);
		
		player1Score = new JLabel("0");
		player1Score.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		player1Score.setFont(new Font("Courier 10 Pitch", Font.BOLD, 30));
		player1Score.setHorizontalAlignment(SwingConstants.CENTER);
		player1Score.setHorizontalTextPosition(SwingConstants.CENTER);
		player1Score.setBounds(206, 546, 50, 50);
		contentPane.add(player1Score);
		
		player2Score = new JLabel("0");
		player2Score.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		player2Score.setHorizontalTextPosition(SwingConstants.CENTER);
		player2Score.setHorizontalAlignment(SwingConstants.CENTER);
		player2Score.setFont(new Font("Courier 10 Pitch", Font.BOLD, 30));
		player2Score.setBounds(322, 546, 50, 50);
		contentPane.add(player2Score);
		
		player1ScoreLabel = new JLabel("P1");
		player1ScoreLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		player1ScoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
		player1ScoreLabel.setBounds(206, 524, 50, 15);
		contentPane.add(player1ScoreLabel);
		
		player2ScoreLabel = new JLabel("P2");
		player2ScoreLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		player2ScoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
		player2ScoreLabel.setBounds(322, 524, 50, 15);
		contentPane.add(player2ScoreLabel);
		
		rdbtnServer = new JRadioButton("Server");
		buttonGroup.add(rdbtnServer);
		rdbtnServer.setBounds(105, 530, 90, 23);
		contentPane.add(rdbtnServer);
		
		rdbtnClient = new JRadioButton("Client");
		buttonGroup.add(rdbtnClient);
		rdbtnClient.setBounds(105, 574, 85, 23);
		contentPane.add(rdbtnClient);
		
		connectionLabel = new JLabel("Connect to:");
		connectionLabel.setBounds(12, 602, 85, 15);
		contentPane.add(connectionLabel);
		
		connectionField = new JTextField();
		connectionField.setText("localhost");
		connectionField.setBounds(12, 620, 124, 20);
		contentPane.add(connectionField);
		connectionField.setColumns(10);
		
		startButton = new JButton("Start");
		startButton.setBounds(422, 524, 90, 50);
		contentPane.add(startButton);
		
		messageField = new JTextArea();
		messageField.setWrapStyleWord(true);
		messageField.setEditable(false);
		messageField.setText("Select 'Server' or 'Client'...");
		messageField.setLineWrap(true);
		messageField.setBounds(146, 605, 364, 33);
		contentPane.add(messageField);
		
		
		
		// action listeners:
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Check if user intends to quit or not
				if (startButton.getText().equalsIgnoreCase("Quit")) {
					// user intends to quit, let other player know
					mt.sendQuit();
					System.exit(0);
					return;
				}
				
				// pressing 'Start' or 'Connect' button
				
				// First, check which radial button was selected (server or client)...
				
				if (rdbtnServer.isSelected()) {
					// check if player1NameField is empty, set default name if so
					if (player1NameField.getText().isEmpty()) {
						player1NameField.setText("Server");
					}
					
					mt.setPlayerName(player1NameField.getText());
					mt.setPlayerNumber(1); // Player 1 is host
					
				} else if (rdbtnClient.isSelected()) {
					// check if user has entered a host to connect to
					if (connectionField.getText().isEmpty()) {
						messageField.setText("Please enter a host to connect to and try again...");
						return;
					}
					
					mt.setConnection(connectionField.getText());
					
					// check if player2NameField is empty, set default name if so
					if (player2NameField.getText().isEmpty()) {
						player2NameField.setText("Client");
					}
					
					mt.setPlayerName(player2NameField.getText());
					mt.setPlayerNumber(2); // Player 2 is client
					
				} else {
					messageField.setText("Please select 'Client' or 'Server' before pressing Start...");
					return;
				}
				
				mt.start();
				startButton.setText("Quit");
			}
		});
		
		rdbtnServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// when the Server radial button is selected...
				startButton.setText("Start");
				player1NameField.setEditable(true);
				player2NameField.setEditable(false);
				connectionLabel.setVisible(false);
				connectionField.setEditable(false);
				connectionField.setVisible(false);
				messageField.setText("Enter your name and then press Start...");
			}
		});
		
		rdbtnClient.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// when the Client radial button is selected...
				startButton.setText("Connect");
				player1NameField.setEditable(false);
				player2NameField.setEditable(true);
				connectionLabel.setVisible(true);
				connectionField.setEditable(true);
				connectionField.setVisible(true);
				messageField.setText("Enter your name, the host to connect to, and then press Connect...");
			}
		});
		
		board.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// Check if the game is already over...
				if (board.gameOver) return;
				
				// First, check if game has been initialized...
				if (!board.hasBeenInitialized) {
					messageField.setText("Uh, you gotta start the game first...");
					return;
				}
				
				// Next, make sure that the user has clicked inside the actual margins of the board...
				if (e.getX() >= 50 && e.getX() <= 450) {
					if(e.getY() >= 50 && e.getX() <= 450) {
						// user has clicked within the board
						
						// check if it is the users turn
						if(!mt.isMyTurn()) {
							messageField.setText("It's not your turn dude...");
							return;
						}
						
						// It's the users turn, try to play their click...
						if (!board.playerTurn(e.getX(), e.getY())) {
							// invalid move, let the user know about (nicely)
							messageField.setText("Invalid move, try again...");
						} else {
							// this was a valid move and the board has been adjusted
							mt.sendTurn(e.getX(), e.getY());
							mt.updateBoard();
						}
					}
				}
			}
		});
	}
}
