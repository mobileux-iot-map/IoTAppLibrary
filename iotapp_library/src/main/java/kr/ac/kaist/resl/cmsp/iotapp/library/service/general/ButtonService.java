package kr.ac.kaist.resl.cmsp.iotapp.library.service.general;

/**
 * Created by shheo on 15. 8. 23.
 */
public interface ButtonService extends ThingService {
    int getButtonCount();
    boolean getButtonState(int index);
}
