package priorityswitching.library;

public class LightDevice extends DeviceGroup<Integer> {

	public LightDevice(){
		super.channel=1;
		super.value=0;
	}
	
	public LightDevice(byte channel) throws Exception{
		if(channel<=255 && channel>0){
			super.channel=channel;
		}else{
			super.channel=1;
			throw new Exception("Wrong Channel No:"+channel);
		}
		super.value=0;
	}
	
	public LightDevice(Integer value){
		super.channel=1;
		super.value=value;
	}
	
	public LightDevice(byte channel,Integer value) throws Exception{
		if(channel<=255 && channel>0){
			super.channel=channel;
					
		}else{
			super.channel=1;
			throw new Exception("Wrong Channel No:"+channel);
		}
		super.value=value;
	}
	
	@Override
	public String getType() {	
		return "Light";
	}

	@Override
	public LightDevice clone() {
		LightDevice ld = null;
		try {
			byte chan = new Byte(channel);
			int val= new Integer(value);
			ld = new LightDevice(chan, val);						
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return ld;
	}

	@Override
	public void resetValue() {
		value=0;
		
	}
	
	

}
