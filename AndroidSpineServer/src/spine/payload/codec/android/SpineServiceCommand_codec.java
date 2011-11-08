package spine.payload.codec.android;

import spine.datamodel.Node;
import spine.datamodel.functions.SpineCodec;
import spine.datamodel.functions.SpineObject;
import spine.datamodel.functions.SpineServiceCommand;
import spine.exceptions.MethodNotSupportedException;

import spine.datamodel.functions.ShimmerNonSpineSetupSensor;


// Payload format
// byte		description
// 0		sensor id		
// 1		command
// 2 - 7		bluetooth address (6 bytes)

public class SpineServiceCommand_codec extends SpineCodec {
	
	public byte[] encode(SpineObject payload) throws MethodNotSupportedException {
		byte[] base = new byte[7 + 255];
		
		SpineServiceCommand dd = (SpineServiceCommand)payload;

		try {
			base[0] = dd.getCommand();
			
			for (int i = 0; i < 6; i++) {
				base[i + 1] = dd.getBtAddress()[i];
			}

			for (int i = 0; i < 255; i++) {
				base[i + 7] = dd.getBtName()[i];
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
