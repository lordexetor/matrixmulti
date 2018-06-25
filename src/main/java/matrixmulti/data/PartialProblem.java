package matrixmulti.data;

import java.util.Arrays;

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
	
	/**
	 * Serialize the PartialProblem
	 * @return A string in the format values#row#column
	 */
	public String serialize() {
		String s = "";
		s += Arrays.toString(getValues());
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
		if (params.length == 3) {
			String[] stringifiedValues = params[0].replace("[", "").replaceAll("]", "").split(", ");
			double[] values = new double[stringifiedValues.length];
			for (int i = 0; i < stringifiedValues.length; i++) {
				values[i] = Double.parseDouble(stringifiedValues[i]);
			}
			int row = Integer.parseInt(params[1]);
			int column = Integer.parseInt(params[2]);
			return new PartialProblem(values, row, column);
		} else throw new Exception("Invalid String format. Cannot parse to partial Problem");
	}
	
}
