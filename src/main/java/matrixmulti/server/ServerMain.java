package matrixmulti;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Poller;
import org.zeromq.ZMQ.Socket;

public class Server {
	public static void main(String[] args) {
		System.out.println("Starting Server ...");
		
		Context context = ZMQ.context(1);
		Socket $frontend = context.socket(ZMQ.ROUTER);
		
		$frontend.bind("tcp://localhost:3000");
		
		while (!Thread.currentThread().isInterrupted()) {
			Poller items = context.poller(2);
			
			int frontendPollerId = items.register($frontend, Poller.POLLIN);
			
			if (items.poll() < 0)
				break;
			
			if (items.pollin(frontendPollerId)) {
				String clientAddr = $frontend.recvStr();
				String empty = $frontend.recvStr();
				assert (empty.length() == 0);
				
				String request = $frontend.recvStr();
				
				
				$frontend.sendMore(clientAddr);
				$frontend.sendMore("");
				$frontend.sendMore("Hello there");
			}
			
		}
		
		$frontend.close();
		context.term();
	}
}
