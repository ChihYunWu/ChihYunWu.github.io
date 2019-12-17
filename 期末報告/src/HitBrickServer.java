import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.bson.Document;

import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
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
				String command;
				String userName;
				int userScore;

				MongoClient mongoClient = new MongoClient("localhost", 27017);
				command = input.readUTF();
				if (command.equals("save")) {
					userName = input.readUTF();
					userScore = input.readInt();
					System.out.println(userName);
					System.out.println(userScore);
					output.writeUTF(exec2(mongoClient, userName, userScore));
				} else {
					StringBuffer rankData = new StringBuffer("");
					List<Document> rankIterable = exec1(mongoClient);
					rankData.append("rank     Name     Score \n");
					for (int i = 0; i < rankIterable.size(); i++) {
						Document data = rankIterable.get(i);

						// rankData.append(Integer.toString(i + 1) + "\t" + data.getString("userName") +
						// "\t"
						// + data.getInteger("userScore").toString() + "\n");
						rankData.append(String.format("%d        %s          %s          %n", i + 1,
								data.getString("userName"), data.getInteger("userScore").toString()));
					}

					output.writeUTF(rankData.toString());
				}
				output.flush();

			} catch (Exception x) {
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

	private static String exec2(MongoClient mongoClient, String userName, int userScore)
			throws IOException, URISyntaxException {
		MongoDatabase db = mongoClient.getDatabase("HitBrick");
		MongoCollection<Document> col = db.getCollection("Rank");
		System.out.println("MongoDB open");

		col.insertOne(new Document("userName", userName).append("userScore", userScore));
		return "save successful";

	}

	private static List<Document> exec1(MongoClient mongoClient) throws IOException, URISyntaxException {
		// String rankData;
		MongoDatabase db = mongoClient.getDatabase("HitBrick");
		MongoCollection<Document> col = db.getCollection("Rank");
		System.out.println("MongoDB open");
		BasicDBObject dbObject = new BasicDBObject();
		dbObject.put("userScore", -1);
		List<Document> iterate = col.find().sort(dbObject).limit(3).into(new ArrayList<Document>());

		return iterate;

	}

	public static void main(String args[]) {
		try {

			HitBrickServer server = new HitBrickServer(8777);
			server.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
