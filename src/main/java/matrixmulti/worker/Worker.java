package matrixmulti.worker;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Poller;
import org.zeromq.ZMQ.Socket;

import matrixmulti.data.PartialProblem;
import matrixmulti.data.PartialSolution;

public class Worker {

	private final AtomicBoolean stopped = new AtomicBoolean(false);
	private final String serverBackendURL;
	private final String id = UUID.randomUUID().toString();
	private Socket socket;

	public Worker(String serverBackendURL) {
		this.serverBackendURL = serverBackendURL;
	}

	public void run() throws Exception{
		System.out.println("Starting Worker ...");
		try {
			Context context = ZMQ.context(1);
			socket = context.socket(ZMQ.REQ);
			socket.setIdentity(id.getBytes());
			socket.connect(serverBackendURL);
			// Tell server we're ready for work
			socket.send("READY");
			socket.bind("tcp://192.168.178.60:5557");
			System.out.println("Worker ready...");
			while (!stopped.get()) {
				PartialProblem partialProblem = null;
				// Try to fetch a problem from the server
				partialProblem = getServerPartialproblem();
				// If the server supplied us a problem, solve it. Then null the problem.
				PartialSolution partialSolution = null;
				if (partialProblem != null) {
					System.out.println(partialProblem.serialize());
					partialSolution = solveProblem(partialProblem);
					partialProblem = null;
				}
				// If there is a solved problem, return value to server
				if (partialSolution != null) {
					// TODO: sendValue(value)
					// ++++++++++++++++++++++++
					// Null the solution
					partialSolution = null;
				}
			}
		}  finally {
			socket.close();
		}
	}

	/**
	 * Returns the result of A x B TODO: Document and javascriptify
	 */
	private PartialSolution multiplyMatrices(PartialProblem _partialProblem) {
		// if (matrixA.getNumberOfColumns() == matrixB.getNumberOfRows()) {
		// // Solve the matrix.
		// Matrix matrixC = new Matrix(matrixA.getNumberOfRows(),
		// matrixB.getNumberOfColumns());
		// for (row_A = 0; row_A < matrixA.getNumberOfRows(); row_A++) {
		// row = martrixA.getRow(row_A);
		// for (column_B = 0; column_B < matrixB.getNumberOfColumns(); column_B++) {
		// sum = 0;
		// column = B.getColumn(column_B);
		// for (i = 0; i < row.length; i++) {
		// sum += row[i] * column[i];
		// }
		// C.setValue(row_A, column_B, sum);
		// }
		// }
		// return C;
		// } else {
		// throw new Exception("The matrices can not be multiplied.");
		// }
		return null;
	}

	/**
	 * Retrieves a MatrixTask from the Server
	 * 
	 * @throws Exception
	 */
	private PartialProblem getServerPartialproblem() throws Exception {
		// payload frame - wait for a Request // NOT A RESPONSE FROM PREVIOUS REQUEST
		String clientAddress = socket.recvStr();
		// empty frame
		String empty = socket.recvStr();
		assert (empty.length() == 0);
		// receive bytes
		byte[] data = socket.recv();
		// byte[] -> string
		String str = new String(data, StandardCharsets.UTF_8);
		// string -> PartialProblem
		PartialProblem partialProblem = PartialProblem.deserialize(str);
		if (partialProblem != null)
			return partialProblem;
		return null;
	}

	/**
	 * Uses a PartialProblem to get the row and column that should be computed.
	 * Using these Arrays, we can call the multiply Matrices function to solve the
	 * problem. After that, the partialSolution is returned.
	 */
	private PartialSolution solveProblem(PartialProblem _partialProblem) {
		PartialSolution partialSolution = multiplyMatrices(_partialProblem);
		return partialSolution;
	}
}