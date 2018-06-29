package matrixmulti.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Poller;
import org.zeromq.ZMQ.Socket;

import matrixmulti.data.Matrix;
import matrixmulti.data.PartialProblem;
import matrixmulti.data.PartialSolution;

public class Server {

	private final String frontendURL;
	private final String backendURL;
	private Socket frontend;
	private Socket backend;
	private Context context;
	private boolean stopped = false;
	private Queue<String> workerQueue = new LinkedList<String>();
	private Map<String, ArrayList<PartialProblem>> partialProblemMap = new HashMap<String, ArrayList<PartialProblem>>();
	private Map<String, ArrayList<PartialSolution>> matrixMap = new HashMap<String, ArrayList<PartialSolution>>();

	public Server(String frontendURL, String backendURL) {
		this.frontendURL = frontendURL;
		this.backendURL = backendURL;
	}

	public void run() throws Exception {
		System.out.println("Starting Server ...");
		// Here we init the sockets
		try {
			context = ZMQ.context(1);
			frontend = context.socket(ZMQ.ROUTER);
			backend = context.socket(ZMQ.ROUTER);
			frontend.bind(frontendURL);
			backend.bind(backendURL);
			System.out.println("Client: " + frontendURL + " Worker: " + backendURL);
			// Here is the loop for the server
			while (!stopped) {
				// Create 2 poller items
				Poller items = context.poller(2);
				// Poll back-end & front-end
				int backendPollerId = items.register(backend, Poller.POLLIN);
				int frontendPollerId = -1;
				if (workerQueue.size() > 0)
					frontendPollerId = items.register(frontend, Poller.POLLIN);
				// If there are no signals during poll, break
				if (items.poll() < 0)
					break;
				// Checks for available workers to queue
				if (items.pollin(backendPollerId)) {
					backendActivity();
				}
				// If we get here, there are available workers and we received sth. from
				// frontend,
				// therefore we handle our frontend
				if (items.pollin(frontendPollerId)) {
					frontendActivity();
				}
				sendProblem();
			}
		} finally {
			frontend.close();
			backend.close();
			context.close();
		}
	}

	/**
	 * Handles the frontend activity
	 */
	private void frontendActivity() {
		String clientAddr = frontend.recvStr();
		byte[] empty = frontend.recv();
		assert (empty.length == 0);
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
			// Create a space where the problems and solutions of a client can be stored
			partialProblemMap.put(clientAddr, partialProblems);
			matrixMap.put(clientAddr, new ArrayList<PartialSolution>());
			System.out.println("New Problem client: " + clientAddr + " Size: " + partialProblems.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * handles the backend activity
	 * 
	 * @throws Exception
	 */
	private void backendActivity() throws Exception {
		// Queue worker address for LRU routing
		String workerAddr = backend.recvStr();
		workerQueue.add(workerAddr);
		// Second frame is empty
		byte[] empty = backend.recv();
		assert (empty.length == 0);
		// Third frame is READY or else client reply address
		String clientAddr = backend.recvStr();
		System.out.println("Added worker: " + workerAddr + ", currently:" + workerQueue.size());
		// If worker sends a client address, route back to client
		if (!clientAddr.equals("READY")) {
			empty = backend.recv();
			assert (empty.length == 0);
			String reply = backend.recvStr();
			PartialSolution partialSolution = PartialSolution.deserialize(reply);
			System.out.println("Received worker: " + partialSolution.getSolution());
			matrixMap.get(clientAddr).add(partialSolution);
			// TODO: combine solutions, send them to client
			// frontend.sendMore(clientAddr);
			// frontend.sendMore("");
			// frontend.send(reply);
			// If we have solved all problems of a client, combine solutions to matrix and send to client
			if(partialProblemMap.get(clientAddr).size() <= 0) {
				ArrayList<PartialSolution> solutions = matrixMap.get(clientAddr);
				ArrayList<ArrayList<Double>> values = new ArrayList<ArrayList<Double>>();
				for(PartialSolution _partialSolution : solutions) {
					int row = _partialSolution.getRow();
					int column = _partialSolution.getColumn();
					values.get(row).add(column, _partialSolution.getSolution());
				}
				// Matrix matrix = new Matrix();
			}
		}
	}

	private void sendProblem() {
		// If there are no clients, pass.
		if (partialProblemMap.size() > 0) {
			// Iterate over each client
			for (String clientAddr : partialProblemMap.keySet()) {
				// Get ProblemList of this client
				ArrayList<PartialProblem> partialProblems = partialProblemMap.get(clientAddr);
				// If there are available works and problems for this client, send a problem
				if (workerQueue.size() > 0 && partialProblems.size() > 0) {
					// Determine index of last problem in our list
					int indexLastProblem = partialProblems.size() - 1;
					// Get last problem of our problem list.
					PartialProblem partialProblem = partialProblems.get(indexLastProblem);
					// Remove last problem from our list
					partialProblems.remove(indexLastProblem);
					// Serialize problem into a String
					String payload = partialProblem.serialize();
					// Send problem to a available worker
					String workerAddress = workerQueue.remove();
					System.out.println("Removed worker: " + workerAddress + ", currently:" + workerQueue.size());
					backend.sendMore(workerAddress);
					backend.sendMore("");
					backend.sendMore(clientAddr);
					backend.sendMore("");
					backend.send(payload);
				}
			}
		}
	}

}