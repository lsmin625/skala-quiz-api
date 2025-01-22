package com.sk.skala.quizapi.tools;

import java.util.Date;

import com.sk.skala.quizapi.config.Constant;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

//@Slf4j
public class JwtTool {

	public static String generateToken(String id, Object payload, String secret) {
		long currentTimeMillis = System.currentTimeMillis();
//		log.debug("JwtTool.generateToken: {} {}", id, payload.toString());
		return Jwts.builder().setIssuer(Constant.JWT_ISSUER).setId(id).setSubject(Constant.JWT_SUBJECT)
				.setIssuedAt(new Date(currentTimeMillis))
				.setExpiration(new Date(currentTimeMillis + Constant.JWT_TTL_MILLIS))
				.setAudience(JsonTool.toString(payload)).signWith(Keys.hmacShaKeyFor(secret.getBytes())).compact();

	}

	public static String getValidPayload(String token, String secret) {
		Claims claims = Jwts.parserBuilder().setSigningKey(secret.getBytes()).build().parseClaimsJws(token).getBody();

//		log.debug("JwtTool.getValidPayload: {} {}", claims.getIssuer(), claims.getAudience());
		return claims.getAudience();
	}
}
