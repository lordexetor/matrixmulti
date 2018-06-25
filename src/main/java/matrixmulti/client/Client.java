package matrixmulti.client;

import org.zeromq.ZMQ.Socket;

import matrixmulti.data.Matrix;

public class Client {

	Socket socket;

	public Client() {
		System.out.println("Starting Client ...");
		socket.connect("tcp://localhost:3000");
		System.out.println("Connected to Server (Router) on port 3000");

		// Matrix A = new Matrix([[1,2,3],[4,5,6]]);
		// Matrix B = new Matrix([[2,1],[2,2],[3,1]]);
		// A.print();
		// B.print();

		// this.sendProblem(A, B);
		// setInterval(function(){
		// console.log('sending work');
		// sock.send('some work');
		// }, 500);

	}

	/**
	 */
	public void run() {

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
		String payload1 = A.toString();
		String payload2 = B.toString();
		System.out.println("Payload 1: " + payload1 + " Payload 2: " + payload2);
		socket.send(payload1 + '#' + payload2);
	}

	private static String generateId() {
		return "@" + System.nanoTime();
	}
}