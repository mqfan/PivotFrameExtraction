package VideoManipulation;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.junit.Test;

import com.xuggle.xuggler.Global;

public class KeyFrameFinder_Xuggler_Method2 {

	private static ArrayList<BufferedImage> frames;
	private static int frameRate;

    private static final String outputFilePrefix = "c:/Users/fanq3/Desktop/revisedKeyExtraction/";
    
	private static int block_X_DIM;
	private static int block_Y_DIM;
	
	public KeyFrameFinder_Xuggler_Method2(ArrayList<BufferedImage> roll, int fRate)
	{
		frames = roll;
		frameRate = fRate;
		block_X_DIM = frames.get(0).getWidth()/20;
		block_Y_DIM = frames.get(0).getHeight()/20;
	}
	
	public static void main(String[] args)
	{
		System.out.println("frame rate is " + frameRate);
		
		findKeys();
		
		
//		findKeys(frameArray);
	}
	
/*	@Test
	public static ArrayList[][] populateAverages()
	{		
		int block_X_DIM = frames.get(0).getWidth()/20;
		int block_Y_DIM = frames.get(0).getHeight()/20;
		
		ArrayList<float[]>[][] allData = new ArrayList[block_X_DIM][block_Y_DIM];
		
		int xPivot = 0;
		int yPivot = 0;
		
		//going across rows
		for (int x = 0; x < allData.length; x++)
		{
			
			//going down columns
			for (int y = 0; y < allData[x].length; y++)
			{
				////////////////////block level////////////////////////
				xPivot = x*block_X_DIM;
				yPivot = y*block_Y_DIM;
				allData[x][y] = new ArrayList<float[]>();
				
				//going through each frame for corresponding block
				for (int frameLayer = 0; frameLayer < frames.size()-1; frameLayer++)
				{
					
					double redBucket = 0;
					double greenBucket = 0;
					double blueBucket = 0;
					
					///////////////////pixel level/////////////////////////

					System.out.println("layer is " + frameLayer);
					System.out.println("xPivot is " + xPivot);
					System.out.println("yPivot is " + yPivot);
					System.out.println();
					
					for (int xShuffler = xPivot; xShuffler < Math.min(xPivot+block_X_DIM, frames.get(frameLayer).getWidth()); xShuffler++)
					{
						for (int yShuffler = yPivot; yShuffler < Math.min(yPivot+block_Y_DIM, frames.get(frameLayer).getHeight()); yShuffler++)
						{
							redBucket += (new Color(frames.get(frameLayer).getRGB(xShuffler, yShuffler))).getRed();
							greenBucket += (new Color(frames.get(frameLayer).getRGB(xShuffler, yShuffler))).getGreen();
							blueBucket += (new Color(frames.get(frameLayer).getRGB(xShuffler, yShuffler))).getBlue();
							
							System.out.println("redbucket is " + redBucket + "greenbucket is " + greenBucket + "bluebucket is " + blueBucket);
							System.out.println("xShuffler is " + xShuffler + " yShuffler is "+ yShuffler);
						}
					}//end pixel level

					if (xPivot != frames.get(frameLayer).getWidth() && yPivot != frames.get(frameLayer).getHeight())
					{
						double redAvg = redBucket/((Math.min(block_X_DIM, frames.get(0).getWidth())-xPivot)*(Math.min(block_Y_DIM, frames.get(0).getHeight())-yPivot));
						double greenAvg = greenBucket/((Math.min(block_X_DIM, frames.get(0).getWidth())-xPivot)*(Math.min(block_Y_DIM, frames.get(0).getHeight())-yPivot));
						double blueAvg = blueBucket/((Math.min(block_X_DIM, frames.get(0).getWidth())-xPivot)*(Math.min(block_Y_DIM, frames.get(0).getHeight())-yPivot));

						allData[x][y].add(new float[3]);
						Color.RGBtoHSB((int)Math.round(redAvg),(int)Math.round(greenAvg),(int)Math.round(blueAvg),allData[x][y].get(allData[x][y].size()-1));
					}

				}//end of the current frame layer
			}//end of current column
		}//end of current row
		
		for (int x = 0; x < allData.length; x++)
		{
			for (int y = 0; y < allData[x].length; y++)
			{
				int c = 0;
				while (c < allData[x][y].size())
				{
					System.out.println(allData[x][y].get(c)[0] + " " + allData[x][y].get(c)[1] + " " + allData[x][y].get(c)[2]);
					c++;
				}
				
				System.out.println("---------------------");
			}
		}
		
		return allData;
		
	}*/
	
