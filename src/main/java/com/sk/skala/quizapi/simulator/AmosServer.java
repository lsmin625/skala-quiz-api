package com.sk.skala.quizapi.simulator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.sk.skala.quizapi.tools.StringTool;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

//@Service
@Slf4j
public class AmosServer {

	private static final String PROMPT = "> ";
	private static final int PORT = 1023;
	private static final String EXIT = "exit";
	private static final Charset CHARSET;
	static {
		CHARSET = System.getProperty("os.name").toLowerCase().contains("win") ? Charset.forName("MS949")
				: StandardCharsets.UTF_8;
	}

	private final Map<String, String> users = new HashMap<>();
	private final Map<String, AmosResponse> commands = new HashMap<>();

	public AmosServer() {
		setupUsers();
		setupCommands();
	}

	private void setupUsers() {
		users.put("admin", "admin@1234");
		users.put("ems", "ems@1234");
		users.put("skt_son", "Sktson@123");
	}

	private void setupCommands() {
		commands.put("st cell", new AmosResponseStCell());
		commands.put("lt all", new AmosResponseLtAll());
	}

	@PostConstruct
	public void start() {
		new Thread(() -> {
			try (ServerSocket serverSocket = new ServerSocket(PORT)) {
				while (true) {
					Socket clientSocket = serverSocket.accept();
					handleClient(clientSocket);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}

	private void handleClient(Socket clientSocket) {
		new Thread(() -> {
			try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), CHARSET));
					PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), CHARSET),
							true)) {

				if (!authenticateUser(in, out)) {
					out.println("Authentication failed. Closing connection.");
					clientSocket.close();
					return;
				}

				out.println(AmosConstant.WELCOME_BANNER);

				String command;
				while ((command = in.readLine()) != null) {
					if (StringTool.isEmpty(command)) {
						out.print(PROMPT);
						out.flush();
						continue;
					}
					String cmd = command.trim().replaceAll("\\s+", " ").toLowerCase();
					if (EXIT.equals(cmd)) {
						out.println("Goodbye!");
						break;
					}
					AmosResponse response = commands.get(cmd);
					if (response != null) {
						out.println(response.printResponse());
						out.print(PROMPT);
						out.flush();
					} else {
						out.println(AmosConstant.UNKNOWN_COMMAND);
						out.print(PROMPT);
						out.flush();
					}
				}
			} catch (Exception e) {
				log.error("Error in TelnetServer", e.getMessage());
			}
		}).start();
	}

	private boolean authenticateUser(BufferedReader in, PrintWriter out) throws Exception {
		out.println("Login:");
		out.flush();
		out.print("Username: ");
		out.flush();
		String userName = in.readLine();

		out.print("Password: ");
		out.flush();
		StringBuilder password = new StringBuilder();
		while (true) {
			int ch = in.read();
			if (ch == '\n' || ch == '\r') {
				break;
			} else {
				password.append((char) ch);
				out.print("\rPassword: *\rPassword: ");
				out.flush();
			}
		}

		return users.containsKey(userName) && users.get(userName).equals(password.toString());
	}
}
