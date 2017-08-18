package net.neophyte.streaming.storm.sample.config;

import java.io.InputStream;
import java.util.Properties;

/**
 * 
 * @author shuvro
 *
 */
public class PropertyLoader {

	private static final String LOCAL_RUN_PROPERTY = "config.localrun";

	private static final String BROKER_URL_PROPERTY = "jms.brokerUrl";
	private static final String USERID_PROPERTY = "jms.userId";
	private static final String PASSWORD_PROPERTY = "jms.password";
	private static final String RECEIVE_TIMEOUT_PROPERTY = "jms.consumer.receiveTimeout";

	private static final String TOPOLOGY_NAME_PROPERTY = "topology.name";
	private static final String SPOUT_ID_PROPERTY = "topology.spout.id";
	private static final String BOLT_ID_PROPERTY = "topology.bolt.id";
	private static final String SPOUT_TASK_NUMBER_PROPERTY = "topology.spout.numOfTasks";
	private static final String BOLT_TASK_NUMBER_PROPERTY = "topology.bolt.numOfTasks";

	private static final String BOLT_EXECUTE_SLEEP_PROPERTY = "bolt.executeSleepTimeInMilisec";

	private static final String SOURCE_QUEUE_NAME_PROPERTY = "app.sourceQueueName";
	private static final String DESTINATION_QUEUE_NAME_PROPERTY = "app.destinationQueueName";
	private static final String ERROR_QUEUE_NAME_PROPERTY = "app.errorQueueName";

	private Properties properties = new Properties();
	private TopologyConfiguration topologyConfiguration = null;
	private BoltConfiguration boltConfiguration = null;
	private SpoutConfiguration soputConfiguration = null;
	private JMSConfiguration jmsConfiguration = null;
	private AppConfiguration appConfiguration = null;
	private String localRunConfigVal = null;
	private boolean isLocalRun = false;
	
	private String propertyFileName = null;

	public PropertyLoader(String propertyFileName) {
		this.propertyFileName = propertyFileName;
	}

	private void loadProperty() throws Exception {
		if (properties == null || properties.isEmpty()) {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			try (InputStream resourceStream = loader.getResourceAsStream(propertyFileName)) {
				if (resourceStream == null) {
					System.out.println("@Could not load the property file name: " + propertyFileName);
					throw new Exception("Could not load the property file name: " + propertyFileName);
				}
				properties.load(resourceStream);
				if (properties.isEmpty()) {
					System.out.println(
							"@properties.load(), Could not load the property file name: " + propertyFileName);
					throw new Exception("Could not load the property file name: " + propertyFileName);
				}
			}
		}
	}

	public BoltConfiguration getBoltConfiguration() throws Exception {

		if (boltConfiguration == null) {
			loadProperty();
			boltConfiguration = new BoltConfiguration();
			boltConfiguration.setId(properties.getProperty(BOLT_ID_PROPERTY));
			int boltSleepTime = Integer.parseInt(properties.getProperty(BOLT_EXECUTE_SLEEP_PROPERTY));
			boltConfiguration.setExecuteSleepTimeInMilisec(boltSleepTime);
		}

		return boltConfiguration;
	}

	public SpoutConfiguration getSpoutConfiguration() throws Exception {

		if (soputConfiguration == null) {
			loadProperty();
			soputConfiguration = new SpoutConfiguration();
			soputConfiguration.setId(properties.getProperty(SPOUT_ID_PROPERTY));
		}

		return soputConfiguration;
	}

	public JMSConfiguration getJMSConfiguration() throws Exception {

		if (jmsConfiguration == null) {
			loadProperty();
			jmsConfiguration = new JMSConfiguration();
			jmsConfiguration.setBrokerUrl(properties.getProperty(BROKER_URL_PROPERTY));
			jmsConfiguration.setUserId(properties.getProperty(USERID_PROPERTY));
			jmsConfiguration.setPassword(properties.getProperty(PASSWORD_PROPERTY));
			jmsConfiguration.setReceiveTimeout(Integer.parseInt(properties.getProperty(RECEIVE_TIMEOUT_PROPERTY)));
		}

		return jmsConfiguration;
	}

	public AppConfiguration getAppConfiguration() throws Exception {

		if (appConfiguration == null) {
			loadProperty();
			appConfiguration = new AppConfiguration();
			appConfiguration.setSourceQueueName(properties.getProperty(SOURCE_QUEUE_NAME_PROPERTY));
			appConfiguration.setDestinationQueueName(properties.getProperty(DESTINATION_QUEUE_NAME_PROPERTY));
			appConfiguration.setErrorQueueName(properties.getProperty(ERROR_QUEUE_NAME_PROPERTY));
		}

		return appConfiguration;
	}

	public boolean isLocalRun() throws Exception {
		if (localRunConfigVal == null) {
			loadProperty();
			localRunConfigVal = properties.getProperty(LOCAL_RUN_PROPERTY);
			isLocalRun = localRunConfigVal.equals("true");
		}
		return isLocalRun;
	}

	public TopologyConfiguration getTopologyConfiguration() throws Exception {
		if (topologyConfiguration == null) {
			loadProperty();
			topologyConfiguration = new TopologyConfiguration();
			topologyConfiguration.setName(properties.getProperty(TOPOLOGY_NAME_PROPERTY));
			int spoutTaskNumber = Integer.parseInt(properties.getProperty(SPOUT_TASK_NUMBER_PROPERTY));
			topologyConfiguration.setNumOfSpoutTasks(spoutTaskNumber);
			int boltTaskNumber = Integer.parseInt(properties.getProperty(BOLT_TASK_NUMBER_PROPERTY));
			topologyConfiguration.setNumOfBoltTasks(boltTaskNumber);
		}

		return topologyConfiguration;
	}
}
