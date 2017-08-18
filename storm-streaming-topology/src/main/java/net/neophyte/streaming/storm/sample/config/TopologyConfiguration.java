package net.neophyte.streaming.storm.sample.config;

/**
 * 
 * @author shuvro
 *
 */
public class TopologyConfiguration {

	private String name = null;
	private int numOfSpoutTasks = 1;
	private int numOfBoltTasks = 1;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNumOfSpoutTasks() {
		return numOfSpoutTasks;
	}

	public void setNumOfSpoutTasks(int numOfSpoutTasks) {
		this.numOfSpoutTasks = numOfSpoutTasks;
	}

	public int getNumOfBoltTasks() {
		return numOfBoltTasks;
	}

	public void setNumOfBoltTasks(int numOfBoltTasks) {
		this.numOfBoltTasks = numOfBoltTasks;
	}
}
