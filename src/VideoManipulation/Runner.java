package VideoManipulation;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import Audio.AudioToString;

import com.sun.jna.NativeLibrary;

public class Runner {

	private static JFileChooser ourFileSelector = new JFileChooser();
	
	public static void main( String[] args ) throws InterruptedException, IOException
	   {
		boolean found = new NativeDiscovery().discover();
        System.out.println(found);
        System.out.println(LibVlc.INSTANCE.libvlc_get_version());
		
        new NativeDiscovery().discover();
        
		NativeLibrary.addSearchPath("libvlc", "C:\\Program Files\\VideoLAN\\VLC");
		NativeLibrary.addSearchPath("libvlccore", "C:\\Program Files\\VideoLAN\\VLC");
		
		final String vlcPath, mediaPath;
		File ourFile;
		
		ourFileSelector.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		ourFileSelector.showSaveDialog(null);
		ourFile = ourFileSelector.getSelectedFile();
		vlcPath = ourFile.getAbsolutePath();
//		System.out.println(vlcPath);
		
		ourFileSelector.setFileSelectionMode(JFileChooser.FILES_ONLY);
		ourFileSelector.showSaveDialog(null);
		ourFile = ourFileSelector.getSelectedFile();
		mediaPath = ourFile.getAbsolutePath();
		String fileName = ourFile.getName();
		

		/** 
		 * Runs the FIRST CUSTOM AUDIO xuggler algorithm for detecting key frames
		 * **/
		/*		AudioToString trial1 = new AudioToString(mediaPath);
		trial1.extractAudio(); */
		
		
		/** 
		 * Runs the SECOND CUSTOM VIDEO xuggler algorithm for detecting key frames
		 * **/
		/*		FrameCollector populate = new FrameCollector();
		populate.main(args);
		xugglerCompare_Key_2 compare2 = new xugglerCompare_Key_2(populate.getFrames(), populate.getFrameRate());
		compare2.main(args); */
		
		

				FrameCollector populate = new FrameCollector();
		populate.main(args);
		KeyFrameFinder_Xuggler_Method1 compare = new KeyFrameFinder_Xuggler_Method1(populate.getFrames());
		compare.findKeys();
		
		
		
		/** 
		 * Runs the DEFAULT VIDEO xuggler algorithm for detecting key frames
		 * **/
/*		FindKeyFrames_Xuggler_Default keys1 = new FindKeyFrames_Xuggler_Default(mediaPath);
		keys1.testSeekKeyFrameCheckIndex();
		for (int x = 0; x < keys1.getTimeStamps().size(); x++)
		{
			FrameExtractor extractor1 = new FrameExtractor(keys1.getTimeStamps().get(x));
			extractor1.start();
		}*/
		

	   }
}
