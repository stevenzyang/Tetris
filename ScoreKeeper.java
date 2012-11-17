public class ScoreKeeper {
	private int score;

	public void scoreLinesCleared(int numConsecutiveLines) {
		if(numConsecutiveLines < 1)
		{
			return;
		}
		
		int multiplier = 1;
		while (numConsecutiveLines > 1) {
			multiplier += numConsecutiveLines;
			numConsecutiveLines--;
		}

		score += multiplier * 100;
	}

	public void scoreDownPress() {
		score++;
	}

	public int getScore() {
		return this.score;
	}
}
