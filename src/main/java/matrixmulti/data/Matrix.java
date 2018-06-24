package matrixmulti.data;

public class Matrix {
	private final double[][] values;
	private final int rows;
	private final int columns;
	
	public Matrix(double[][] values) {
		this.values = values;
		this.rows = values.length;
		this.columns = values[0].length;
	}

	public double[][] getValues() {
		return values;
	}

	public int getRows() {
		return rows;
	}

	public int getColumns() {
		return columns;
	}
}
