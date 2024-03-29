package communication;


import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import communication.Message;
import communication.Message.messageKind;
import process.TestBench;
import utils.Node;

import org.json.JSONException;
import org.json.JSONObject;
import com.google.gson.Gson;
public class P2PNetwork {

	public Node localNode;

	List<Message> delay_receive_queue;
	List<Message> receive_queue;

	HashMap<String, Node> foundNodes;
	List<Node> neighborNodes;

	public P2PNetwork(Node myself) {
		/* Initiate the fields */
		this.delay_receive_queue = new ArrayList<Message>();
		this.receive_queue = new ArrayList<Message>();
		this.foundNodes = new HashMap<String, Node>();
		this.localNode = myself;
		if (this.localNode.ip == null || this.localNode.port == 0) {
			System.out.println("Unknown IP or Port!");
			// TODO: throw an exception - maybe IncorrectPortIP
			return;
		}

		// run server
		Thread server = new Thread() {
			public void run() {
				listen_server();
			}
		};
		server.start();

		/* bootstrap */
		System.out.println("Starting bootstrapping...");
		if (!findFirstNodeByPort()){
			//user is dead
			System.out.println("I am the first node");
		}

	}

	public boolean findFirstNodeByPort(){
		String myIP = this.localNode.ip;
		String delims = "[.]";
		String[] chunks = myIP.split(delims);

		int maxIP = 150;
		String testIP;
		for (int i = 0; i<maxIP; i++){
			if (Integer.toString(i).equals(chunks[3])){
				continue;
			}

			testIP = chunks[0]+"."+chunks[1]+"."+chunks[2]+"."+i;
			Socket s = null;
			int sensorPort = 4001;
			try {

				InetSocketAddress endpoint = new InetSocketAddress(testIP, sensorPort);
				s = new Socket();
				s.connect(endpoint, 101);

				s.close();
				System.out.println(testIP+":"+sensorPort + " - Found first node");
				Message message = new Message(testIP, sensorPort,messageKind.REQ_START,this.localNode);
				message.setJsonRoute(new ArrayList<String>());

				//destLocation
				message.setDestLoc("40.44316,-79.9422"); // node 16 coordinates
				//	message.setDestLoc(TestBench.destinationUser);

				message.setSenderNode(localNode);
				message.setPhoneIP(localNode.ip);
				message.setPhonePort(localNode.port);
				message.setNode(localNode);
				send(message);
				return true;
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
//				socket did not successfully open
				System.out.println(testIP+":"+sensorPort + " - Closed");
			}
		}
		return false;
	}

	public void send(Message message) {

		String ip = message.destIP;
		int port = message.destPort;
		System.out.println("Sending message to "+ip+":"+port+" from "+localNode.ip+":"+localNode.port);
		System.out.println(message.toString());

		Socket s = null;

		Connection2 connection_to_use = null;

		try {
			s = new Socket(ip, port);

			s.setKeepAlive(true);

			connection_to_use = new Connection2(s, this);

			System.out.println("Connection created");

		} catch (UnknownHostException e) {
			System.out.println("Client Socket error:" + e.getMessage());
		} catch (EOFException e) {
			System.out.println("Client EOF error:" + e.getMessage());
		} catch (IOException e) {
			System.out.println("Client readline error:" + e.getMessage());
		}
		if (connection_to_use == null) {
			System.out.println("Failed to find or open connection");
			return;
		}

		System.out.println("Sending message");

		Gson gson = new Gson();
		String json = gson.toJson(message);
		System.out.println(json);
		connection_to_use.write(json);
		//connection_to_use.write_object(message);
		connection_to_use.close();

	}

	public Message receive() {
		int size = 0;
		synchronized (receive_queue) {
			size = receive_queue.size();
		}
		if (size < 1)
			return null;

		Message retreive = null;
		synchronized (receive_queue) {
			retreive = receive_queue.remove(0);
		}

		return retreive;
	}

