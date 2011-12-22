package spine.payload.codec.android;

import spine.datamodel.Node;
import spine.datamodel.functions.SpineCodec;
import spine.datamodel.functions.SpineObject;
import spine.exceptions.MethodNotSupportedException;

import spine.datamodel.functions.ShimmerNonSpineSetupSensor;


// Payload format
// byte		description
// 0		sensor id		
// 1		command
// 2 - 7		bluetooth address (6 bytes)

public class ShimmerNonSpineSetupSensor_codec extends SpineCodec {
	
	public byte[] encode(SpineObject payload) throws MethodNotSupportedException {
		byte[] base = new byte[8 + 255];
		
		ShimmerNonSpineSetupSensor dd = (ShimmerNonSpineSetupSensor)payload;

		try {
			base[0] = dd.getSensor();
			base[1] = dd.getCommand();
			
			for (int i = 0; i < 6; i++) {
				base[i + 2] = dd.getBtAddress()[i];
			}

			for (int i = 0; i < 255; i++) {
				base[i + 8] = dd.getBtName()[i];
			}
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
				
		return base;
	};

	public SpineObject decode(Node node, byte[] payload) throws MethodNotSupportedException {
		throw new MethodNotSupportedException("decode");
	}
}
