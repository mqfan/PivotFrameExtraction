package VideoManipulation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.junit.Test;

import com.xuggle.xuggler.Global;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IRational;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IVideoResampler;
import com.xuggle.xuggler.Utils;

public class FrameExtractor {
    private static final String inputFilename = "c:/Users/fanq3/Desktop/DRose.mp4";
    private static final String outputFilePrefix = "c:/Users/fanq3/Desktop/AllFrames/";
    
    private static long timestamp;
    
    private final static int seconds = 0;
    
    public FrameExtractor(long key)
    {
    	timestamp = key;
    }
    
	private static void processFrame(IVideoPicture picture, BufferedImage image) {
		try {
           
			String outputFilename = outputFilePrefix + 
                    System.currentTimeMillis() + ".png";
               ImageIO.write(image, "png", new File(outputFilename));

			// indicate file written
			double seconds = ((double) picture.getPts())
					/ Global.DEFAULT_PTS_PER_SECOND;
			System.out.printf("at elapsed time of %6.3f seconds wrote: %s\n",
					seconds, new File(outputFilename));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	public static void start() throws NumberFormatException, IOException
	{

		// make sure that we can actually convert video pixel formats
		if (!IVideoResampler
				.isSupported(IVideoResampler.Feature.FEATURE_COLORSPACECONVERSION))
			throw new RuntimeException();

		// create a Xuggler container object
		IContainer container = IContainer.make();

		// open up the container
		if (container.open(inputFilename, IContainer.Type.READ, null) < 0)
			throw new IllegalArgumentException("could not open file: "
					+ inputFilename);

		// query how many streams the call to open found
		int numStreams = container.getNumStreams();

		// and iterate through the streams to find the first video stream
		int videoStreamId = -1;
		IStreamCoder videoCoder = null;
		for (int i = 0; i < numStreams; i++) {
			// find the stream object
			IStream stream = container.getStream(i);
			// get the pre-configured decoder that can decode this stream;
			IStreamCoder coder = stream.getStreamCoder();

			if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
				videoStreamId = i;
				videoCoder = coder;
				break;
			}
		}

		if (videoStreamId == -1)
			throw new RuntimeException(
					"could not find video stream in container: " + inputFilename);
		
		if (videoCoder.open() < 0)
			throw new RuntimeException(
					"could not open video decoder for container: " + inputFilename);

		IVideoResampler resampler = null;
		if (videoCoder.getPixelType() != IPixelFormat.Type.BGR24) {
			// if this stream is not in BGR24, we're going to need to
			// convert it. The VideoResampler does that for us.

			resampler = IVideoResampler.make(videoCoder.getWidth(),
					videoCoder.getHeight(), IPixelFormat.Type.BGR24,
					videoCoder.getWidth(), videoCoder.getHeight(),
					videoCoder.getPixelType());
			if (resampler == null)
				throw new RuntimeException(
						"could not create color space resampler for: "
								+ inputFilename);
		}

		// We create a new packet.
		IPacket packet = IPacket.make();

		// Let's Check the timeBase of this container
		IRational timeBase = container.getStream(videoStreamId).getTimeBase();

		// With the stream timebase we can calculate the timestamp
		System.out.println("Timebase " + timeBase.toString());
		
		// Calculate the timeStamp offset
		long timeStampOffset = (timeBase.getDenominator() / timeBase
				.getNumerator()) * (seconds);
		System.out.println("TimeStampOffset " + timeStampOffset);
		
		// we go directly to our target timestamp + startTime
		long target = container.getStartTime() + timeStampOffset + timestamp;

		// Let's seek up to the target key frame
		container.seekKeyFrame(videoStreamId, target, 0);

		boolean isFinished = false;

		while (container.readNextPacket(packet) >= 0 && !isFinished) {
			System.out.println("here");
			// Now we have a packet, let's see if it belongs to our video stream
			if (packet.getStreamIndex() == videoStreamId) {
				// We allocate a new picture to get the data out of Xuggle

				IVideoPicture picture = IVideoPicture.make(
						videoCoder.getPixelType(), videoCoder.getWidth(),
						videoCoder.getHeight());

				int offset = 0;
				while (offset < packet.getSize()) {
					// Now, we decode the video, checking for any errors.

					int bytesDecoded = videoCoder.decodeVideo(picture, packet,
							offset);
					if (bytesDecoded < 0) {
						System.err.println("WARNING!!! got no data decoding "
								+ "video in one packet");
					}
					offset += bytesDecoded;

					// Some decoders will consume data in a packet, but will not
					// be able to construct a full video picture yet. Therefore
					// you should always check if you got a complete picture
					// from
					// the decode.

					if (picture.isComplete()) {

						IVideoPicture newPic = picture;

						// If the resampler is not null, it means we didn't get
						// the
						// video in BGR24 format and need to convert it into
						// BGR24
						// format.

						if (resampler != null) {
							// we must resample
							newPic = IVideoPicture.make(
									resampler.getOutputPixelFormat(),
									picture.getWidth(), picture.getHeight());
							if (resampler.resample(newPic, picture) < 0)
								throw new RuntimeException(
										"could not resample video from: "
												+ inputFilename);
						}

						if (newPic.getPixelType() != IPixelFormat.Type.BGR24)
							throw new RuntimeException(
									"could not decode video as BGR 24 bit data in: "
											+ inputFilename);

						// convert the BGR24 to an Java buffered image
						BufferedImage javaImage = Utils
								.videoPictureToImage(newPic);

						// process the video frame
						processFrame(newPic, javaImage);
						isFinished = true;
				}
			}
		}

	}
		// clean up

		if (videoCoder != null) {
			videoCoder.close();
			videoCoder = null;
		}
		if (container != null) {
			container.close();
			container = null;
		}
	}
	   
}
