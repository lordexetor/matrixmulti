package matrixmulti.server;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Poller;
import org.zeromq.ZMQ.Socket;

import matrixmulti.data.Matrix;
import matrixmulti.data.PartialProblem;

public class Server {

	private final String frontendURL;
	private final String backendURL;
	private Socket frontend;
	private Socket backend;
	private Context context;
	Queue<String> workerQueue = new LinkedList<String>();
	private boolean stopped = false;

	public Server(String frontendURL, String backendURL) {
		this.frontendURL = frontendURL;
		this.backendURL = backendURL;
	}

	public void run() {
		System.out.println("Starting Server ...");
		// Here we init the sockets
		try {
			context = ZMQ.context(1);
			frontend = context.socket(ZMQ.ROUTER);
			backend = context.socket(ZMQ.ROUTER);
			frontend.bind(frontendURL);
			System.out.println("Client Socket on " + frontendURL);
			backend.bind(backendURL);
			System.out.println("Worker Socket on " + backendURL);
			// Here is the loop for the server
			while (!stopped) {
				Poller items = context.poller(2);
				int backendPollerId = items.register(backend, Poller.POLLIN);
				int frontendPollerId = items.register(frontend, Poller.POLLIN);
				
				if (items.poll() < 0)
					break;
				// TODO: only poll frontend if workers are available
				if (items.pollin(frontendPollerId)) {
					System.out.println("Problem received");
					frontendActivity();
				}
				
//				// Initialize poll set
//				Poller items = context.poller(2);
//				// Always poll for worker activity on backend
//				int backendPollerId = items.register(backend, Poller.POLLIN);
//				
//				
////				// Poll frontend only if we have available workers
//				int frontendPollerId = -1;
////				if (workerQueue.size() > 0)
//					frontendPollerId = items.register(frontend, Poller.POLLIN);
////				// If there is nothing, break
//				if (items.poll() < 0)
//					break;
//				// Handle worker activity on backend
//				if (items.pollin(backendPollerId))
//					System.out.println("Problem received");
////					backendActivity();
////				// Handle client activity on frontend
////				if (items.pollin(backendPollerId))
////					frontendActivity();
			}
		} finally {
			frontend.close();
			backend.close();
			context.close();
		}
	}

	private void backendActivity() {
		// Queue worker address for LRU routing
		String workerAddr = backend.recvStr();
		workerQueue.add(workerAddr);
		// Second frame is empty
		byte[] empty = backend.recv();
		assert (empty.length == 0);
		// Third frame is READY or else client reply address
		String clientAddr = backend.recvStr();
		// If worker reply with value, receive from backend and add value to result
		if (!clientAddr.equals("READY")) {
			empty = backend.recv();
			assert (empty.length == 0);
			byte[] reply = backend.recv();
			// TODO: Deserialize, add value
		}
		// TODO: If matrix is complete, send result to client
	}

	private void frontendActivity() {
		String clientAddr = frontend.recvStr();
		String empty = frontend.recvStr();
		assert (empty.length() == 0);
		String problem = frontend.recvStr();
		
		String[] separateMatrices = problem.split("X");
		assert (separateMatrices.length == 2);
		try {
			Matrix A = Matrix.deserialize(separateMatrices[0]);
			Matrix B = Matrix.deserialize(separateMatrices[1]);
			
			ArrayList<PartialProblem> partialProblems = new ArrayList<PartialProblem>();
			
			for (int r = 0; r < A.getRows(); r++) {
				for (int c = 0; c < B.getColumns(); c++) {
					PartialProblem p = new PartialProblem(A.getRow(r), B.getColumn(c), r, c);
					partialProblems.add(p);
				}
			}
			
			System.out.println("Created " + partialProblems.size() + " partial problems to solve");
			// TODO: serialize and send the partial problems to the workers
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}