package co.edu.unbosque.parcialfinal.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import co.edu.unbosque.parcialfinal.entity.Usuario;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

	private static final long JWT_TOKEN_VALIDITY = 12 * 60 * 60 * 1000;

	@Value("${jwt.secret}")
	private String secret;

	private SecretKey getSigningKey() {

		byte[] keyBytes = secret.getBytes(java.nio.charset.StandardCharsets.UTF_8);

		if (keyBytes.length < 32) {
			throw new IllegalArgumentException("La clave JWT debe tener mínimo 32 bytes.");
		}

		return Keys.hmacShaKeyFor(keyBytes);
	}

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	public String extractRole(String token) {
		return extractClaim(token, claims -> claims.get("role", String.class));
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {

		Claims claims = extractAllClaims(token);

		return claimsResolver.apply(claims);
	}

	private Claims extractAllClaims(String token) {

		return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
	}

	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	public String generateToken(UserDetails userDetails) {

		Map<String, Object> claims = new HashMap<>();

		if (userDetails instanceof Usuario) {

			Usuario usuario = (Usuario) userDetails;

			claims.put("role", usuario.getRole().name());
		}

		return createToken(claims, userDetails.getUsername());
	}

	private String createToken(Map<String, Object> claims, String subject) {

		Date fechaActual = new Date();

		Date fechaExpiracion = new Date(fechaActual.getTime() + JWT_TOKEN_VALIDITY);

		return Jwts.builder().claims(claims).subject(subject).issuedAt(fechaActual).expiration(fechaExpiracion)
				.signWith(getSigningKey()).compact();
	}

	public Boolean validateToken(String token, UserDetails userDetails) {

		String username = extractUsername(token);

		return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
	}
}