package matrixmulti.data;

import java.util.Arrays;

public class PartialProblem {
	private final double[] aValues;
	private final double[] bValues;
	private final int row;
	private final int column;
	
	public PartialProblem(double[] aValues, double[] bValues, int row, int column) {
		this.aValues = aValues;
		this.bValues = bValues;
		this.row = row;
		this.column = column;
	}

	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}
	
	public double[] getaValues() {
		return aValues;
	}

	public double[] getbValues() {
		return bValues;
	}
	
	/**
	 * Serialize the PartialProblem
	 * @return A string in the format aValues#bValues#row#column
	 */
	public String serialize() {
		String s = "";
		s += Arrays.toString(getaValues());
		s += "#";
		s += Arrays.toString(getbValues());
		s += "#";
		s += Integer.toString(getRow());
		s += "#";
		s += Integer.toString(getColumn());
		return s;
	}
	
	/**
	 * Deserialize a partial problem
	 * @param s the serialized string
	 * @return a partial problem conatining the serialized values.
	 * @throws Exception when the string is not in the correct format
	 */
	public static PartialProblem deserialize(String s) throws Exception {
		String[] params = s.split("#");
		if (params.length == 4) {
			double[] aValues = PartialProblem.fromString(params[0]);
			double[] bValues = PartialProblem.fromString(params[1]);
			int row = Integer.parseInt(params[2]);
			int column = Integer.parseInt(params[3]);
			return new PartialProblem(aValues, bValues, row, column);
		} else throw new Exception("Invalid String format. Cannot parse to partial Problem");
	}
	
	private static double[] fromString(String string) {
		String[] strValues = string.replace("[", "").replace("]", "").split(", ");
		double[] values = new double[strValues.length];
		for (int i = 0; i < strValues.length; i++) {
			values[i] = Double.parseDouble(strValues[i]);
		}
		return values;
	}
}
