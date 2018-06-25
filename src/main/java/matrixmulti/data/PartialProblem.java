package matrixmulti.data;

public class PartialProblem {
	private final double[] values;
	private final int row;
	private final int column;
	
	public PartialProblem(double[] values, int row, int column) {
		this.values = values;
		this.row = row;
		this.column = column;
	}

	public double[] getValues() {
		return values;
	}

	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}
	
}
