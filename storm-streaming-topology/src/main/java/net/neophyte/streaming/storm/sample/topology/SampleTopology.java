package net.neophyte.streaming.storm.sample.topology;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.topology.TopologyBuilder;

import net.neophyte.streaming.storm.sample.bolt.SampleBolt;
import net.neophyte.streaming.storm.sample.config.AppConfiguration;
import net.neophyte.streaming.storm.sample.config.BoltConfiguration;
import net.neophyte.streaming.storm.sample.config.JMSConfiguration;
import net.neophyte.streaming.storm.sample.config.PropertyLoader;
import net.neophyte.streaming.storm.sample.config.SpoutConfiguration;
import net.neophyte.streaming.storm.sample.config.TopologyConfiguration;
import net.neophyte.streaming.storm.sample.jmsconnector.SimpleConnectionProvider;
import net.neophyte.streaming.storm.sample.spout.SampleSpout;

/**
 * 
 * @author shuvro
 *
 */
public class SampleTopology {

	private boolean isLocalRun = false;
	private TopologyConfiguration topologyConfig = null;
	private SpoutConfiguration soputConfig = null;
	private BoltConfiguration boltConfig = null;
	private JMSConfiguration jmsConfiguration = null;
	private AppConfiguration appConfiguration = null;
	private TopologyBuilder builder = null;
	private PropertyLoader propertyLoader = null;
	private boolean alreadyRunning = false;
	
	public SampleTopology(String propertyFileName) throws Exception {
		propertyLoader = new PropertyLoader(propertyFileName);
		this.isLocalRun = propertyLoader.isLocalRun();
		this.topologyConfig = propertyLoader.getTopologyConfiguration();
		this.soputConfig = propertyLoader.getSpoutConfiguration();
		this.boltConfig = propertyLoader.getBoltConfiguration();
		this.jmsConfiguration = propertyLoader.getJMSConfiguration();
		this.appConfiguration = propertyLoader.getAppConfiguration();
		build();
	}
	
	private void build() throws Exception {

		if(builder == null) {
			this.builder = new TopologyBuilder();
			builder.setSpout(soputConfig.getId(), new SampleSpout(jmsConfiguration, 
					appConfiguration.getSourceQueueName()),
					topologyConfig.getNumOfSpoutTasks());
	
			builder.setBolt(boltConfig.getId(),
					new SampleBolt(jmsConfiguration, appConfiguration.getDestinationQueueName(),
							appConfiguration.getDestinationQueueName()),
					topologyConfig.getNumOfBoltTasks()).shuffleGrouping(soputConfig.getId());
		}
	}
	
	public void run() throws Exception {

		if(builder == null || alreadyRunning) {
			System.out.println("Either TopologyBuilder is null or the topology is already running... exiting run()!");
			return;
		}
		
		Config config = new Config();
		if (isLocalRun) {/* for testing locally */
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology(topologyConfig.getName(), config, builder.createTopology());
			alreadyRunning = true;
			/* The topology will run for until the main thread sleeps */
			Thread.sleep(120000);
			cluster.deactivate(topologyConfig.getName());
			cluster.killTopology(topologyConfig.getName());
			/*
			 * wait few seconds before closing connection and shutting down the
			 * cluster
			 */
			Thread.sleep(30000);
			SimpleConnectionProvider.closeConnection();
			cluster.shutdown();
			System.exit(0);
		} else {
			StormSubmitter.submitTopology(topologyConfig.getName(), config, builder.createTopology());
			alreadyRunning = true;
		}
	}
}
