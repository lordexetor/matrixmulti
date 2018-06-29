package matrixmulti;

import matrixmulti.client.*;
import matrixmulti.server.*;
import matrixmulti.worker.*;

public class Main{
	/**
	 * @param args First argument is "c" or "s" or "w"
	 * If server, add frontendURL and backendURL
	 * If worker, add backendURL
	 */
    public static void main(String[] args) {
    	switch(args.length) {
    		default:
    			System.out.println("Please state necessary parameters");
    			break;
    		case 1:
    			System.out.println("Valid parameters: -c -[serverURLfrontend] or -w -[serverURLbackend] or -s -[serverURLfrontend] -[serverURLbackend]");
    			break;
    		case 2:
    			if (args[0].equals("c") && !args[1].isEmpty()) {
    				Client c = new Client(args[1]);
    				c.run();
    			}
    			
    			else if (args[0].equals("w") && !args[1].isEmpty()) {
    				//TODO: start worker
    			}
    			
    			break;
    		case 3:
    			if (args[0].equals("s") && !args[1].isEmpty() && !args[2].isEmpty()) {
    				Server s = new Server(args[1], args[2]);
    				s.run();
    			}
    			break;
    			
    	}
    	
    	//        switch(args[0]){
//            case "c":
//                Client client = new Client();
//                client.run();
//                break;
//            case "s":
//                Server server = new Server(args[1], args[2]);
//                server.run();
//                break;
//            case "w":
//                Worker worker = new Worker(args[1]);
//			try {
//				worker.run();
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//                break;
//        }
    }
}
