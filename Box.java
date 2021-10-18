// *********************************
// ** Bradley Dowling - CSE223 - PA4 - 5/20/2021
// **
// ** This is a programming assignment that explores Swing and JFrames.
// ** The main program runs through Dots.java. Box.java is the class that
// ** stores the properties/behaviors of each box within the game board.

public class Box {
	
	// Properties:
	public boolean top = false;
	public boolean right = false;
	public boolean bottom = false;
	public boolean left = false;
	public String owner = "";
	
	// Constructor:
	public Box() {}
	
	// Mutators:
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	// resetFields() resets all of the properties in this class to the default values
	public void resetFields() {
		top = false;
		right = false;
		bottom = false;
		left = false;
		owner = "";
	}
	
	// isValidMove() checks to see if a specific side in this box has already been
	// clicked on, returns false if it has, true if it has not...
	public boolean isValidMove(char side) {
		switch(side) {
			case 't':
				return(!top); // return true if side has not been played, false it it has
			case 'r':
				return(!right);
			case 'b':
				return(!bottom);
			case 'l':
				return(!left);
			default:
				return(false);
		}
	}
	
	// isCompleted() checks to see if all of the sides of this box have been clicked on at some point.
	public boolean isCompleted() {
		if (top && right && bottom && left) {
			return(true);
		} else return(false);
	}
	
}