	public synchronized void receive_message(Message message, Connection2 c) {

		Node newNode = message.getNode();

		switch (message.kind) {

			case REQ_START:
				System.out.println("Received \"REQ_START\"");

				if(this.localNode.inMyArea(newNode))	{
					this.send((new Message(newNode.ip,newNode.port,messageKind.MY_AREA, this.localNode)));
				}
				else{
					this.send(new Message(newNode.ip,newNode.port,messageKind.NOT_MY_AREA, this.localNode));
				}
				break;
			//if phone received MSG_JSON, display the json received
			case MSG_JSON:
				System.out.println("Received \"MSG_JSON\"");
				System.out.println("Route is "+message.getJsonRoute().toString());
				JSONObject coordinates = new JSONObject();
				int count = 0;
				for(String each:message.getJsonRoute()){
					/*String[] latlng = each.split(",");
					Double[] point = new Double[2];
					point[0] = Double.valueOf(latlng[0]);
					point[1] = Double.valueOf(latlng[1]);*/

					try {
						coordinates.put(String.valueOf(count), each);
					}catch(JSONException ex){
						ex.printStackTrace();
					}
					count++;

				}
				System.out.println(coordinates.toString()																			);

				TestBench.finalRoute = coordinates;

			/* if my_area, set the destloc, startnodeip, send out the request
			 * else, send req_start to the the closestnode*/
				return;
			case MY_AREA:
				System.out.println("Received \"MY_AREA\"");
				Message jsonRequest = new Message(newNode.ip,newNode.port,messageKind.MSG_JSON, this.localNode);
				jsonRequest.setSenderNode(localNode);
				jsonRequest.setPhoneIP(this.localNode.ip);
				jsonRequest.setPhonePort(this.localNode.port);
				jsonRequest.setDestLoc(TestBench.destinationUser);
				jsonRequest.setStartNodeIP(newNode.ip);
				jsonRequest.setJsonRoute(new ArrayList<String>());
				jsonRequest.setKind(messageKind.MSG_JSON);
				jsonRequest.setSenderNode(localNode);
				send(jsonRequest);
				break;
			case NOT_MY_AREA:
				System.out.println("Received \"NOT_MY_AREA\"");
				Node newDest = message.getClosestNode();
				message.setKind(messageKind.REQ_START);
				message.setDestIP(newDest.ip);
				message.setDestPort(newDest.port);
				message.setSenderNode(localNode);
				send(message);
				break;
			default:
				break;
		}

		// the receive buffer from the Homeworks
		synchronized (receive_queue) {
			receive_queue.add(message);
		}

		// put delayed messages into receive queue
		for (Message msg : new ArrayList<Message>(this.delay_receive_queue)) {
			synchronized (receive_queue) {
				receive_queue.add(msg);
			}
		}
		delay_receive_queue.clear();
	}

	private void listen_server() {


		System.out.println("Starting MessagePasser server with address = " + this.localNode.getName());
		int counter = 0;
		ServerSocket listenSocket = null;
		try {
			listenSocket = new ServerSocket(this.localNode.port);

			while (true) {
				Socket clientSocket = listenSocket.accept();
				System.out.println("server thread");
				new Connection2(clientSocket, this);
				System.out.println("Server received a new connection: # " + counter);
				counter++;
			}
		} catch (IOException e) {
			System.out.println("Listen socket:" + e.getMessage());
		} finally {
			try {
				if (listenSocket != null)
					System.out.println("Closing socket");
				listenSocket.close();
			} catch (IOException e) {
				System.out.println("Failed to close server.");
			}
		}

	}

	public void printFoundNodes(){
		System.out.println("Printing Found Nodes----------");
		System.out.println(this.localNode.getName()+" : "+this.localNode.toString());

		for (Entry<String, Node> entry : this.foundNodes.entrySet()) {
			System.out.println(entry.getKey()+" : "+entry.getValue());
		}

//		int numFoundNodes = this.foundNodes.size();
//		for (int i = 0; i<numFoundNodes; i++){
////			System.out.println(" "+i+": "+this.foundNodes.get(i).toString());
//			System.out.println(" "+i+": "+this.foundNodes..get(i).toString());
//		}
		System.out.println("------------------------------");
	}

}

class Connection2 extends Thread {

	DataInputStream in;
	DataOutputStream out;
	ObjectOutputStream outObj;
	ObjectInputStream inObj;

	Socket clientSocket;
	P2PNetwork p2p;

	public Connection2(Socket aClientSocket, P2PNetwork p2p) {
		this.p2p = p2p;
		try {

			clientSocket = aClientSocket;
			in = new DataInputStream(clientSocket.getInputStream());
			out = new DataOutputStream(clientSocket.getOutputStream());
			this.start();
			System.out.println("Connection created with " + aClientSocket.getInetAddress());
		} catch (IOException e) {
			System.out.println("Connection:" + e.getMessage());
		}
	}

	public void run() {
		Gson gson = new Gson();
		try {
			while (true) {
				String json = in.readUTF();
				System.out.println("Received json is "+json.toString());
				JSONObject testJSON = null;
				JSONObject mapJSON = null;
				Message message = gson.fromJson(json, Message.class);
				System.out.println("Message received " + message.toString());
				System.out.println("JSON in message is "+message.getJsonRoute());


				p2p.receive_message(message, this);
			}

		} catch (EOFException e) {
			System.out.println("Server EOF:" + e.getMessage());
		} catch (IOException e) {
		} finally {
			try {
				if (clientSocket != null) {
					clientSocket.close();
				}
			} catch (IOException e) {
				System.out.println("Failed to close connection!");
			}
		}

	}

	public void write_object(Object object) {
		try {
			synchronized (outObj) {
				this.outObj.writeObject(object);
			}
		} catch (IOException e) {
			System.out.println("error sending message - client side:" + e.getMessage());
			e.printStackTrace();
		}
	}

	public void write(String json) {
		try {
			this.out.writeUTF(json);
		} catch (IOException e) {
			System.out.println("error sending message - client side:" + e.getMessage());
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			this.clientSocket.close();
		} catch (IOException e) {
			System.out.println("Failed to close connection: ");
			e.printStackTrace();
		}
	}
}