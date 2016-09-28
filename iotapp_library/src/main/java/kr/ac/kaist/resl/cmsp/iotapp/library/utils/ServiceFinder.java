package kr.ac.kaist.resl.cmsp.iotapp.library.utils;

import kr.ac.kaist.resl.cmsp.iotapp.library.service.general.ThingService;

/**
 * Adopted from http://stackoverflow.com/questions/13215403/finding-a-class-reflectively-by-its-simple-name-alone
 */
public class ServiceFinder {
    public static final String[] searchPackages = {
            "kr.ac.kaist.resl.cmsp.iotapp.library.service.general",
            "kr.ac.kaist.resl.cmsp.iotapp.library.service.extension"
    };
    public static Class<? extends ThingService> findService(String simpleName) throws ClassNotFoundException {
        for(int i=0; i<searchPackages.length; i++){
            try{
                return (Class<? extends ThingService>) Class.forName(searchPackages[i] + "." + simpleName);
            } catch (ClassNotFoundException e){
                //not in this package, try another
            }
        }
        throw new ClassNotFoundException("ServiceFinder could not find the service " + simpleName);
    }
}
