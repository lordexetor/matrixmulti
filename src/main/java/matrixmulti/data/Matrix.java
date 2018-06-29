package matrixmulti.data;

import java.util.Arrays;

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
	
	public double[] getRow(int i) {
		return values[i];
	}
	
	public double[] getColumn(int i) {
		double[] colValues = new double[getRows()];
		for (int j = 0; j < getRows(); j++) {
			colValues[j] = values[j][i];
		}
		return colValues;
	}

	/**
	 * Serialize this matrix in the format values#rows#columns
	 * 
	 * @return serialized string
	 */
	public String serialize() {
		String s = "";
		s += Arrays.deepToString(getValues());
		s += "#";
		s += Integer.toString(getRows());
		s += "#";
		s += Integer.toString(getColumns());
		return s;
	}

	public static Matrix deserialize(String string) throws Exception {
		String[] params = string.split("#");
		if (params.length == 3) {
			int rows = Integer.parseInt(params[1]);
			int columns = Integer.parseInt(params[2]);
			double[][] values = new double[rows][columns];
			String[] strValues = params[0].replaceAll("[\\[\\]\\,]", "").split(" ");
			int n = 0;
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < columns; j++) {
					values[i][j] = Double.parseDouble(strValues[n]);
					n++;
				}
			}
			return new Matrix(values);
		} else
			throw new Exception("Invalid string format. Cannot deserialize Matrix");
	}
}
