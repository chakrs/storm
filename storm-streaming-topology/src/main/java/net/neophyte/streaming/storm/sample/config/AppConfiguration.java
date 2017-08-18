package net.neophyte.streaming.storm.sample.config;

/**
 * 
 * @author shuvro
 *
 */
public class AppConfiguration {

	private String sourceQueueName = null;
	private String destinationQueueName = null;
	private String errorQueueName = null;

	public String getSourceQueueName() {
		return sourceQueueName;
	}

	public void setSourceQueueName(String sourceQueueName) {
		this.sourceQueueName = sourceQueueName;
	}

	public String getDestinationQueueName() {
		return destinationQueueName;
	}

	public void setDestinationQueueName(String destinationQueueName) {
		this.destinationQueueName = destinationQueueName;
	}

	public String getErrorQueueName() {
		return errorQueueName;
	}

	public void setErrorQueueName(String errorQueueName) {
		this.errorQueueName = errorQueueName;
	}
}
