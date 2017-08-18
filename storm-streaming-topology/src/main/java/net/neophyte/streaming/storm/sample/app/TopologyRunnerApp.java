package net.neophyte.streaming.storm.sample.app;

import net.neophyte.streaming.storm.sample.topology.SampleTopology;

/**
 * 
 * @author shuvro
 *
 */
public class TopologyRunnerApp {
	private static final String PROPERTY_FILE_NAME = "config.properties";

	public static void main(String[] args) throws Exception {

		new SampleTopology(PROPERTY_FILE_NAME).run();
	}
}