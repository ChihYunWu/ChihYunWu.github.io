import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.bson.Document;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class HitBrickServer extends Thread {

	int port;
	ServerSocket serverSocket;
	List<Connection> connections;

	public HitBrickServer(int p) {
		port = p;
		connections = Collections.synchronizedList(new LinkedList());
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException x) {
			x.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				System.out.println("wait for connections...");
				Socket connSocket = serverSocket.accept();
				Connection connection = new Connection(connSocket);
				connections.add(connection);

				connection.start();
			} catch (IOException x) {
				x.printStackTrace();
			}
		}

	}

	class Connection extends Thread {
		Socket connSocket;
		DataInputStream input;
		DataOutputStream output;

		public Connection(Socket s) {
			connSocket = s;
			try {
				input = new DataInputStream(connSocket.getInputStream());
				output = new DataOutputStream(connSocket.getOutputStream());
				System.out.println("connect successful");
			} catch (IOException x) {
				x.printStackTrace();
			}
		}

		@Override
		public void run() {
			try {
				output.writeUTF("123");
				output.flush();

			} catch (IOException x) {
				System.out.println(x);
				close();
			}
			super.run();
		}

		public void close() {
			try {
				System.out.println("close connection...");
				input.close();
				output.close();
				connSocket.close();
				removeConnection(this);
			} catch (IOException x) {
				x.printStackTrace();
			}
		}

		void removeConnection(Connection connection) {
			connections.remove(connection);
		}
	}

	private static void exec2(MongoClient mongoClient) throws IOException, URISyntaxException {
		MongoDatabase db = mongoClient.getDatabase("HitBrick");
		MongoCollection<Document> col = db.getCollection("Rank");
		System.out.println("MongoDB open");
	}

	public static void main(String args[]) {
		try {
			MongoClient mongo = new MongoClient("localhost", 27017);
			HitBrickServer server = new HitBrickServer(8777);
			server.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
