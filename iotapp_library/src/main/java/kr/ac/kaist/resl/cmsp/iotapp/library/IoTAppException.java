package kr.ac.kaist.resl.cmsp.iotapp.library;

public class IoTAppException extends Exception {
	private static final long serialVersionUID = -7326435601438002491L;

	public IoTAppException(String msg) {
		super(msg);
	}
	public IoTAppException(Throwable ex) {
		super(ex);
	}
	public IoTAppException(String msg, Throwable ex) {
		super(msg, ex);
	}
}
