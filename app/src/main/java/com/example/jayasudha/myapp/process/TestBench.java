package com.example.jayasudha.myapp.process;


import java.net.InetAddress;
import java.net.UnknownHostException;
import com.example.jayasudha.myapp.communication.*;
import com.example.jayasudha.myapp.utils.*;

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
	static P2PNetwork p2p;
	static P2PRegion region;
	static NodePatrolArea initial_patrol_area;
	static NodeLocation node_loc;
	static Node myNode;

	public static void main(String[] args) {
		// --------------------------------
		// initialize - get necessary parameter inputs

		int port = 4050; // Ports can be in the range 4000 - 4200
		String IP = "172.29.92.26"; // localhost
		//phone doesnt have range
//		double[] range = {40.441713,-79.947789,40.443844,-79.947789};
//		double[] location = {40.443052,-79.944806};
		//take this as user location
		double[] location = {40.442546,-79.941759}; // a second spot on campus


		// --------------------------------
		// construct the required objects

		//user has no region, no patrol region
		//	region = new P2PRegion(range);

		//	initial_patrol_area = new NodePatrolArea(range);
		node_loc = new NodeLocation(location);
		//passing region,patrol_area = null for phone
		myNode = new Node(null,null,node_loc,port,IP);

		p2p = new P2PNetwork(myNode);

	}


}