package priorityswitching.library;

import java.util.ArrayList;
import java.util.Arrays;

public class PrioritySwitcher {

	private static ArrayList<DeviceGroup> devices = new ArrayList<DeviceGroup>();
	private static ArrayList<DeviceApp> apps = new ArrayList<DeviceApp>();
	
	public static ArrayList<DeviceGroup> getCommandAppDeviceState(byte[] command){
		ArrayList<DeviceGroup> retDevices = new ArrayList<DeviceGroup>();
		if(command!=null && command.length>0){		
			
			byte priority=command[0];
			//update the App Device List Status with the current command
			DeviceApp dApp=null;
			for (int i = 0; i < apps.size(); i++) {
				if(((DeviceApp)apps.get(i)).getPriority()==priority){
					dApp=apps.get(i);
					break;
				}
			}
			//if there is no such an App, add it to the list
			if(dApp==null){
				//New App is created
				dApp= new DeviceApp(false, priority);
				//Copy current device list
				ArrayList<DeviceGroup> list=copyList();
				dApp.setAppDevices(list);
				//Add the newly created App to the App list
				apps.add(dApp);			
			}
								
			
			//Save new state by the command
			int index=1;						
			while(index<command.length){
				byte[] idBin=new byte[2];
				System.arraycopy(command, index, idBin, 0, 2);				
				index=saveStateOfApp(bytesToHex(idBin),command,index,dApp,retDevices);
			}
			
			//Checks if the App is active check for higher priority App
			boolean found=false;
			if(dApp.isActive()){
				
				for (int i = 0; i < apps.size(); i++) {
					if(apps.get(i).isActive() && apps.get(i).getPriority()>dApp.getPriority()){								
						found=true;
						break;
					}
				}
				
				
			}
					
			//Save the app
			/////////////////////////////////////////
			ArrayList<DeviceGroup> ad= dApp.getAppDevices();						
			for (int i = 0; i < ad.size(); i++) {							
				for (int j = 0; j < retDevices.size(); j++) {					
					if(retDevices.get(j).getChannel()==ad.get(i).getChannel()&&retDevices.get(j).getType().equals(ad.get(i).getType())){
						ad.get(i).setValue(retDevices.get(j).getValue());											
						break;
					}
				}							
			}
						 
			
			 
			//if available return null
			if(found || !dApp.isActive()){								
				//Return empty list
				return new ArrayList<DeviceGroup>();
			}
			
			//if not, update the devices and return the updated devices.			
			//update the App
			for (int i = 0; i < retDevices.size(); i++) {
				for (int j = 0; j < devices.size(); j++) {
					if(retDevices.get(i).getChannel()==devices.get(j).getChannel() && retDevices.get(i).getType().equals(devices.get(j).getType())){
						devices.get(j).setValue(retDevices.get(i).getValue());						
					}
				}
			}
			
		}
		return retDevices;
	}
	
