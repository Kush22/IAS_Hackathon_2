package xmlParser;

public class Action {
	String eventId;
	String sensorActionId;
	String actionJarLocation;
	String classDescription;
	String location;
	
	public String getEventId() {
		return eventId;
	}
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
	public String getSensorActionId() {
		return sensorActionId;
	}
	public void setSensorActionId(String sensorActionId) {
		this.sensorActionId = sensorActionId;
	}
	public String getActionJarLocation() {
		return actionJarLocation;
	}
	public void setActionJarLocation(String actionJarLocation) {
		this.actionJarLocation = actionJarLocation;
	}
	public String getClassDescription() {
		return classDescription;
	}
	public void setClassDescription(String classDescription) {
		this.classDescription = classDescription;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
}
