package org.rsg.carnivore.cache;

import java.io.Serializable;

import org.rsg.carnivore.Constants;
import org.rsg.lib.time.TimeUtilities;

//a small class for timestamping any object 
//designed for the objects going in/out of Cache (which is a java.util.Stack)
public class TimestampedObject implements Serializable {
	private static final long serialVersionUID = Constants.VERSION;
	public long timestamp; //milliseconds since the epoch
	public Object object;

	public TimestampedObject(long timestamp, Object object) {
		this.timestamp = timestamp;
		this.object = object;
	}

	public TimestampedObject(Object object) {
		this.timestamp = TimeUtilities.currentTime();
		this.object = object;
	}

	public String toString() {
		return timestamp + "ms " + object.getClass().toString();
	}
}
