package com.sk.skala.quizapi.tools;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class SecureTool {
	private static final String STORED_KEY = "967baf77a4f8d5065566f4";
	private static final Pattern BASE64_PATTERN = Pattern.compile("^[a-zA-Z0-9+/]*={0,2}$");
	private static final int SALT_LENGTH = 16;

	private static byte[] generateRandomBytes(int length) {
		byte[] randomBytes = new byte[length];
		new SecureRandom().nextBytes(randomBytes);
		return randomBytes;
	}

	private static byte[] generateSalt() {
		return generateRandomBytes(SALT_LENGTH);
	}

	private static byte[] generateKey(String key, byte[] salt) {
		byte[] decodedKey = Base64.getDecoder().decode(key);
		byte[] result = new byte[decodedKey.length + salt.length];
		System.arraycopy(decodedKey, 0, result, 0, decodedKey.length);
		System.arraycopy(salt, 0, result, decodedKey.length, salt.length);
		return result;
	}

	private static byte[] addSaltAndIVToEncryptedData(byte[] salt, byte[] initVector, byte[] encryptedData) {
		byte[] result = new byte[salt.length + initVector.length + encryptedData.length];
		System.arraycopy(salt, 0, result, 0, salt.length);
		System.arraycopy(initVector, 0, result, salt.length, initVector.length);
		System.arraycopy(encryptedData, 0, result, salt.length + initVector.length, encryptedData.length);
		return result;
	}

	private static byte[] extractSaltFromEncryptedData(byte[] data) {
		byte[] salt = new byte[SALT_LENGTH];
		System.arraycopy(data, 0, salt, 0, salt.length);
		return salt;
	}

	private static byte[] extractIVFromEncryptedData(byte[] data) {
		byte[] initVector = new byte[16];
		System.arraycopy(data, SALT_LENGTH, initVector, 0, initVector.length);
		return initVector;
	}

	private static byte[] removeSaltAndIVFromEncryptedData(byte[] data) {
		int saltLength = SALT_LENGTH;
		int ivLength = 16;
		byte[] result = new byte[data.length - saltLength - ivLength];
		System.arraycopy(data, saltLength + ivLength, result, 0, result.length);
		return result;
	}

	public static String encryptAes(String value, String key) throws Exception {
		byte[] salt = generateSalt();
		byte[] initVector = generateRandomBytes(16); // 16 bytes for AES
		IvParameterSpec iv = new IvParameterSpec(initVector);
		SecretKeySpec skeySpec = new SecretKeySpec(generateKey(key, salt), "AES");

		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

		byte[] encrypted = cipher.doFinal(value.getBytes());
		return Base64.getEncoder().encodeToString(addSaltAndIVToEncryptedData(salt, initVector, encrypted));
	}

	public static String decryptAes(String encrypted, String key) throws Exception {
		byte[] decodedEncrypted = Base64.getDecoder().decode(encrypted);
		byte[] salt = extractSaltFromEncryptedData(decodedEncrypted);
		byte[] initVector = extractIVFromEncryptedData(decodedEncrypted);

		IvParameterSpec iv = new IvParameterSpec(initVector);
		SecretKeySpec skeySpec = new SecretKeySpec(generateKey(key, salt), "AES");

		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
		cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

		byte[] original = cipher.doFinal(removeSaltAndIVFromEncryptedData(decodedEncrypted));

		return new String(original);
	}

	public static String generateKey() {
		return generateKey(16);
	}

	public static String generateKey(int length) {
		SecureRandom secureRandom = new SecureRandom();
		byte[] key = new byte[length];
		secureRandom.nextBytes(key);
		return Base64.getEncoder().encodeToString(key);
	}

	public static String encryptAes(String value) throws Exception {
		return encryptAes(value, STORED_KEY);
	}

	public static String decryptAes(String value) throws Exception {
		return decryptAes(value, STORED_KEY);
	}

	public static String encodeBase64(String msg) {
		try {
			byte[] bytes = Base64.getEncoder().encode(msg.getBytes());
			return new String(bytes);
		} catch (Exception e) {
			System.err.println(e.toString());
		}
		return new String();
	}

	public static String decodeBase64(String msg) {
		try {
			byte[] bytes = Base64.getDecoder().decode(msg.getBytes());
			return new String(bytes);
		} catch (Exception e) {
			System.err.println(e.toString());
		}
		return new String();
	}

	public static boolean isBase64Encoded(String text) {
		if (text == null || text.isEmpty() || (text.length() % 4 != 0) || !BASE64_PATTERN.matcher(text).matches()) {
			return false;
		}

		try {
			byte[] decodedBytes = Base64.getDecoder().decode(text);
			for (byte b : decodedBytes) {
				if ((b < 32 || b > 127) && b != '\t' && b != '\n' && b != '\r') {
					return false;
				}
			}
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}
}
