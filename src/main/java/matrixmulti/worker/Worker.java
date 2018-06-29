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

	public void run() throws Exception {
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
				String clientAddress = socket.recvStr();
				partialProblem = getServerPartialproblem();
				// If the server supplied us a problem, solve it. Then null the problem.
				PartialSolution partialSolution = null;
				if (partialProblem != null) {
					System.out.println(partialProblem.serialize());
					System.out.println(partialProblem.getRow());
					System.out.println(partialProblem.getColumn());
					partialSolution = solveProblem(partialProblem);
					partialProblem = null;
				}
				// If there is a solved problem, return value to server
				if (partialSolution != null) {
					String message = partialSolution.serialize();
					socket.sendMore(clientAddress);
					socket.sendMore("");
					socket.send(message);
					partialSolution = null;
				}
			}
		} finally {
			socket.close();
		}
	}

	/**
	 * Retrieves a MatrixTask from the Server
	 * 
	 * @throws Exception
	 */
	private PartialProblem getServerPartialproblem() throws Exception {
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
	 * @throws Exception 
	 */
	private PartialSolution solveProblem(PartialProblem _partialProblem) throws Exception {
		double[] a = _partialProblem.getaValues();
		double[] b = _partialProblem.getbValues();
		double result = 0;
		PartialSolution partialSolution = null;
		if (a.length == b.length) {
			for(int i = 0; i < a.length; i++) {
				double valueA = a[i];
				double valueB = b[i];
				double value = valueA * valueB;
				result += value;
			}
			partialSolution = new PartialSolution(result,_partialProblem.getRow(),_partialProblem.getColumn());
		} else {
			throw new Exception("The matrices can not be multiplied.");
		}
		System.out.println("Result is: "+result);
		return partialSolution;
	}
}