	public static void findKeys()
	{
		ArrayList<float[][]> frameColors = new ArrayList<float[][]>();
		ArrayList<float[][]> avgColors = new ArrayList<float[][]>();
		
		ArrayList<BufferedImage> actualKeys = new ArrayList<BufferedImage>();
		
		for (int layer = 0; layer < frames.size(); layer++)
		{
			frameColors.add(new float[400][3]); //400=frames.get(layer).getWidth()*frames.get(layer).getHeight()/block_X_DIM/block_Y_DIM
			int blockPosition = 0;
			
			for (int xPivot = 0; xPivot < frames.get(layer).getWidth(); xPivot += block_X_DIM)
			{
				for (int yPivot = 0; yPivot < frames.get(layer).getHeight(); yPivot += block_Y_DIM)
				{
					int redBucket = 0;
					int greenBucket = 0;
					int blueBucket = 0;
					
					for (int xShuffler = xPivot; xShuffler < Math.min(xPivot + block_X_DIM, frames.get(layer).getWidth()); xShuffler++)
					{
						for (int yShuffler = yPivot; yShuffler < Math.min(yPivot + block_X_DIM, frames.get(layer).getHeight()); yShuffler++)
						{
							redBucket += (new Color(frames.get(layer).getRGB(xShuffler, yShuffler))).getRed();
							greenBucket += (new Color(frames.get(layer).getRGB(xShuffler, yShuffler))).getGreen();
							blueBucket += (new Color(frames.get(layer).getRGB(xShuffler, yShuffler))).getBlue();
						}
					}
					double redAvg = (double)(redBucket)/(Math.min(block_X_DIM, frames.get(layer).getWidth()-xPivot)*Math.min(block_Y_DIM, frames.get(layer).getHeight()-yPivot));
					double greenAvg = (double)(greenBucket)/(Math.min(block_X_DIM, frames.get(layer).getWidth()-xPivot)*Math.min(block_Y_DIM, frames.get(layer).getHeight()-yPivot));
					double blueAvg = (double)(blueBucket)/(Math.min(block_X_DIM, frames.get(layer).getWidth()-xPivot)*Math.min(block_Y_DIM, frames.get(layer).getHeight()-yPivot));
				
					 if (blockPosition >= frameColors.get(frameColors.size()-1).length)
						 break;
					
					 Color.RGBtoHSB((int)redAvg, (int)greenAvg, (int)blueAvg, frameColors.get(frameColors.size()-1)[blockPosition]);
					 System.out.println(blockPosition);
					 blockPosition++;
				}
			}
		}
		
		for (int layer = 0; layer < frames.size(); layer++)
		{
			avgColors.add(new float[400][3]);

			for (int blockPosition = 0; blockPosition < frameColors.get(layer).length; blockPosition++)
			{
				float hueBucket = 0;
				float satBucket = 0;
				float briBucket = 0;
				
				for (int folder = layer; folder < Math.min(layer+frameRate, frames.size()-1); folder++)
				{
					hueBucket += frameColors.get(avgColors.size()-1)[blockPosition][0];
					satBucket += frameColors.get(avgColors.size()-1)[blockPosition][1];
					briBucket += frameColors.get(avgColors.size()-1)[blockPosition][2];
				}
				
				float avgHue = hueBucket/frameRate;
				float avgSat = satBucket/frameRate;
				float avgBri = briBucket/frameRate;
				
				avgColors.get(layer)[blockPosition][0] = avgHue;
				avgColors.get(layer)[blockPosition][1] = avgSat;
				avgColors.get(layer)[blockPosition][2] = avgBri;
				
/*				System.out.println(avgColors.get(layer)[blockPosition][0] == avgHue);
				System.out.println(avgColors.get(layer)[blockPosition][1] == avgSat);
				System.out.println(avgColors.get(layer)[blockPosition][2] == avgBri);*/
				
				System.out.println(avgColors.get(layer)[blockPosition][0]);
				System.out.println(avgColors.get(layer)[blockPosition][1]);
				System.out.println(avgColors.get(layer)[blockPosition][2]);
				System.out.println("---------------");
				
			}
		}
		
		double acceptableError = 0.05;
		for (int layer = 0; layer < frames.size(); layer++)
		{
			int hits = 0;
			double error = 0;
			for (int blockPosition = 0; blockPosition < frameColors.get(layer).length; blockPosition++)
			{
				if (blockPosition >= frameColors.get(layer).length || blockPosition >= avgColors.get(layer).length)
					break;
				
				error += (frameColors.get(layer)[blockPosition][0]+frameColors.get(layer)[blockPosition][1]+frameColors.get(layer)[blockPosition][2]);
				error -= (avgColors.get(layer)[blockPosition][0] + avgColors.get(layer)[blockPosition][1] + avgColors.get(layer)[blockPosition][2]);
				error = Math.abs(error);
				if (error >= acceptableError)
					hits++;
			}
			
			if (hits >= 1)
				actualKeys.add(frames.get(layer));
			
			System.out.println(layer + " " + (hits>=25));
		}
		
		System.out.println(actualKeys.size());
		for (int x = 0; x < frameColors.get(15).length; x++)
		{
			System.out.println(frameColors.get(15)[x][0]);
			System.out.println(frameColors.get(15)[x][1]);
			System.out.println(frameColors.get(15)[x][2]);
		}
		
		for (int x = 0; x < avgColors.get(15).length; x++)
		{
			System.out.println(frameColors.get(15)[x][0]);
			System.out.println(frameColors.get(15)[x][1]);
			System.out.println(frameColors.get(15)[x][2]);
		}

		
		
		exportFrames(actualKeys);
		
	}
	
/*	public static void findKeys(ArrayList<float[][]> frameColors)
	{
		
		
		
				ArrayList<BufferedImage> actualKeys = new ArrayList<BufferedImage>();
		double acceptableError = 1.5;
		
		int[] flagged = new int[frames.size()];
		
		for (int x = 0; x < allData.length; x++)
		{
			for (int y = 0; y < allData[x].length; y++)
			{				
				for (int layer = 0; layer < allData[x][y].size(); layer++)
				{
					double hueBucket = 0;
					double satBucket = 0;
					double briBucket = 0;
					
					int c = 0;
					for (int folder = Math.max(0, layer-frameRate/2); folder < Math.min(allData[x][y].size()-1, layer+frameRate/2); folder++)
					{
						hueBucket += (double)(allData[x][y].get(folder)[0]);
						satBucket += (double)(allData[x][y].get(folder)[1]);
						briBucket += (double)(allData[x][y].get(folder)[2]);
						
						System.out.println(hueBucket + " " + satBucket + " " + briBucket);
						c++;
					}
					double hueAvg = hueBucket/c;
					double satAvg = satBucket/c;
					double briAvg = briBucket/c;
					
					double hueDiff = Math.abs(allData[x][y].get(layer)[0] - hueAvg);
					double satDiff = Math.abs(allData[x][y].get(layer)[1] - satAvg);
					double briDiff = Math.abs(allData[x][y].get(layer)[2] - briAvg);
					
					double errorDetected = hueDiff + satDiff + briDiff;
					
					System.out.println("error detected:" + errorDetected);
					
					if (errorDetected >= acceptableError)
					{
						System.out.println("layer: " + layer);
						flagged[layer]++;
					}
				}
			}
		}
		
		for (int x = 0; x < flagged.length; x++)
		{
			System.out.println(Integer.toString(flagged[x]));
			if (flagged[x] > 40)
				actualKeys.add(frames.get(x));
		}
		
//		exportFrames(actualKeys);
		
	}*/
	
	public static void exportFrames(ArrayList<BufferedImage> actualKeys)
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
