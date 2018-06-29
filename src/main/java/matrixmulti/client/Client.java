package matrixmulti.client;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

import matrixmulti.data.Matrix;

public class Client {

	private Socket socket;
	private Context context;
	private final String serverURL;
	
	public Client(String serverURL) {
		this.serverURL = serverURL;
	}
	
	public void run() {
		try {
			context = ZMQ.context(1);
			socket = context.socket(ZMQ.REQ);
			System.out.println("Starting Client ...");
			socket.connect(serverURL);
			System.out.println("Connected to Server (Router) on " + serverURL);

			double[][] valuesA = {{3.0,2.0,1.0} , {1.0,0.0,2.0}};
			double[][] valuesB = {{1.0,2.0},{0.0,1.0},{4.0,0.0}};
			 Matrix A = new Matrix(valuesA);
			 Matrix B = new Matrix(valuesB);
			
			 this.sendProblem(A, B);
			 String resolution = socket.recvStr();
			 System.out.println("Solution is: " + resolution);
			 try {
				Matrix C = Matrix.deserialize(resolution);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} finally {
			socket.close();
			context.close();
		}
	}

	/**
	 * Send two matrices to the server to be multiplied.
	 * 
	 * @param {Matrix}
	 *            A - first matrix
	 * @param {Matrix}
	 *            B - second matrix
	 */
	private void sendProblem(Matrix A, Matrix B) {
		String payload1 = A.serialize();
		String payload2 = B.serialize();
		System.out.println("Payload 1: " + payload1 + " Payload 2: " + payload2);
		socket.send(payload1 + 'X' + payload2);
	}

	private static String generateId() {
		return "@" + System.nanoTime();
	}
}