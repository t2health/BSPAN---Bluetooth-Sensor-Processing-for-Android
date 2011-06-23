package spine.datamodel;


public class MindsetData  extends Data {
	private static final long serialVersionUID = 1L;	
	
	public int poorSignalStrength;
	public int attention;
	public int meditation;
	public int blinkStrength;
	public int rawSignal;
	private byte[] spectralData;
	
	public int delta;
	public int theta;
	public int lowAlpha;
	public int highAlpha;
	public int lowBeta;
	public int highBeta;
	public int lowGamma;
	public int midGamma;
	
	
	
	private Node node;
	public byte functionCode;	
	public byte sensorCode;
	public byte exeCode;
	
	public MindsetData(	byte functionCode, byte sensorCode, byte exeCode, 
						byte poorSignalStrength, byte attention, 
						byte meditation, byte blinkStrength) {
		this.functionCode = functionCode;
		this.sensorCode = sensorCode;
		this.exeCode = exeCode;
		
		this.poorSignalStrength = poorSignalStrength;
		this.attention = attention;
		this.meditation = meditation;
		this.blinkStrength = blinkStrength;
	}	

	
	
	
}
