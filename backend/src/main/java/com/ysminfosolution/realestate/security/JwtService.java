package com.ysminfosolution.realestate.security;

import java.text.ParseException;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

@Service
public class JwtService {

    @Value("${security.jwt.secret}")
    private String secret;

    @Value("${security.jwt.access-ttl-seconds:900}") // 15 min
    private long accessTtl;

    @Value("${security.jwt.refresh-ttl-seconds:1209600}") // 14 days
    private long refreshTtl;

    private MACSigner signer() throws KeyLengthException {
        return new MACSigner(Base64.getDecoder().decode(secret));
    }

    private MACVerifier verifier() throws JOSEException {
        return new MACVerifier(Base64.getDecoder().decode(secret));
    }

    // ---------------- Public API ----------------

    public String generateAccessToken(AppUserDetails user) {
        return signToken(user, accessTtl, "access");
    }

    public String generateRefreshToken(AppUserDetails user) {
        return signToken(user, refreshTtl, "refresh");
    }

    public JWTClaimsSet validate(String token) {
        try {
            SignedJWT jwt = SignedJWT.parse(token);
            if (!jwt.verify(verifier())) {
                throw new BadCredentialsException("Invalid signature");
            }
            Date exp = jwt.getJWTClaimsSet().getExpirationTime();
            if (exp.before(new Date())) {
                throw new BadCredentialsException("Token expired");
            }
            return jwt.getJWTClaimsSet();
        } catch (ParseException | JOSEException e) {
            throw new BadCredentialsException("Invalid token", e);
        }
    }

    // ---------------- Internal ----------------

    private String signToken(AppUserDetails user, long ttlSeconds, String typ) {
        try {
            Instant now = Instant.now();

            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .issuer("realestate-app")
                    .issueTime(Date.from(now))
                    .expirationTime(Date.from(now.plusSeconds(ttlSeconds)))
                    .claim("userId", user.getUserId())
                    .claim("orgId", user.getOrgId())
                    .jwtID(typ)
                    .build();

            JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.HS256)
                    .type(new JOSEObjectType("JWT"))
                    .build();

            SignedJWT jwt = new SignedJWT(header, claims);
            jwt.sign(signer());
            return jwt.serialize();
        } catch (Exception e) {
            throw new IllegalStateException("Could not sign JWT", e);
        }
    }
}
