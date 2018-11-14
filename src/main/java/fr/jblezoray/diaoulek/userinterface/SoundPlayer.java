package fr.jblezoray.diaoulek.userinterface;


import fr.jblezoray.diaoulek.data.model.lessonelement.qrcouple.SoundReference;
import fr.jblezoray.diaoulek.data.scrapper.FileCache;
import fr.jblezoray.diaoulek.data.scrapper.FileDownloader;
import fr.jblezoray.diaoulek.data.scrapper.FileRetrieverException;
import fr.jblezoray.diaoulek.entrypoint.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.*;
import javax.sound.sampled.DataLine.Info;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED;

/**
 * see https://odoepner.wordpress.com/2013/07/19/play-mp3-or-ogg-using-javax-sound-sampled-mp3spi-vorbisspi/
 */
public class SoundPlayer {
    private static final Logger LOGGER = LoggerFactory.getLogger(SoundPlayer.class);

    private final FileCache cache;
    private Thread playerThread = null;

    // for testing purpose.
    public static void main(String[] args) throws FileRetrieverException {
        FileDownloader fd = new FileDownloader("");
        FileCache cache = new FileCache(Config.CACHE_DIR, fd);
        SoundPlayer soundPlayer = new SoundPlayer(cache);
        int[][] indexes = new int[][]{
//                {9216,   445952},
//                {454656, 846848},
                {13312,  80896},
                {82432,  147968},
                {150528, 261632},
                {263680, 316416},
                {325120, 447488},
        };
        for (int[] index : indexes) {
            SoundReference sr = new SoundReference();
            sr.setSoundFileName("ee-1.ogg");
            sr.setSoundBeginIndex(index[0]);
            sr.setSoundEndIndex(index[1]);
            System.out.println(sr.toString());
            soundPlayer.playSound(sr);
        }
    }

    public SoundPlayer(FileCache cache) {
        this.cache = cache;
    }

    public void playSound(SoundReference snd)
            throws FileRetrieverException {
        byte[] bytes = this.cache.getFileContent(
                snd.getSoundFileName(), Optional.of("SOUND"));
        if (!isDone()) this.stop();
        this.playerThread = new Thread(() -> {
            playSound(bytes, snd.getSoundBeginIndex(), snd.getSoundEndIndex());
        });
        this.playerThread.start();
    }

    public boolean isDone() {
        return this.playerThread!=null
                && !this.playerThread.isAlive()
                && !this.playerThread.isInterrupted();
    }

    public void stop() {
        if (this.playerThread!=null) {
            this.playerThread.interrupt();
        }
    }

    private void playSound(byte[] bytes, int beginIndex, int endIndex) {
        try (
                ByteArrayInputStream bais  = new ByteArrayInputStream(bytes);
                AudioInputStream in = AudioSystem.getAudioInputStream(bais);
        ) {

            AudioFormat inFormat = in.getFormat();
            int ch = inFormat.getChannels();
            float rate = inFormat.getSampleRate();

            AudioFormat outFormat = new AudioFormat(PCM_SIGNED, rate,
                    16, ch, ch * 2, rate, false);

            Info info = new Info(SourceDataLine.class, outFormat);
            try (
                    SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info)
            ) {
                AudioInputStream audioIs = AudioSystem.getAudioInputStream(outFormat, in);
                if (line != null) {
                    line.open(outFormat);
                    line.start();
                    fadeInMasterGain(line, 75);// avoids white noises at the begin.
                    try {
                        stream(audioIs, line, beginIndex, endIndex);
                        line.drain();
                    } catch (InterruptedException e) {
                        line.flush();
                    }
                    line.stop();
                }
            }
        } catch (LineUnavailableException|UnsupportedAudioFileException|IOException e) {
            e.printStackTrace();
        }
    }


    private void fadeInMasterGain(SourceDataLine line, long fadeInTime) {
        FloatControl gainControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
        final float minVol = gainControl.getMinimum();
        gainControl.setValue(minVol);
        // gainControl.shift(..) doesn't work properly with the default
        // implementation.  Therefore, here is a straightforward implementation:
        AtomicBoolean started = new AtomicBoolean(false);
        Thread thread = new Thread(() -> {
            started.set(true);
            float value;
            final long sleepTime = 10; // ms
            long elapsedTime = 0; // ms
            do {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {}
                elapsedTime+=sleepTime;
                value = (float) (minVol * Math.cos((Math.PI/2)*(elapsedTime/fadeInTime)));
                gainControl.setValue(Math.min(value, gainControl.getMaximum()));
            } while(value <= 0.0);
        });
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
        // wait for the thread to start
        while (!started.get()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {}
        }
    }


    private void stream(AudioInputStream in,
                        SourceDataLine line,
                        int beginIndex,
                        int endIndex)
            throws IOException, InterruptedException {
        int frameSize = line.getFormat().getFrameSize();
        // move to beginIndex.
        in.skip(beginIndex * frameSize);
        int bytesToReadLeft = (endIndex - beginIndex) * frameSize;
        byte[] buffer = new byte[1024];
        while (bytesToReadLeft>0) {
            int bytesToRead = Math.min(bytesToReadLeft, buffer.length);
            int bytesRead = in.read(buffer, 0, bytesToRead);
            bytesToReadLeft -= bytesRead;
            if (bytesRead>0) {
                bytesRead -= bytesRead % frameSize;
                line.write(buffer, 0, bytesRead);
            }
            while (line.available()<1024*2) {
                Thread.sleep(50);
            }
            if (this.playerThread.isInterrupted()) {
                throw new InterruptedException();
            }
        }
    }


}
