package VideoManipulation;

import static org.junit.Assert.assertTrue;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.xuggle.xuggler.Global;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IError;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;

public class KeyFrameFinder_Xuggler_Default {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private String INPUT_FILE = "";

	private ArrayList<Long> offsets = new ArrayList<Long>();
	private ArrayList<Long> timestamps = new ArrayList<Long>();

	public KeyFrameFinder_Xuggler_Default(String mediaPath) {
		INPUT_FILE = mediaPath;
	}

	public ArrayList<Long> getTimeStamps() {
		return timestamps;
	}

	@Test
	public void testSeekKeyFrameCheckIndex() throws IOException {


		Global.setFFmpegLoggingLevel(52);
		IContainer container = IContainer.make();

		int retval = -1;

		final ICodec.ID VID_CODEC = ICodec.ID.CODEC_ID_H264;

		retval = container.open(INPUT_FILE, IContainer.Type.READ, null);
		assertTrue("could not open file", retval >= 0);

		// First, let's get all the key frames

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
					"could not find video stream in container");

		// now, loop through the entire file and record the index of EACH
		// video key frame

		final IPacket packet = IPacket.make();
		offsets = new ArrayList<Long>();

		long numPackets = 0;
		int numKeys = 0;
		int numVidPackets = 0;
		while (container.readNextPacket(packet) >= 0) {
			System.out.println(numPackets);
			if (packet.isComplete()) {
				System.out.println("complete");
				if (packet.getStreamIndex() != videoStreamId) {
					System.out.println("wrong stream index");
					numPackets++;
					continue;
				}
				numVidPackets++;
				if (!packet.isKey()) {
					System.out.println("not key " + packet.getPosition());
					numPackets++;
					continue;
				}
				if (numPackets < 5) {
					log.debug("First Packet: {}", packet);
				}
				System.out.println("IS KEY");
				assertTrue(packet.getPosition() >= 0);
				offsets.add(packet.getPosition());
				timestamps.add(packet.getDts());
				System.out.println(offsets);
				System.out.println(timestamps);
				numKeys++;
			} else
				System.out.println("not complete");
			numPackets++;
		}
		/*
		 * log.debug("Num Index Entries; container: {}; test: {}",
		 * videoStreamId.getNumIndexEntries(), offsets.size());
		 */

		// move the seek head backwards
		retval = container.seekKeyFrame(-1, Long.MIN_VALUE, 0, Long.MAX_VALUE,
				IContainer.SEEK_FLAG_BACKWARDS);
		assertTrue("got negative retval: " + IError.errorNumberToType(retval),
				retval >= 0);

		System.out.println("here");

		// now let's walk through that index and ensure we can seek to each key
		// frame.
		for (int i = 0; i < offsets.size(); i++) {
			long index = offsets.get(i);
			retval = container.seekKeyFrame(videoStreamId, videoStreamId,
					videoStreamId, index, IContainer.SEEK_FLAG_BYTE);
			assertTrue(
					"got negative retval: " + IError.errorNumberToType(retval),
					retval >= 0);
			retval = container.readNextPacket(packet);
			log.debug("{}", packet);
			assertTrue(
					"got negative retval: " + IError.errorNumberToType(retval),
					retval >= 0);
			assertTrue(packet.isComplete());
			if (!packet.isKey()) {
				offsets.remove(i);
				i--;
				System.out.println("also not key");
			}
		}

		System.out.println("number of video packets: " + numVidPackets);
		System.out.println("number of key frames: " + numKeys);
		System.out.println("number of frames in container:"
				+ container.getStream(videoStreamId).getNumFrames());
		container.close();
		// System.out.println("timestamps: " + timestamps);
		// System.out.println("offsets: " + offsets);
	}

}
