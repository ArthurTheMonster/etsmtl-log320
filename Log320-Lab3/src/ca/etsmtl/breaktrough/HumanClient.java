package ca.etsmtl.breaktrough;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;


//http://www.codeproject.com/KB/game/breakthrough.aspx
public class HumanClient {

	public static void main(String[] args) {
		Socket clientSocket;
		BufferedInputStream input;
		BufferedOutputStream output;
		try {
			clientSocket = new Socket("localhost", 8888);
			input = new BufferedInputStream(clientSocket.getInputStream());
			output = new BufferedOutputStream(clientSocket.getOutputStream());
			BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
			while (true) {
				char cmd = (char) input.read();

				// Le serveur demande le prochain coup
				// Le message contient aussi le dernier coup joué.
				if (cmd == '1') {
					byte[] aBuffer = new byte[16];

					int size = input.available();

					input.read(aBuffer, 0, size);

					String s = new String(aBuffer);
					System.out.println("Dernier coup : " + s);
					System.out.println("Entrez votre coup : ");
					String move = null;
					move = console.readLine();
					output.write(move.getBytes(), 0, move.length());
					output.flush();
				}
				// Le dernier coup est invalide
				if (cmd == '2') {
					System.out.println("Coup invalide, entrez un nouveau coup : ");
					String move = null;
					move = console.readLine();
					output.write(move.getBytes(), 0, move.length());
					output.flush();
				}
			}
		} catch (IOException e) {
			System.out.println(e);
		}
	}
}
