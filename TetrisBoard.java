import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class TetrisBoard extends JPanel {
	private Block[][] board;
	private Block[][] currentPiece;
	private ScoreKeeper score;
	private int piecesPlayed;
	private int row;
	private int col;
	private Timer moveTimer;

	public static void main(String[] args) {
		final TetrisBoard board = new TetrisBoard();
		JFrame frame = new JFrame("Tetris");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.getContentPane().add(board, BorderLayout.CENTER);
		frame.setSize(frame.getMinimumSize());
		frame.setVisible(true);
		frame.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyChar() == 'z' || arg0.getKeyChar() == 'Z') {
					board.onAction(TetrisLogic.ROTATELEFT);
				} else if (arg0.getKeyChar() == 'x' || arg0.getKeyChar() == 'X') {
					board.onAction(TetrisLogic.ROTATERIGHT);
				} else {
					switch (arg0.getKeyCode()) {
					case KeyEvent.VK_LEFT:
						board.onAction(TetrisLogic.MOVELEFT);
						break;
					case KeyEvent.VK_RIGHT:
						board.onAction(TetrisLogic.MOVERIGHT);
						break;
					case KeyEvent.VK_DOWN:
						board.score.scoreDownPress();
						board.onAction(TetrisLogic.MOVEDOWN);
						break;
					case KeyEvent.VK_UP:
						board.onAction(TetrisLogic.ROTATERIGHT);
						break;
					case KeyEvent.VK_SPACE:
						while (TetrisLogic.isActionValid(board.board, board.currentPiece,
					board.row, board.col, TetrisLogic.MOVEDOWN)){
							board.row++;
						}
						board.onAction(TetrisLogic.MOVEDOWN);
						board.invalidate();
						board.repaint();
						break;
					}
				}
			}
		});
	}

	public TetrisBoard() {
		this.setMinimumSize(new Dimension(300, 600));

		this.board = new Block[20][10];
		this.score = new ScoreKeeper();
		
		this.moveTimer = new Timer(1050, new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				TetrisBoard.this.onAction(TetrisLogic.MOVEDOWN);
			}
		});
		
		this.moveTimer.start();

		this.piecesPlayed = 0;
		this.positionNewPiece();
	}

	private void updateMoveTimer() {
		// between 1050ms (level 0) and 50ms (level 10)
		this.moveTimer.setDelay((10 - this.getLevel()) * 100 + 50);
	}
	
	private int getLevel()
	{
		int level = this.piecesPlayed / 20;
		
		return level < 10 ? level : 10;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		g.setColor(Color.RED);

		Block[][] mergedBoard = this.board;

		if (this.currentPiece != null) {
			mergedBoard = TetrisLogic.combine(this.board, this.currentPiece,
					this.row, this.col);
		}

		for (int i = 0; i < mergedBoard.length; i++) {
			Block[] row = mergedBoard[i];

			for (int j = 0; j < row.length; j++) {
				if (row[j] != null) {
					g.setColor(row[j].getColor());
					g.fill3DRect(
							(this.getWidth() * j / row.length),
							(this.getHeight() * i / mergedBoard.length),
							(this.getWidth() * (j + 1) / row.length)
									- (this.getWidth() * j / row.length),
							(this.getHeight() * (i + 1) / mergedBoard.length)
									- (this.getHeight() * i / mergedBoard.length),
							true);
				}
			}
		}

		g.setColor(Color.WHITE);
		char[] chars = this.getDisplay().toCharArray();
		g.drawChars(chars, 0, chars.length, this.getWidth() - 145, 10);
	}

	private String getDisplay() {
		return "Level: " + this.getLevel() + "  Score: " + this.score.getScore();
	}

	private void endGame() {
		this.moveTimer.stop();
		this.currentPiece = null;
		JOptionPane.showMessageDialog(this, "Game over!");
	}

	private void onAction(int action) {
		if (this.currentPiece == null) {
			// the game is over, ignore moves.
			return;
		}

		boolean modified = false;

		switch (action) {
		case TetrisLogic.ROTATELEFT:
			if (TetrisLogic.isActionValid(this.board, this.currentPiece,
					this.row, this.col, action)) {
				this.currentPiece = TetrisLogic
						.rotate(this.currentPiece, false);
				modified = true;
			}

			break;
		case TetrisLogic.ROTATERIGHT:
			if (TetrisLogic.isActionValid(this.board, this.currentPiece,
					this.row, this.col, action)) {
				this.currentPiece = TetrisLogic.rotate(this.currentPiece, true);
				modified = true;
			}

			break;
		case TetrisLogic.MOVELEFT:
			if (TetrisLogic.isActionValid(this.board, this.currentPiece,
					this.row, this.col, action)) {
				this.col--;
				modified = true;
			}
			break;
		case TetrisLogic.MOVERIGHT:
			if (TetrisLogic.isActionValid(this.board, this.currentPiece,
					this.row, this.col, action)) {
				this.col++;
				modified = true;
			}
			break;
		case TetrisLogic.MOVEDOWN:
			if (TetrisLogic.isActionValid(this.board, this.currentPiece,
					this.row, this.col, action)) {
				this.row++;
			} else {
				this.board = TetrisLogic.combine(this.board, this.currentPiece,
						row, col);

				if (row < 0) {
					// I know Jason, this is a bug - technically we should try
					// to clear the line and shift the piece down by that
					// amount, which may bring it down below the cut, but it
					// would make the rest of the logic a little more
					// complicated and I didn't want to do that to you guys.
					this.endGame();
				} else {
					TetrisLogic.clearLines(this.board, this.score);
					this.positionNewPiece();
				}
			}

			modified = true;
			break;
		}

		if (modified) {
			this.invalidate();
			this.repaint();
		}
	}

	private void positionNewPiece() {
		this.currentPiece = TetrisBoard.newPiece();
		this.row = -1 * this.currentPiece.length;
		this.col = 3;
		this.piecesPlayed++;
		this.updateMoveTimer();
	}

	private static Block[][] newPiece() {
		int blockType = (int) (Math.random() * 7);

		switch (blockType) {
		case 0:
			// long piece
			return new Block[][] { { null, Block.RED, null, null },
					{ null, Block.RED, null, null },
					{ null, Block.RED, null, null },
					{ null, Block.RED, null, null }, };
		case 1:
			// Backwards L
			return new Block[][] {
					{ Block.ORANGE, Block.ORANGE, Block.ORANGE },
					{ null, null, Block.ORANGE }, { null, null, null }, };
		case 2:
			// L
			return new Block[][] {
					{ Block.YELLOW, Block.YELLOW, Block.YELLOW },
					{ Block.YELLOW, null, null }, { null, null, null }, };
		case 3:
			// Square
			return new Block[][] { { Block.GREEN, Block.GREEN },
					{ Block.GREEN, Block.GREEN }, };
		case 4:
			// S
			return new Block[][] { { null, Block.BLUE, Block.BLUE },
					{ Block.BLUE, Block.BLUE, null }, { null, null, null }, };
		case 5:
			// T
			return new Block[][] { { Block.PINK, Block.PINK, Block.PINK },
					{ null, Block.PINK, null }, { null, null, null }, };
		case 6:

			// Z
			return new Block[][] { { Block.MAGENTA, Block.MAGENTA, null },
					{ null, Block.MAGENTA, Block.MAGENTA },
					{ null, null, null }, };
		default:
			return null; // not possible - just making the compiler be cool.
		}
	}
}
