package matrixmulti;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

import matrixmulti.data.Matrix;

public class Client {
	public static void main(String[] args) {
		System.out.println("Starting Client ...");
		
		Context context = ZMQ.context(1);
		Socket $client = context.socket(ZMQ.REQ);
		
		final String id = Client.generateId();
		System.out.println("Client " + id + " started.");
		$client.connect("tcp://localhost:3000");
		
		double[][] a = { {1,2,3}, {3,4,5} };
		double[][] b = { {1,2}, {2,3}, {3,4} };
		Matrix A = new Matrix(a);
		Matrix B = new Matrix(b);
		
		$client.sendMore("PROBLEM");
		$client.sendMore("");
		$client.sendMore(id);
		
		String reply = $client.recvStr();
		System.out.println("Client " + id + " received " + reply);
		
		$client.close();
		context.term();
		
		System.out.println("Client " + id + "terminated.");
		
	}
	
	private static String generateId() {
		return "@" + System.nanoTime();
	}
}
