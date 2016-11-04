package alice.tuprolog;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

public class Datagram_Socket extends AbstractSocket {
	
	private static final long serialVersionUID = 1L;

	private DatagramSocket socket;

	public Datagram_Socket(DatagramSocket socket) {
		super();
		this.socket = socket;
	}

	@Override
	public boolean isClientSocket() {
		return false;
	}

	@Override
	public boolean isServerSocket() {
		return false;
	}

	@Override
	public boolean isDatagramSocket() {
		return true;
	}

	@Override
	public DatagramSocket getSocket() {
		return socket;
	}

	@Override
	public InetAddress getAddress() {
		if(socket.isBound())return socket.getInetAddress();
		else return null;
	}

	@Override
	boolean unify(List<Var> varsUnifiedArg1, List<Var> varsUnifiedArg2, Term t) {
		t = t.getTerm();
        if (t instanceof Var) {
            return t.unify(varsUnifiedArg1, varsUnifiedArg2, this);
        } else if (t instanceof AbstractSocket && ((AbstractSocket) t).isDatagramSocket()) {
        	InetAddress addr= ((AbstractSocket) t).getAddress();
            return socket.getInetAddress().toString().equals(addr.toString());
        } else {
            return false;
        }
	}
	
	@Override
	public String toString(){
		return socket.toString();
	}
}