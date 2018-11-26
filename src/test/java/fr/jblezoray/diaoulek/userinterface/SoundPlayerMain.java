package fr.jblezoray.diaoulek.userinterface;

import fr.jblezoray.diaoulek.data.model.lessonelement.qrcouple.SoundReference;
import fr.jblezoray.diaoulek.data.scrapper.FileCache;
import fr.jblezoray.diaoulek.data.scrapper.FileDownloader;
import fr.jblezoray.diaoulek.data.scrapper.FileRetrieverException;
import fr.jblezoray.diaoulek.entrypoint.Config;

public class SoundPlayerMain {


    // for development purpose.
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
            while (!soundPlayer.isDone());
        }
    }

}
