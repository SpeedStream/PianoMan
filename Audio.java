import java.io.*;
import sun.audio.*;
import java.io.File;

public class Audio{
	private AudioStream mAudio = null;
	
	public void setAudio(String file){
		try{
			FileInputStream in = new FileInputStream("audio/" + file);
			mAudio = new AudioStream(in);
		}
		catch(Exception fnfe){
			System.out.println("Exception found. " + fnfe.getMessage());
		}
	}

	public void play(){
		try{
			AudioPlayer.player.start(mAudio);
			//AudioPlayer.player.stop(mAudio);
		}
		catch(Exception e){
			System.out.println("Exception found. " + e.getMessage());
		}
	}
	public void stop(){
		try{
			AudioPlayer.player.stop(mAudio);
		}
		catch(Exception e){
			System.out.println("Exception found. " + e.getMessage());
		}
	}
}