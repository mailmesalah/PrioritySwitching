package priorityswitching.library;

import java.util.ArrayList;

public class DeviceApp {
	private ArrayList appDevices = new ArrayList<DeviceGroup>();
	private boolean active=false;
	private byte priority=1;
	private boolean superApp=false;
	
	public ArrayList<DeviceGroup> getAppDevices() {
		return appDevices;
	}
	public void setAppDevices(ArrayList<DeviceGroup> appDevices) {
		this.appDevices = appDevices;
	}
	
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public byte getPriority() {
		return priority;
	}
	public void setPriority(byte priority) {
		this.priority = priority;
	}
	
	public boolean isSuperApp() {
		return superApp;
	}
	public void setSuperApp(boolean superApp) {
		this.superApp = superApp;
	}
	
	public DeviceApp(boolean status, byte priority) {
		super();
		this.active = status;
		this.priority = priority;
	}
	public DeviceApp(boolean active, byte priority, boolean superApp) {
		super();
		this.active = active;
		this.priority = priority;
		this.superApp = superApp;
	}
	
	
}
