import java.awt.Color;

public class Block
{
	public static final Block RED = new Block(Color.RED);
	public static final Block ORANGE = new Block(Color.ORANGE);
	public static final Block YELLOW = new Block(Color.YELLOW);
	public static final Block GREEN = new Block(Color.GREEN);
	public static final Block BLUE = new Block(Color.BLUE);
	public static final Block PINK = new Block(Color.PINK);
	public static final Block MAGENTA = new Block(Color.MAGENTA);
	
	private Color color;
	
	private Block(Color color)
	{
		this.color = color;
	}
	
	public Color getColor()
	{
		return this.color;
	}
}