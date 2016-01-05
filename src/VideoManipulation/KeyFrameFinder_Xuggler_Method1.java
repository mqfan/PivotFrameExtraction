package VideoManipulation;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.junit.Test;

import com.xuggle.xuggler.Global;

public class KeyFrameFinder_Xuggler_Method1 {

	private static ArrayList<BufferedImage> frames;

    private static final String outputFilePrefix = "c:/Users/fanq3/Desktop/KeyExtractionCustom/";
	
	public KeyFrameFinder_Xuggler_Method1(ArrayList<BufferedImage> roll)
	{
		frames = roll;
	}
	
	@Test
	public void findKeys()
	{
		int numFrames = frames.size();
		ArrayList<Color> colors = new ArrayList<Color>();
		ArrayList<float[]> hsb1 = new ArrayList<float[]>();
		ArrayList<float[]> hsb2 = new ArrayList<float[]>();
		
		ArrayList<float[]> differences = new ArrayList<float[]>();
		
		int wInc = frames.get(0).getWidth()/20;
		int hInc = frames.get(0).getHeight()/20;
		
		long redBucket = 0;
		long greenBucket = 0;
		long blueBucket = 0;
		
		int redAvg = 0;
		int greenAvg = 0;
		int blueAvg = 0;
		
		double hueDiffBucket = 0;
		double satDiffBucket = 0;
		double briDiffBucket = 0;
		
		double hueDiffAvg = 0;
		double satDiffAvg = 0;
		double briDiffAvg = 0;
		
		for (int x = 0; x < numFrames-1; x++)
		{
			wInc = frames.get(x).getWidth()/20;
			hInc = frames.get(x).getHeight()/20;

			hueDiffBucket = 0;
			satDiffBucket = 0;
			briDiffBucket = 0;
			
			hueDiffAvg = 0;
			satDiffAvg = 0;
			briDiffAvg = 0;
			
			//block level
			for (int w = 0; w < frames.get(x).getWidth(); w+=wInc)
			{
				for (int h = 0; h < frames.get(x).getHeight(); h+=hInc)
				{

					//pixel level
					for (int smallW = w; smallW < Math.min(w + wInc, frames.get(x).getWidth()); smallW++)
					{
						for (int smallH = h; smallH < Math.min(h + hInc, frames.get(x).getHeight()); smallH++)
						{
							redBucket += (new Color(frames.get(x).getRGB(smallW, smallH))).getRed();
							greenBucket += (new Color(frames.get(x).getRGB(smallW, smallH))).getGreen();
							blueBucket += (new Color(frames.get(x).getRGB(smallW, smallH))).getBlue();
						}
					}//end of pixel level
					
					int totalPixelsInBlock = (Math.min(h + hInc, frames.get(x).getHeight())-h)*(Math.min(w + wInc, frames.get(x).getWidth())-w);
					
					redAvg = (int) (redBucket/totalPixelsInBlock);
					greenAvg = (int) (greenBucket/totalPixelsInBlock);
					blueAvg = (int) (blueBucket/totalPixelsInBlock);
					
					redBucket = 0;
					greenBucket = 0;
					blueBucket = 0;
					
					colors.add(new Color(redAvg, greenAvg, blueAvg));
					hsb1.add(new float[3]);
					Color colorTemp = colors.get(colors.size()-1);
					colorTemp.RGBtoHSB(colorTemp.getRed(),colorTemp.getGreen(),colorTemp.getBlue(),hsb1.get(hsb1.size()-1));
					
//					System.out.println(hsb1.get(hsb1.size()-1)[0] + " " + hsb1.get(hsb1.size()-1)[1] + " " + hsb1.get(hsb1.size()-1)[2]);
					
					if (hsb2.size() > 0)
					{
						hueDiffBucket += Math.abs(hsb1.get(hsb1.size()-1)[0] - hsb2.get(hsb1.size()-1)[0]);
						satDiffBucket += Math.abs(hsb1.get(hsb1.size()-1)[1] - hsb2.get(hsb1.size()-1)[1]);
						briDiffBucket += Math.abs(hsb1.get(hsb1.size()-1)[2] - hsb2.get(hsb1.size()-1)[2]);
					}
					
					System.out.println(hueDiffBucket + " " + satDiffBucket + " " + briDiffBucket + " ");
				}
			}//end block level

			if (hsb2.size() > 0) {
//				System.out.println(hueDiffBucket + " " + satDiffBucket + " " + briDiffBucket + " ");

				hueDiffAvg = hueDiffBucket / (frames.get(x).getHeight() / hInc * frames.get(x).getWidth() / wInc);
				satDiffAvg = satDiffBucket / (frames.get(x).getHeight() / hInc * frames.get(x).getWidth() / wInc);
				briDiffAvg = briDiffBucket / (frames.get(x).getHeight() / hInc * frames.get(x).getWidth() / wInc);
				differences.add(new float[] { (float) hueDiffAvg, (float) satDiffAvg, (float) briDiffAvg });
			}
			
			System.out.println(hsb1.size() + " " + hsb2.size());
			hsb2 = hsb1;
			hsb1 = new ArrayList<float[]>();
		}
		
		double errorBound = 0.6;
		double errorDetected = 0;
		
		ArrayList<BufferedImage> actualKeys = new ArrayList<BufferedImage>();
		
		for (int x = 0; x < differences.size(); x++)
		{
			errorDetected += (differences.get(x)[0] + differences.get(x)[1] +differences.get(x)[2]);
			if (errorDetected >= errorBound)
				actualKeys.add(frames.get(x));
			errorDetected = 0;
		}
		
		System.out.println("Listed HSB Difference in between frames");
		for (int x = 0; x < differences.size(); x++)
			System.out.println("Index: " + x + " " + differences.get(x)[0] + " " +differences.get(x)[1] + " " +differences.get(x)[2]);
	
		System.out.println("Number of Key Frames: " + actualKeys.size());
		
		exportFrames(actualKeys);
		
	}
	
	public void exportFrames(ArrayList<BufferedImage> actualKeys)
	{
		for (int x = 0; x < actualKeys.size(); x++) {
			try {

				String outputFilename = outputFilePrefix
						+ x + ".png";
				ImageIO.write(actualKeys.get(x), "png", new File(outputFilename));

/*				// indicate file written
				double seconds = ((double) picture.getPts())
						/ Global.DEFAULT_PTS_PER_SECOND;
				System.out.printf(
						"at elapsed time of %6.3f seconds wrote: %s\n",
						seconds, new File(outputFilename));
*/
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}
