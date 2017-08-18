package net.neophyte.streaming.storm.sample.config;

/**
 * 
 * @author shuvro
 *
 */
public class BoltConfiguration {

	private String id = null;
	private long executeSleepTimeInMilisec = 10;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getExecuteSleepTimeInMilisec() {
		return executeSleepTimeInMilisec;
	}

	public void setExecuteSleepTimeInMilisec(long executeSleepTimeInMilisec) {
		this.executeSleepTimeInMilisec = executeSleepTimeInMilisec;
	}
}
