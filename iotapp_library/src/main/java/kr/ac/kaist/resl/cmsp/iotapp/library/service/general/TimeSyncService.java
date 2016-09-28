package kr.ac.kaist.resl.cmsp.iotapp.library.service.general;

/**
 * Created by shheo on 16. 4. 1.
 */
public interface TimeSyncService extends ThingService {
    long getCurrentUTCTimestamp();
}
