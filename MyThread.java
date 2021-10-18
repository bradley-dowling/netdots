// *****************************************
// ** Bradley Dowling - CSE223 - PA5 - 6/2/2021
// **
// ** MyThread.java is an extension of the Thread class. It
// ** controls the behavior of the different threads for the
// ** client and server in the NetDot game. A MyThread object also
// ** has access to all of the objects within the NetDot object
// ** that initialized it.


import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class MyThread extends Thread {
	
	// properties:
	NetDot parent;
	ServerSocket ss;
	Socket sock;
	Scanner sc;
	PrintWriter pw;
	
	int playerNumber; // server = 1, client = 2
	boolean quitFlag = false; // game is over if quitFlag is raised to true
	String playerName;
	String connection;
	
	// Constructor:
	public MyThread(NetDot parent) {
		this.parent = parent;
	}
	
	// main method:
	public void run() {
		// if host, try waiting for a connection to accept...
		if (playerNumber == 1) {
			parent.player1NameField.setEditable(false);
			parent.messageField.setText("Waiting for connection...");
			try {
				ss = new ServerSocket(1234);
				sock = ss.accept();
				ss.close();
			} catch (Exception e) {
				System.out.println("Error: " + e);
				parent.messageField.setText("Could not create connection...");
				parent.board.gameOver = true; // end the game
				return;
			}
		} else {
			// if client, try to connect to the connection
			// specified in the connectionField
			parent.player2NameField.setEditable(false);
			parent.connectionField.setEditable(false);
			try {
				sock = new Socket(connection, 1234);
			} catch (Exception e) {
				System.out.println("Error: " + e);
				parent.messageField.setText("Could not connect...");
				parent.board.gameOver = true; // end the game
				return;
			}
		}
		
		// try setting up sc and pw
		
		try {
			sc = new Scanner(sock.getInputStream());
			pw = new PrintWriter(sock.getOutputStream());
		} catch (Exception e) {
			System.out.println("Error: " + e);
			parent.messageField.setText("Could not establish communication with other player...");
			parent.board.gameOver = true; // end the game
			return;
		}
		
		// send initial messages regarding names
		// set the name fields accordingly
		
		if (playerNumber == 1) {
			pw.println(playerName);
			pw.flush();
			parent.player2NameField.setText(sc.nextLine());
		} else {
			parent.player1NameField.setText(sc.nextLine());
			pw.println(playerName);
			pw.flush();
		}
		
		// now initialize the board...
		initializeBoard();
		
		String temp; // temp string for parsing
		
		// wait for inputs in infinite loop
		while (sc.hasNextLine()) {
			temp = sc.nextLine();
			parseTurn(temp);
			if (quitFlag) {
				break; // quit message received...
			}
		}
		
		// close everything out, send the user a message via the messageField, and exit this thread
		sc.close();
		pw.close();
		quit();
		return;
	}
	
	// Getters and setters:
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	
	public void setPlayerNumber(int playerNumber) {
		this.playerNumber = playerNumber;
	}
	
	public int getPlayerNumber() {
		return(playerNumber);
	}
	
	public void setConnection(String connection) {
		this.connection = connection;
	}
	
	// Additional Methods:
	public void initializeBoard() {
		// initialize the game	
		parent.board.initializeBoard();

		// set the players initials and then set up the score display appropriately
		parent.board.setPlayerInitials(parent.player1NameField.getText(), parent.player2NameField.getText());
		parent.player1ScoreLabel.setText(parent.board.getPlayer1Initials());
		parent.player2ScoreLabel.setText(parent.board.getPlayer2Initials());
		parent.player1Score.setText("" + parent.board.getPlayer1Score());
		parent.player2Score.setText("" + parent.board.getPlayer2Score());
		
		parent.messageField.setText(parent.board.getCurrentPlayerInitials() + "'s turn..."); // display that it is player 1's turn first
		parent.board.repaint();
	}
	
	public boolean isMyTurn() {
		// check to see if it is this players turn
		if (parent.board.getCurrentPlayer() == playerNumber) return(true);
		else return(false);
	}
	
	public void sendTurn(int x, int y) {
		// a valid turn has been played on this end, send that 
		// turn to the other player through the printwriter
		try {
			pw.println("C " + x + " " + y);
			pw.flush();
		} catch (Exception e) {
			System.out.println("Error: " + e);
		}
	}
	
	public void updateBoard() {
		// update the game board with the relevant info.
		// This method is called after every player turn.
		parent.board.repaint();
		
		if (parent.board.gameOver) {
			if (parent.board.getPlayer1Score() > parent.board.getPlayer2Score()) {
				parent.messageField.setText("GAME OVER! " + parent.board.getPlayer1Initials() + " wins!");
			} else if (parent.board.getPlayer2Score() > parent.board.getPlayer1Score()) {
				parent.messageField.setText("GAME OVER! " + parent.board.getPlayer2Initials() + " wins!");
			} else {
				parent.messageField.setText("GAME OVER! IT'S A TIE!");
			}
		} else {
			// game not yet over, so continue on...
			parent.player1Score.setText("" + parent.board.getPlayer1Score());
			parent.player2Score.setText("" + parent.board.getPlayer2Score());
			parent.messageField.setText(parent.board.getCurrentPlayerInitials() + "'s turn...");
		}
	}
	
	public void sendQuit() {
		// quit button has been pressed on this end, send a quit message
		// to the other end (if the printwriter has been initialized...
		if (pw != null) {
			try {
				pw.println("Q");
				pw.flush();
			} catch (Exception e) {
				System.out.println("Error: " + e);
			}
		}
	}
	
	public void quit() {
		// Another user has quit the game, let the current player know,
		// and prevent them from playing further...
		parent.messageField.setText("Someone quit the game...");
		parent.board.gameOver = true;
	}
	
	public void parseTurn(String turn) {
		// parseTurn takes the message that was received from the other
		// user, and parses that into either a click, or a quit message.
		if (turn.charAt(0) == 'C') {
			// This was a valid move, get x and y coords
			int x = Integer.parseInt(turn.split(" ")[1]);
			int y = Integer.parseInt(turn.split(" ")[2]);
			
			// play the move
			parent.board.playerTurn(x, y);
			
			// update the board and score info just as if you were the one who clicked...
			updateBoard();
		} else if (turn.charAt(0) == 'Q') {
			// other user has tried to quit, set the quit flag to true accordingly...
			quitFlag = true;
		}
	}
}
