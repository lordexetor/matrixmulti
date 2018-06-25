package matrixmulti.data;

import java.util.Arrays;

public class PartialSolution {
	private final double solution;
	private final int row;
	private final int column;

	public PartialSolution(double solution, int row, int column) {
		this.solution = solution;
		this.row = row;
		this.column = column;
	}

	public double getSolution() {
		return solution;
	}

	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}

	/**
	 * Serialize the partial solution
	 * 
	 * @return a string of the format value#row#column
	 */
	public String serialize() {
		String s = "";
		s += Double.toString(getSolution());
		s += "#";
		s += Integer.toString(getRow());
		s += "#";
		s += Integer.toString(getColumn());
		return s;
	}

	/**
	 * Deserialize a partial solution
	 * 
	 * @param s
	 *            the serialized string
	 * @return a partial solution with the values from the serialization
	 * @throws Exception
	 *             is thrown if the string format is invalid
	 */
	public static PartialSolution deserialize(String s) throws Exception {
		String[] params = s.split("#");
		if (params.length == 3) {
			double solution = Double.parseDouble(params[0]);
			int row = Integer.parseInt(params[1]);
			int column = Integer.parseInt(params[2]);
			return new PartialSolution(solution, row, column);
		} else
			throw new Exception("Invalid String format. Cannot parse to partial Solution");

	}

}