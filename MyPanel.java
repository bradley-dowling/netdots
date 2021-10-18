// *********************************
// ** Bradley Dowling - CSE223 - PA5 - 6/2/2021
// **
// ** For PA5, MyPanel.java behaves almost exactly as it did
// ** PA4. All of the networking is handled within NetDot.java
// ** and MyThread.java.

import java.awt.Graphics;
import javax.swing.JPanel;

public class MyPanel extends JPanel {
	
	// cell/box properties
	private int boxWidth = 50;
	private int boxHeight = 50;
	private int totalRows = 8;
	private int totalColumns = 8;
	private int margin = 50;
	
	// player properties
	private String player1Initials = "";
	private String player2Initials = "";
	private int player1Score;
	private int player2Score;
	private int currentPlayer; // 1 or 2
	
	// the board
	private Box[][] boxArray;
	
	// miscellaneous properties
	public int invalidPlays = 0;
	public boolean gameOver = false;
	public boolean hasBeenInitialized = false;
	public boolean myTurn;
	
	// initializeBoard() runs the first time that a user presses the 'start' button
	// boxArray as initialized and scores are set.
	public void initializeBoard() {
		boxArray = new Box[totalRows][totalColumns];
		for (int row = 0; row < totalRows; row++) {
			for (int column = 0; column < totalColumns; column++) {
				boxArray[row][column] = new Box();
			}
		}
		
		player1Score = 0;
		player2Score = 0;
		currentPlayer = 1;
		hasBeenInitialized = true;
		
	}
	
	// main paint() method.
	public void paint(Graphics g) {
		super.paint(g);
		
		// draw the dots:
		for (int x = margin; x < (totalRows * boxWidth) + (2 * margin); x += boxWidth) {
			for (int y = margin; y < (totalColumns * boxHeight) + (2 * margin); y += boxHeight) {
				g.fillOval(x-5, y-5, 10, 10);
			}
		}
		
		// don't attempt to draw the box if it hasn't been initialized yet (done by pressing start button)
		if (boxArray == null) return;

		// draw the lines:
		Box currentBox;
		int xCoord;
		int yCoord;
		
		for (int row = 0; row < totalRows; row++) {
			for (int column = 0; column < totalColumns; column++) {
				currentBox = boxArray[row][column];
				xCoord = column * boxWidth + margin;
				yCoord = row * boxWidth + margin;
				
				// Check if side has been played. If so, draw the line...
				if (currentBox.top) g.drawLine(xCoord, yCoord, xCoord + boxWidth, yCoord);
				if (currentBox.right) g.drawLine(xCoord + boxWidth, yCoord, xCoord + boxWidth, yCoord + boxHeight); 
				if (currentBox.bottom) g.drawLine(xCoord, yCoord + boxHeight, xCoord + boxWidth, yCoord + boxHeight);
				if (currentBox.left) g.drawLine(xCoord, yCoord, xCoord, yCoord + boxHeight);
				
				// Finally, check if all sides for this box have been played. If so, 
				// draw the owners initial to symbolize that they won that box.
				if (currentBox.isCompleted()) {
					g.drawString(currentBox.owner, xCoord + 20, yCoord + 20);
				}
			}
		}
		
	}
	
	// method for when a player clicks on the board at position (x, y)...
	public boolean playerTurn(int x, int y) {
		
		boolean hasScored = false; // boolean representing whether user has scored or not...
		
		// get the row and column of the box user clicked within:
		int row = getRowNum(y);
		int column = getColumnNum(x);
		
		Box currentBox = boxArray[row][column]; // box that user clicked within
		
		char closestSide = getClosestSide(x, y, column, row); // 't', 'r', 'b', 'l'
		
		// check if this is a valid move or not
		if (!currentBox.isValidMove(closestSide)) {
			invalidPlays++;
			return(false);
		}
		
		// finally, set the side of the current box and the adjacent box
		// also check to see if the user has scored a point off of a completed adjacent box
		switch (closestSide) {
		case 't':
			currentBox.top = true;
			if (row > 0) {
				Box topBox = boxArray[row - 1][column];
				topBox.bottom = true; // adjacent box to top side
				if (topBox.isCompleted()) {
					topBox.owner = getCurrentPlayerInitials();
					hasScored = true;
					addPoint(currentPlayer);
				}
			}
			break;
		case 'r':
			currentBox.right = true;
			if (column < 7) {
				Box rightBox = boxArray[row][column + 1];
				rightBox.left = true; // adjacent box to right side
				if (rightBox.isCompleted()) {
					rightBox.owner = getCurrentPlayerInitials();
					hasScored = true;
					addPoint(currentPlayer);
				}
			}
			break;
		case 'b':
			currentBox.bottom = true;
			if (row < 7) {
				Box bottomBox = boxArray[row + 1][column];
				bottomBox.top = true; // adjacent box to bottom side
				if (bottomBox.isCompleted()) {
					bottomBox.owner = getCurrentPlayerInitials();
					hasScored = true;
					addPoint(currentPlayer);
				}
			}
			break;
		case 'l':
			currentBox.left = true;
			if (column > 0) {
				Box leftBox = boxArray[row][column - 1];
				leftBox.right = true; // adjacent box to the left side
				if (leftBox.isCompleted()) {
					leftBox.owner = getCurrentPlayerInitials();
					hasScored = true;
					addPoint(currentPlayer);
				}
			}
			break;
		}
		
		// Now, check if the current box is completed as well...
		if (currentBox.isCompleted()) {
			// user scored a point and they get to keep playing
			hasScored = true;
			addPoint(currentPlayer);
			currentBox.owner = getCurrentPlayerInitials();
		} 
		
		// Finally, if user hasn't scored this round, it's now the other users turn...
		if (!hasScored) {
			// other players turn
			swapCurrentPlayer();
		}
		
		invalidPlays = 0;
		gameOver = isGameOver(); // check if game is over
		return(true); // move was valid
	}
	
