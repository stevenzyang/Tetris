public class TetrisLogic {
	public static final int MOVEDOWN = 0;
	public static final int MOVERIGHT = 1;
	public static final int MOVELEFT = 2;
	public static final int ROTATERIGHT = 3;
	public static final int ROTATELEFT = 4;

	/**
	 * Clears lines, if any need to be cleared.
	 * 
	 * @param board
	 *            The board array. The contents of the board array will be
	 *            modified by this method to clear the lines.
	 * @param score
	 *            The score keeper - use this to modify this score.
	 */
	public static void clearLines(Block[][] board, ScoreKeeper score) {
		int numConsecutiveLines = 0;
		for (int row = board.length - 1; row > -1; row--){
			boolean fullLine = true;
			for (int col = 0; col < board[row].length; col++){
				if (board[row][col] == null){
					fullLine = false;
				}
			}
			if (fullLine == true){
				for (int row2 = row; row2 > 0; row2--){
					for(int col = 0; col < board[row2].length; col++){
						board[row2][col] = board[row2 - 1][col];
					}
				}
				for(int col = 0; col < board[0].length; col++)
					board[0][col] = null;
				numConsecutiveLines++;
				row++;
			}
		}
		score.scoreLinesCleared(numConsecutiveLines);
	}

	/**
	 * Combine the board and the piece into a block array that contains all of
	 * the Blocks from both.
	 * 
	 * @param board
	 *            The board array.
	 * @param piece
	 *            The piece we want to merge with the board.
	 * @param row
	 *            The row of the upper left corner of the piece (relative to the
	 *            board)
	 * @param col
	 *            The column of the upper left corner of the piece (relative to
	 *            the board)
	 * @return A *new* Block[][] - original arrays must remain untouched.
	 */
	public static Block[][] combine(Block[][] board, Block[][] piece, int row,
			int col) {
		Block[][] newBoard = clone(board);
		for (int i = 0; i < piece.length; i++){
			for (int j = 0; j < piece[i].length; j++){
				if (row + i > -1 && piece[i][j] != null){
					if (piece[i][j] != null && newBoard[row + i][col + j] != null)
						touchingBlock = true;
				
					newBoard[row + i][col + j] = piece[i][j];
				}
			}
		}
		return newBoard;
	}
	
	private static Block[][] clone(Block[][] blocks){
		Block[][] copy = new Block[blocks.length][blocks[0].length];
		for (int row = 0; row < copy.length; row++){
			for (int col = 0; col < copy[0].length; col++){
				copy[row][col] = blocks[row][col];
			}
		}
		return copy;
	}
	
	private static Block[][] clear(Block[][] blocks){
		Block[][] cleared = new Block[blocks.length][blocks[0].length];
		for (int row = 0; row < cleared.length; row++){
			for (int col = 0; col < cleared[0].length; col++){
				cleared[row][col] = null;
			}
		}
		return cleared;
	}
	/**
	 * Checks to see if the specified action is valid.
	 * 
	 * Action can be one of MOVEDOWN, MOVERIGHT, MOVELEFT, ROTATERIGHT, or
	 * ROTATELEFT. (In other words, the values 0-4 are valid)
	 * 
	 * An action is invalid if it causes part of the piece to either be off the
	 * board or in the same position as an already occupied part of the board.
	 * The only exception to this is if the piece is sticking out the top of the
	 * board, which is OK.
	 * 
	 * @param board
	 *            The game board.
	 * @param piece
	 *            The piece that will move or rotate.
	 * @param row
	 *            The row of the upper left corner of the piece (relative to the
	 *            board)
	 * @param col
	 *            The column of the upper left corner of the piece (relative to
	 *            the board)
	 * @param action
	 *            The action to take on the piece. (MOVEDOWN, MOVERIGHT,
	 *            MOVELEFT, ROTATERIGHT, or ROTATELEFT)
	 * @return True if the action is possible, false if it is invalid.
	 */
	public static boolean isActionValid(Block[][] board, Block[][] piece,
			int row, int col, int action) {
		if (action == MOVEDOWN){
			touchingBlock = false;
			Block[][] newBoard = clone(board);
			Block[][] newPiece = clone(piece);
			if (row + piece.length > 0){
				try{
				combine(newBoard, newPiece, row + 1, col);
				}catch(ArrayIndexOutOfBoundsException e){
					return false;
				}finally{
					if (touchingBlock)
						return false;
				}
			}
			return true;
		}
		else if (action == MOVERIGHT){
			Block[][] newBoard = clone(board);
			Block[][] newPiece = clone(piece);
			
			if (row < 0){
				row = 0;
				newBoard = clear(board);
			}
			
			try{
				combine(newBoard, newPiece, row, col + 1);	
			}catch (ArrayIndexOutOfBoundsException e){
				return false;
			}finally {
				if (touchingBlock)
					return false;
			}
			return true;
		}
		else if (action == MOVELEFT){
			Block[][] newPiece = clone(piece);
			Block[][] newBoard = clone(board);
			
			if (row < 0){
				row = 0;
				newBoard = clear(board);
			}
			
			try{
				combine(newBoard, newPiece, row, col - 1);
			}catch (ArrayIndexOutOfBoundsException e){
				return false;
			}finally {
				if (touchingBlock)
					return false;
				}
			return true;
		}
		else {
			Block[][] newPiece = clone(piece);
			Block[][] newBoard = clone(board);
			
			if (action == ROTATERIGHT)
				newPiece = rotate(newPiece, true);
			
			else if (action == ROTATELEFT)
				newPiece = rotate(newPiece, false);
			
			if (row + piece.length < 2)
				return false;
			try{
				combine(newBoard, newPiece, row, col);
			}catch (ArrayIndexOutOfBoundsException e){
				return false;
			}finally {
				if (touchingBlock)
					return false;
			}
			return true;
		}
		
		
	}

	/**
	 * Rotates a piece, returning an entirely new array of Blocks.
	 * 
	 * @param piece
	 *            The piece to rotate. THIS WILL BE SQUARE.
	 * @param rotateRight
	 *            If rotateRight is true, rotate the piece right. Otherwise,
	 *            rotate it left.
	 * @return A *new* Block[][] - piece must remain untouched.
	 */
	public static Block[][] rotate(Block[][] piece, boolean rotateRight) {
		if (rotateRight == false){
			Block[][] newPiece = clone(piece);
			for (int i = 0; i < piece.length; i++){
				for (int j = 0; j < piece[i].length; j++){
					newPiece[j][i] = piece[i][piece.length - 1 - j];
				}
			}
			return newPiece;
		}
		else {
			Block[][] newPiece = clone(piece);
			for (int h = 0; h < 3; h++){
				for (int i = 0; i < piece.length; i++){
					for (int j = 0; j < piece[i].length; j++){
						newPiece[j][i] = piece[i][piece.length - 1 - j];
					}
				}
				piece = clone(newPiece);
			}
			return newPiece;
		}
	}
	private static boolean touchingBlock;
}