	private static int saveStateOfApp(String idHex,byte[] command,int currentIndex, DeviceApp dApp, ArrayList<DeviceGroup> retDevices){
		int returnIndex=command.length;
				
		System.out.println("Hex : "+idHex);
		switch(idHex){
		
		//Request
		case  "0000":break;
		
		//Property
		case  "0001"://Set the app active or inactive
			byte[] data=new byte[1];
			System.arraycopy(command, currentIndex+2, data, 0, 1);			
			if(data[0]==0){//Inactive
				dApp.setActive(false);
			}else if(data[0]==1){//Active
				dApp.setActive(true);
			}
			
			//setting the index to check for next command 
			returnIndex=currentIndex+3;
			break;			
			
		case  "0002":break;
		case  "0003":break;
		case  "0004":break;
		
		//Generic
		case  "0032":break;
		case  "0033":break;
		case  "0034":break;
		case  "0035":break;
		case  "0036":break;
		case  "0037":break;
		case  "0038":break;
		case  "0039":break;
		case  "003A":break;
		case  "003B":break;
		case  "003C":break;
		case  "003D":break;		
			
		//Lightning	
		case "0064"://Turn ON				
			
			if(command.length>=currentIndex+2){//Command has the required byte length
				
				if(command.length==currentIndex+2){//single command with no channel
					//setting the index to check for next command 
					returnIndex=currentIndex+2;
					
					//Setting all light to maximum
					for (int i = 0; i < devices.size(); i++) {
						if(devices.get(i).getType().equals("Light")){
							LightDevice ld =(LightDevice) devices.get(i).clone();							
							ld.setValue((Integer) 65535);
							//System.out.println(ld.getValue());
							retDevices.add(ld);						
						}
					}
				}else if(command.length>=currentIndex+3){//single command with channel
					//setting the index to check for next command 
					returnIndex=currentIndex+3;
					
					byte[] channel=new byte[1];
					System.arraycopy(command, currentIndex+2, channel, 0, 1);
					if(channel[0]==0){//All channels
						for (int i = 0; i < devices.size(); i++) {
							if(devices.get(i).getType().equals("Light")){
								LightDevice ld =(LightDevice) devices.get(i).clone();
								ld.setValue((Integer) 65535);
								retDevices.add(ld);
							}
						}
					}else{
						for (int i = 0; i < devices.size(); i++) {
							if(devices.get(i).getType().equals("Light")&&devices.get(i).getChannel()==channel[0]){
								LightDevice ld =(LightDevice) devices.get(i).clone();
								ld.setValue((Integer) 65535);
								retDevices.add(ld);
								break;
							}
						}
					}
				}
			}
			
			
			
			break;
		case "0065"://Turn OFF
			if(command.length>=currentIndex+2){//Command has the required byte length
				//setting the index to check for next command 
				returnIndex=currentIndex+2;
				
				//setting all light to 0
				if(command.length==currentIndex+2){//single command with no channel
					for (int i = 0; i < devices.size(); i++) {
						if(devices.get(i).getType().equals("Light")){
							LightDevice ld =(LightDevice) devices.get(i).clone();
							ld.setValue((Integer) 0);
							retDevices.add(ld);							
						}
					}
				}else if(command.length>=currentIndex+3){//single command with channel
					//setting the index to check for next command 
					returnIndex=currentIndex+3;
					
					byte[] channel=new byte[1];
					System.arraycopy(command, currentIndex+2, channel, 0, 1);
					if(channel[0]==0){//All channels
						for (int i = 0; i < devices.size(); i++) {
							if(devices.get(i).getType().equals("Light")){
								LightDevice ld =(LightDevice) devices.get(i).clone();
								ld.setValue((Integer) 0);
								retDevices.add(ld);
							}
						}
					}else{
						for (int i = 0; i < devices.size(); i++) {
							if(devices.get(i).getType().equals("Light")&&devices.get(i).getChannel()==channel[0]){
								LightDevice ld =(LightDevice) devices.get(i).clone();
								ld.setValue((Integer) 0);
								retDevices.add(ld);
								break;
							}
						}
					}
				}
			}
			
			//setting the index to check for next command 
			returnIndex=currentIndex+3;
			break;
		case "0066"://Toggle
			
			break;
		case "0067"://Fade to value x in speed s (Milliseconds)
			
			break;
			
		case "0068"://Set to value x
			
			break;
			
		case "0069"://Set to value r,g,w in speed s
			
			break;
			
		
		//Climate
		case  "012C":break;
		case  "012D":break;
		case  "012E":break;
		case  "012F":break;
		
		//Weather
		case  "0400":break;
		case  "0401":break;
		case  "0402":break;
		case  "0403":break;
		case  "0404":break;
		case  "0405":break;
		case  "0406":break;
		
		//Identification
		case  "0384":break;
		
		//Override
		case  "FFDC":break;
		case  "FFDD":break;
		
		//Compressed
		case  "FFFE":break;
		
		
		default:
			
		}
		
		return returnIndex;			
	}
	
	public static ArrayList<DeviceGroup> getAllDeviceState(){
		return devices;
	}
	
	private static int getIndexOfDevice(DeviceGroup dg){
		for (int i = 0; i < devices.size(); i++) {
			DeviceGroup obj = (DeviceGroup) devices.get(i);
			if(obj.getType().equals(dg.getType()) && obj.getChannel()==dg.getChannel()){
				return i;
			}			
		}
		
		return -1;		
	}
	
	private static DeviceGroup getDeviceFromList(DeviceGroup dg){
		for (int i = 0; i < devices.size(); i++) {
			DeviceGroup obj = (DeviceGroup) devices.get(i);			
			if(obj.getType().equals(dg.getType()) && obj.getChannel()==dg.getChannel()){
				return obj;
			}			
		}
		
		return null;		
	}
	
	private static ArrayList<DeviceGroup> copyList() {
		ArrayList<DeviceGroup> rList= new ArrayList<DeviceGroup>();
		for (int i = 0; i < devices.size(); i++) {			
			DeviceGroup dg=devices.get(i).clone();
			dg.resetValue();
			rList.add(dg);
					
		}
		return rList;
	}
	
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	
	public static void addDevice(DeviceGroup dg){
		devices.add(dg);
	}
	
	public static void printStatus(){
		System.out.println("Device Status");
		for (int i = 0; i < devices.size(); i++) {
			DeviceGroup dg = devices.get(i);			
			System.out.println("Group : "+dg.getType()+" Channel : "+dg.getChannel()+" Value : "+dg.getValue());
		}
		
		System.out.println("Each App Status");
		for (int i = 0; i < apps.size(); i++) {
			DeviceApp da = apps.get(i);
			System.out.println("App Priority : "+da.getPriority()+" Active :"+da.isActive());
			ArrayList<DeviceGroup> devs = da.getAppDevices();
			for (int j = 0; j < devs.size(); j++) {
				DeviceGroup dg = devs.get(j);
				System.out.println("Group : "+dg.getType()+" Channel : "+dg.getChannel()+" Value : "+dg.getValue());
			}
			System.out.println();
		}
	}
	
