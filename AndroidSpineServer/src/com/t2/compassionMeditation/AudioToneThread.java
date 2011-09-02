package com.t2.compassionMeditation;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

class AudioToneThread extends Thread {
	private boolean isRunning = false;
	private boolean cancelled = false;
	private AndroidAudioDevice device;	
	private float frequency = 440;
	private float increment = (float)(2*Math.PI) * frequency / 44100; // angular increment for each sample	
	
	public AudioToneThread() {
	}
	
	public float getFrequency() {
		return frequency;
	}

	public void setFrequency(float aFrequency) {
		
		if (frequency != aFrequency) {
			frequency = aFrequency;
			increment = (float)(2*Math.PI) * frequency / 44100; // angular increment for each sample
		}
	}

	@Override
	public void run() {
		isRunning = true;
		
      float angle = 0;
      device = new AndroidAudioDevice( );
      float samples[] = new float[1024];
		
		
		while(true) {
			// Break out if this was cancelled.
			if(cancelled) {
				break;
			}
			
          for( int i = 0; i < samples.length; i++ )
          {
             samples[i] = (float)Math.sin( angle );
             angle += increment;
          }

          device.writeSamples( samples );			
			
		}
		

		isRunning = false;
	}
	
	
	public void cancel() {
		this.cancelled = true;
		device.stop();
		
	}
	
	public boolean isRunning() {
		return this.isRunning;
	}
	
	
	public class AndroidAudioDevice
	{
	   AudioTrack track;
	   short[] buffer = new short[1024];
	 
	   public AndroidAudioDevice( )
	   {
	      int minSize =AudioTrack.getMinBufferSize( 44100, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT );        
	      track = new AudioTrack( AudioManager.STREAM_MUSIC, 44100, 
	                                        AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, 
	                                        minSize, AudioTrack.MODE_STREAM);
	      track.play();        
	   }	   
	 
	   public void writeSamples(float[] samples) 
	   {	
	      fillBuffer( samples );
	      track.write( buffer, 0, samples.length );
	   }
	 
	   private void fillBuffer( float[] samples )
	   {
	      if( buffer.length < samples.length )
	         buffer = new short[samples.length];
	 
	      for( int i = 0; i < samples.length; i++ )
	         buffer[i] = (short)(samples[i] * Short.MAX_VALUE);;
	   }	
	   
	   public void stop() {
		   track.flush();
		   track.stop();
		   track.release();
		   
	   }
	}	
	
	
	
}   