	// getClosestSide is a function that determines what side the user
	// has clicked closest to (x, y coords) within a specified box (boxArray[row][column])
	public char getClosestSide(int x, int y, int column, int row) {
		// First, get the distances to all of the sides...
		int topDistance = (y - margin) - (row * boxHeight);
		int rightDistance = ((column + 1) * boxWidth) - (x - margin);
		int bottomDistance = ((row + 1) * boxHeight) - (y - margin);
		int leftDistance = (x - margin) - (column * boxWidth);
		
		// Next, figure out which distance is smallest and return a char
		// representing that side...
		int closestDistance = topDistance;
		char closestSide = 't'; // t = top; r = right; b = bottom; l = left
		
		if (closestDistance > rightDistance) {
			closestDistance = rightDistance;
			closestSide = 'r';
		}
		
		if (closestDistance > bottomDistance) {
			closestDistance = bottomDistance;
			closestSide = 'b';
		}
		
		if (closestDistance > leftDistance) {
			closestDistance = leftDistance;
			closestSide = 'l';
		}
		
		// Closest side found, now return the char representing that side...
		return(closestSide);
	}
	
	// getRowNum() gets the row that the user clicked in (with y coord)
	public int getRowNum(int y) {
		int row = (int)((y - margin) / boxHeight);
		if (row > totalRows - 1) row = totalRows - 1;
		return(row);
	}
	
	// getColumnNum() gets the column that the user clicked in (with x coord)
	public int getColumnNum(int x) {
		int column = (int)((x - margin) / boxWidth);
		if (column > totalColumns - 1) column = totalColumns - 1;
		return(column);
	}
	
	// setPlayerInitials() sets the initials of the players that were specified
	// in player1NameField and player2NameField text boxes
	public void setPlayerInitials(String player1, String player2) {
		// first, check if player1 and player2 have the same first initial
		if (player1.toUpperCase().charAt(0) == player2.toUpperCase().charAt(0)) {
			player1Initials = player1.toUpperCase().substring(0, 1).concat("1");
			player2Initials = player2.toUpperCase().substring(0, 1).concat("2");
		} else {
			// first initials must be different so just use those!
			player1Initials = player1.toUpperCase().substring(0, 1);
			player2Initials = player2.toUpperCase().substring(0, 1);
		}
	}
	
	// addPoint() adds a point to the current players score
	public void addPoint(int currentPlayer) {
		if (currentPlayer == 1) player1Score++;
		else player2Score++;
	}
	
	// isGameOver() checks to see if the game is over (surprise)
	public boolean isGameOver() {
		for (int row = 0; row < totalRows; row++) {
			for (int column = 0; column < totalColumns; column++) {
				if (!boxArray[row][column].isCompleted()) return(false);
			}
		}
		return(true);
	}
	
	// standard accessors for some of the private properties of MyPanel...
	public String getPlayer1Initials() {
		return(player1Initials);
	}
	
	public String getPlayer2Initials() {
		return(player2Initials);
	}
	
	public int getPlayer1Score() {
		return(player1Score);
	}
	
	public int getPlayer2Score() {
		return(player2Score);
	}
	
	public int getCurrentPlayer() {
		return(currentPlayer);
	}
	
	public void swapCurrentPlayer() {
		if (currentPlayer == 1) currentPlayer = 2;
		else currentPlayer = 1;
	}
	
	public boolean isMyTurn() {
		return(myTurn);
	}
	
	public String getCurrentPlayerInitials() {
		if (currentPlayer == 1) return(player1Initials);
		else return(player2Initials);
	}
	
}