	public static void main(String arg[]) throws Exception{		
		byte[] command = null;
		//PrioritySwitcher.getCommandAppDeviceState(command);
		
		//Initialise the PrioritySwitcher with all the devices
		//int i=1;
		//while(i<=5){
		//	//Creating Device
		//	LightDevice ld = new LightDevice((byte)i);
		//	//Adding to the list
		//	PrioritySwitcher.addDevice(ld);
		//	i++;
		//}
		
		LightDevice ld = new LightDevice((byte)1);
		PrioritySwitcher.addDevice(ld);
		
		PrioritySwitcher.printStatus();
		//Send commands
		//byte[] cmd = {(byte) 0x05/*Priority*/,(byte) 0x00/*ID Hex byte 1 */,(byte) 0x01/*ID Hex byte 2*/,(byte) 0x01/*Data Byte*/,(byte) 0x00/*Channel Byte*/};
		
		//Activates App 05 
		byte[] cmd0 = {(byte) 0x05/*Priority*/,(byte) 0x00/*ID Hex byte 1 */,(byte) 0x01/*ID Hex byte 2*/,(byte) 0x01/*Data Byte*/};
		//Activates App 01
		byte[] cmd1 = {(byte) 0x09/*Priority*/,(byte) 0x00/*ID Hex byte 1 */,(byte) 0x01/*ID Hex byte 2*/,(byte) 0x01/*Data Byte*/};
		//Deactivates App 06
		byte[] cmd2 = {(byte) 0x06/*Priority*/,(byte) 0x00/*ID Hex byte 1 */,(byte) 0x01/*ID Hex byte 2*/,(byte) 0x00/*Data Byte*/};
		//Activates App 04
		byte[] cmd4 = {(byte) 0x04/*Priority*/,(byte) 0x00/*ID Hex byte 1 */,(byte) 0x01/*ID Hex byte 2*/,(byte) 0x01/*Data Byte*/};
		
		//App 04 Turn ON All Lights
		byte[] cmd5 = {(byte) 0x04/*Priority*/,(byte) 0x00/*ID Hex byte 1 */,(byte) 0x64/*ID Hex byte 2*/};
		//App 06 Turn ON All Lights
		byte[] cmd6 = {(byte) 0x06/*Priority*/,(byte) 0x00/*ID Hex byte 1 */,(byte) 0x64/*ID Hex byte 2*/};
		//App 01 Turn ON Channel 1 Light and Turn OFF Channel 2 Light
		byte[] cmd7 = {(byte) 0x09/*Priority*/,(byte) 0x00/*ID Hex byte 1 */,(byte) 0x64/*ID Hex byte 2*/,(byte) 0x01/*Channel */,(byte) 0x00/*ID Hex byte 1 */,(byte) 0x65/*ID Hex byte 2*/,(byte) 0x02/*Channel */};
		//App 05 Turn ON Channel 3 Light and Channel 5 Light
		byte[] cmd8 = {(byte) 0x05/*Priority*/,(byte) 0x00/*ID Hex byte 1 */,(byte) 0x64/*ID Hex byte 2*/,(byte) 0x03/*Channel */,(byte) 0x00/*ID Hex byte 1 */,(byte) 0x64/*ID Hex byte 2*/,(byte) 0x05/*Channel */};
		
		//Activates App 05
		PrioritySwitcher.getCommandAppDeviceState(cmd0);		
		PrioritySwitcher.printStatus();
		//Activates App 01
		PrioritySwitcher.getCommandAppDeviceState(cmd1);
		PrioritySwitcher.printStatus();
		//Deactivates App 06
		PrioritySwitcher.getCommandAppDeviceState(cmd2);
		PrioritySwitcher.printStatus();
		//Activates App 04
		PrioritySwitcher.getCommandAppDeviceState(cmd4);
		PrioritySwitcher.printStatus();
		//App 04 Turn ON All Lights
		PrioritySwitcher.getCommandAppDeviceState(cmd5);
		PrioritySwitcher.printStatus();
		//App 06 Turn ON All Lights
		PrioritySwitcher.getCommandAppDeviceState(cmd6);
		PrioritySwitcher.printStatus();
		//App 01 Turn ON Channel 1 Light and Turn OFF Channel 2 Light
		PrioritySwitcher.getCommandAppDeviceState(cmd7);
		PrioritySwitcher.printStatus();
		//App 05 Turn ON Channel 3 Light and Channel 5 Light
		PrioritySwitcher.getCommandAppDeviceState(cmd8);
		PrioritySwitcher.printStatus();
	}
}
