package alice.tuprolog.json.test;

import java.net.*;

import alice.tuprolog.exceptions.InvalidTheoryException;
import alice.tuprolog.exceptions.MalformedGoalException;
import alice.tuprolog.exceptions.NoMoreSolutionException;
import alice.tuprolog.Prolog;
import alice.tuprolog.Theory;

import java.io.*;

//Alberto
public class ClientTest {

	public static void main(String[] args) throws InvalidTheoryException {
		String serverName = "127.0.0.1";
		int port = 40000;
		Prolog prolog = new Prolog();
		String query = "member(A, [1,2,3,4,5,6,7,8,9]).";
		System.out.println("CLIENT: ?- "+query);
		try {
			prolog.addTheory(new Theory(":-flag(ciao, [on], on, true)."));
			System.out.println(prolog.solve("flag_list(L).").toString());
			System.out.println(prolog.solve(query).toString());
			System.out.println(prolog.solveNext().toString());
		} catch (MalformedGoalException | NoMoreSolutionException e1) {
			e1.printStackTrace();
		}
		try {
			System.out.println("\nConnecting to " + serverName + " on port " + port);
			Socket client = new Socket(serverName, port);
      
			System.out.println("\nJust connected to " + client.getRemoteSocketAddress());
			OutputStream outToServer = client.getOutputStream();
			DataOutputStream out = new DataOutputStream(outToServer);
      
			System.out.println(prolog.toJSON(Prolog.INCLUDE_KB_IN_SERIALIZATION));
			out.writeUTF(prolog.toJSON(Prolog.INCLUDE_KB_IN_SERIALIZATION));
			InputStream inFromServer = client.getInputStream();
			DataInputStream in = new DataInputStream(inFromServer);
      
			System.out.println("\nServer says next solution is:\n \n" + in.readUTF());
			System.out.println("\nServer says next solution is:\n \n" + in.readUTF());
			client.close();
		} catch(IOException e) {
			e.printStackTrace();
   		}
	}
}