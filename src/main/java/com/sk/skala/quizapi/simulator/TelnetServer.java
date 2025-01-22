package com.sk.skala.quizapi.simulator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.sk.skala.quizapi.tools.StringTool;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TelnetServer {

	private final String PROMPT = "> ";
	private final int PORT = 1023;
	private final String EXIT = "exit";
	private static final Charset CHARSET;
	static {
		CHARSET = System.getProperty("os.name").toLowerCase().contains("win") ? Charset.forName("MS949")
				: StandardCharsets.UTF_8;
	}

	private final Map<String, String> users = new HashMap<>();
	private final Map<String, AmosResponse> commands = new HashMap<>();

	private final String WELCOME_BANNER = "Welcome\r\n";

	public TelnetServer() {
		users.put("skt_son", "Sktson@123");
		users.put("ems", "ems@1234");
		users.put("admin", "admin@1234");

		commands.put("st cell", new AmosResponseStCell());
	}

	@PostConstruct
	public void start() {
		new Thread(() -> {
			try (ServerSocket serverSocket = new ServerSocket(PORT)) {
				log.debug("TelnetServer started on port {} ", PORT);
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

				out.println(WELCOME_BANNER);
				out.print(PROMPT);
				out.flush();

				String command;
				while ((command = in.readLine()) != null) {
					if (StringTool.isEmpty(command)) {
						log.info("handleClient: empty command");
						out.print(PROMPT);
						out.flush();
						continue;
					}
					if (EXIT.equals(command.toLowerCase())) {
						out.println("Goodbye!");
						break;
					}
					log.info("handleClient: {}", command.toLowerCase());
					AmosResponse response = commands.get(command.toLowerCase());
					if (response != null) {
						out.println(response.printResponse());
						out.print(PROMPT);
						out.flush();
					} else {
						processCommand(command, out);
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

	private void processCommand(String command, PrintWriter out) {
		log.debug("processCommand: start {}", command);
		ProcessBuilder processBuilder = new ProcessBuilder();
		BufferedReader bufferedReader = null;
		StringBuffer buff = new StringBuffer();

		try {
			if (System.getProperty("os.name").toLowerCase().contains("win")) {
				processBuilder.command("cmd.exe", "/c", command);
			} else {
				processBuilder.command("bash", "-c", command);
			}

			Process process = processBuilder.start();
			bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), CHARSET));

			String line;
			while ((line = bufferedReader.readLine()) != null) {
				buff.append(line).append(System.lineSeparator());
			}

			BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), CHARSET));
			while ((line = errorReader.readLine()) != null) {
				buff.append(line).append(System.lineSeparator());
			}

			int exitCode = process.waitFor();
			log.debug("Command exited with code {}", exitCode);
		} catch (Exception e) {
			log.error("CommandRunner.run: error {}", e.getMessage());
			buff.append("Error executing command: ").append(e.getMessage()).append(System.lineSeparator());
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			log.debug("processCommand: end {}", command);
			out.println(buff.toString());
			out.print(PROMPT);
			out.flush();
		}
	}
}
