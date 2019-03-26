import com.fazecast.jSerialComm.*;	//Connnection to ports

public class SerialPortTest{
	SerialPort thePort;

	public boolean AutoEstablishPort(){
		SerialPort[] ports = SerialPort.getCommPorts();
		if (ports.length == 0){
			return false;
		}

		for (int i = 0; i < ports.length; i++) {
			try{
				thePort = ports[i];
				if (thePort.openPort()){
					thePort.setBaudRate(9600);
					return true;
				}
			}
			catch(Exception e){}
		}
		return false;
	}

	public String getPortName(){
		return thePort.getSystemPortName();
	}

	public void readValue(SimpleCallBack mValue){
		thePort.addDataListener(new SerialPortDataListener(){
			@Override
			public int getListeningEvents(){ return SerialPort.LISTENING_EVENT_DATA_AVAILABLE; }

			@Override
			public void serialEvent(SerialPortEvent event){
				SerialPort comPort = event.getSerialPort();
				byte[] newData = new byte[comPort.bytesAvailable()];
				int numRead = comPort.readBytes(newData, newData.length);
				//String value = new String(newData);
				mValue.callback(String.valueOf(new String(newData).charAt(0)));
				//tileValue.callback(mValue);
				System.out.println("Read!");
			}
		});
	}

	public SerialPort getPort(){
		return thePort;
	}

	public interface SimpleCallBack {
		void callback(String data);
	}
}