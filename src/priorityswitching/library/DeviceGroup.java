package priorityswitching.library;

public abstract class DeviceGroup <C>{
	protected byte channel=1;
	protected C value;
	
	public byte getChannel() {
		return channel;
	}

	public void setChannel(byte channel) {
		this.channel = channel;
	}

	public C getValue() {
		return value;
	}

	public void setValue(C value) {
		this.value = value;
	}
	
	public abstract String getType();
	
	public abstract DeviceGroup clone();
	
	public abstract void resetValue();
	
	
}
