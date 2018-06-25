public class Main{
    public static void main(String[] args) {
        switch(args[0]){
            case "client":
                Client client = new Client();
                client.run();
                break;
            case "server":
                Server server = new Server();
                server.run();
                break;
            case "worker":
                Worker worker = new Worker();
                worker.run();
                break;
        }
    }
}
