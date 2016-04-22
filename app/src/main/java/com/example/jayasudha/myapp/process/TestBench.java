package com.example.jayasudha.myapp.process;


import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import com.example.jayasudha.myapp.communication.*;
import com.example.jayasudha.myapp.utils.NodeLocation;
import com.example.jayasudha.myapp.utils.NodePatrolArea;
import com.example.jayasudha.myapp.utils.P2PRegion;

import org.json.JSONObject;

import utils.Node;

public class TestBench {


	
	/*
	 * TODO:
	 * Refactor:
	 * - P2PNetwork.java should hold details about the P2P network  
	 * -- neighboring nodes
	 * 
	 * Bootstrapping:
	 * - Just need to find one, and that will help you get to the next one
	 * - Figure out how to divide up the network
	 * 
	 * Coordinates:
	 * - CMU is not perfectly aligned north/south
	 * - Southwest corner: 40.441713 | Longitude: -79.947789
	 * - Southeast corner: 40.440309 | Longitude: -79.941298 (roughly)
	 * - Northwest corner: 40.444963 | Longitude: -79.946405 (roughly)
	 * - Northeast corner: 40.443844 | Longitude: -79.947789 (roughly)
	 * 
	 * - Position 1: 40.443052 | Longitude: -79.944806
	 * - Position 2: 40.442546 | Longitude: -79.941759
	 * 
	 */

	protected static final String receive_block = new String();
	static P2PNetwork p2p2;
	static P2PRegion region;
	static NodePatrolArea initial_patrol_area;
	static NodeLocation node_loc;
	static Node myNode;
	volatile public static JSONObject finalRoute = new JSONObject();
	public static String destinationUser = new String();
	public TestBench(String pos,String dest) throws UnknownHostException, IOException {


		// pos is the position of user and dest is his destination. Parse pos into double.
		//TODO : CHANGE DEST TO DOUBLE[] TOO

		pos = "40.4431325,-79.9423925"; //delete this
		/* set phone's location */
		String latLng[] = pos.split(",");
		double location[] = new double[2];
		location[0] = Double.parseDouble(latLng[0]);
		location[1] = Double.parseDouble(latLng[1]);

		/*set destination */
		destinationUser = dest;
		destinationUser = "40.4429675,-79.9422275";

		// --------------------------------
		// initialize - get necessary parameter inputs
		int port = 4050;


		String IP = findMyIPaddr();
		IP = "192.168.2.127"; //hardcoding it to phone's ip

		double[] range = {0, 0, 0, 0};
	//	double[] location = {2, 2}; // node 5

		// --------------------------------
		// construct the required objects
		region = new P2PRegion(range);
		initial_patrol_area = new NodePatrolArea(range);
		node_loc = new NodeLocation(location);
		myNode = new Node(initial_patrol_area, region, node_loc, port, IP);

		p2p2 = new P2PNetwork(myNode);


		// --------------------------------
		// Execute something here

		//run_receiver(p2p2);
	}


	private static String findMyIPaddr() {
		// TODO: make this failure tolerant
		InetAddress x = null;
		try {
			x = InetAddress.getLocalHost();


		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return x.getHostAddress();

	}
}



