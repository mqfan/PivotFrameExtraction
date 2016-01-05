package VideoManipulation;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.sun.jna.NativeLibrary;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

public class vlcPlayer {

	private JFrame ourFrame = new JFrame();
	private EmbeddedMediaPlayerComponent ourMediaPlayer;
	private String mediaPath = "";
	
	private final JButton pauseButton;
	private final JButton rewindButton;
	private final JButton skipButton;
	
	Thread thread;
	
	vlcPlayer(String vlcPath, String mediaURL, String fileName)
	{
		this.mediaPath = mediaURL;
		NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), vlcPath);	
		ourMediaPlayer = new EmbeddedMediaPlayerComponent();
		
		ourFrame.setTitle(fileName);
		ourFrame.setBounds(100,100,600,400);
		ourFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	    ourFrame.addWindowListener(new WindowAdapter(){
	    @Override
	    	public void windowClosing(WindowEvent e)
	    	{
	    		ourMediaPlayer.release();
	    		System.exit(0);
	    		}
	    });
		
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());

        contentPane.add(ourMediaPlayer, BorderLayout.CENTER);
        
        JPanel controlsPane = new JPanel();
        pauseButton = new JButton("Pause");
        controlsPane.add(pauseButton);
        rewindButton = new JButton("Rewind");
        controlsPane.add(rewindButton);
        skipButton = new JButton("Skip");
        controlsPane.add(skipButton);
        contentPane.add(controlsPane, BorderLayout.SOUTH);

        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            	if(ourMediaPlayer.getMediaPlayer().isPlaying())
            	{
            		pauseButton.setText("Pause");
                	red();
            	}
            	else
            	{
            		pauseButton.setText("Play");
            		green();
            	}
            }
        });

        rewindButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	ourMediaPlayer.getMediaPlayer().skip(-10000);
            }
        });

        skipButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	ourMediaPlayer.getMediaPlayer().skip(10000);
            }
        });
        
        contentPane.addKeyListener(new KeyAdapter() {
        	@Override
        	public void keyPressed(KeyEvent f){
        		if (f.getKeyCode() == KeyEvent.VK_ENTER)
				{
        			ourMediaPlayer.getMediaPlayer().pause();
				}
        	}
        });
        
        ourFrame.setContentPane(contentPane);
        ourFrame.setVisible(true);

	}
	
	public void initializer()
	{
		ourMediaPlayer.getMediaPlayer().playMedia(mediaPath);
	}
	
	public void green()
	{
		ourMediaPlayer.getMediaPlayer().play();
	}
	
	public void red()
	{
		ourMediaPlayer.getMediaPlayer().pause();
		
	}
}