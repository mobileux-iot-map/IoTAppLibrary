// ILocalServiceObjectHandler.aidl
package kr.ac.kaist.resl.cmsp.iotapp.library.impl;

interface ILocalServiceObjectHandler {
    String callback(String invocation, boolean isReturnable);
}
