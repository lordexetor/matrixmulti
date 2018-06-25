public class Server{

    private final String frontendURL;
	private final String backendURL;
    private Socket frontend;
    private Socket backend;
    Queue<String> workerQueue = new LinkedList<String>();

    public Server(String frontendURL, String backendURL) {
		this.frontendURL = frontendURL;
		this.backendURL = backendURL;
	}

    public void run(){
        System.out.println("Starting Server ...");
        // Here we init the sockets
        Context context = ZMQ.context(1);
		frontend = context.socket(ZMQ.ROUTER);
		backend = context.socket(ZMQ.ROUTER);) 
		frontend.bind(frontendURL);
		backend.bind(backendURL);
        // Here is the loop for the server
        while (!stopped.get()) {
            // Initialize poll set
            Poller items = context.poller(2);
            //   Always poll for worker activity on backend
			int backendPollerId = items.register(backend, Poller.POLLIN);
            //   Poll front-end only if we have available workers
			int frontendPollerId = -1;
            if (workerQueue.size() > 0) frontendPollerId = items.register(frontend, Poller.POLLIN);
            // If there is nothing, break
            if (items.poll() < 0) break;
            // Handle worker activity on backend
            if (items.pollin(backendPollerId)) backendActivity();
            // Handle client activity on frontend
            if (items.pollin(backendPollerId)) frontendActivity();
        }
    }

    private void backendActivity(){
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
			// TODO: deserialize, add value
		}
        if(true){
            // TODO: If matrix is complete, send result to client
            break;
        }
    }

    private void frontendActivity(){

    }


    

}