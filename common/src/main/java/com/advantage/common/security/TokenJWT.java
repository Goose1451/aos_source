package com.advantage.common.security;


import com.advantage.common.enums.AccountType;
import com.advantage.common.exceptions.token.*;
import io.jsonwebtoken.*;

import java.util.Map;

/**
 * Created by Evgeney Fiskin on 02-01-2016.
 */
public class TokenJWT extends Token {

    private Claims tokenClaims;
    private Header tokenHeader;
    private JwtBuilder builder;
    private JwtParser parser;
    //private CompressionCodec compressionCodec;
    private SignatureAlgorithm signatureAlgorithm;

    private TokenJWT() {
        super();
        //compressionCodec = SecurityTools.getCompressionCodec();
        convertSignatureAlgorithm();
    }

    public TokenJWT(long appUserId, String loginName, AccountType accountType) {
        this();
        builder = Jwts.builder();
        tokenHeader = Jwts.header();
        tokenHeader.setType(Header.JWT_TYPE);
        tokenClaims = Jwts.claims();
        tokenClaims.setIssuer(issuer);
        //tokenClaims.setIssuedAt(new Date());
        tokenClaims.put(USER_ID_FIELD_NAME, appUserId);
        if (loginName != null && !loginName.isEmpty()) {
            tokenClaims.setSubject(loginName);
        }
        tokenClaims.put(ROLE_FIELD_NAME, accountType);
//        if (email != null && !email.isEmpty()) {
//            tokenClaims.put("email", email);
//        }
        builder.setHeader((Map<String, Object>) tokenHeader);
        builder.setClaims(tokenClaims);
    }

    public TokenJWT(String base64Token) throws VerificationTokenException, WrongTokenTypeException, ContentTokenException {
        this();
        try {
            parser = Jwts.parser();
            if (!parser.isSigned(base64Token)) {
                throw new TokenUnsignedException("Token is unsigned");
            }
            parser.setSigningKey(key);
            parser.requireIssuer(issuer);
            Jws<Claims> claimsJws = parser.parseClaimsJws(base64Token);
            tokenClaims = claimsJws.getBody();
            JwsHeader jwsHeader = claimsJws.getHeader();

            if (!jwsHeader.getType().equals(Header.JWT_TYPE)) {
                throw new WrongTokenTypeException("Wrong token type");
            }
            if (!jwsHeader.getAlgorithm().equals(signatureAlgorithm.name())) {
                throw new SignatureAlgorithmException(String.format("The token signed by %s algorithm, but must be signed with %s (%s)", jwsHeader.getAlgorithm(), signatureAlgorithm.name(), signatureAlgorithmJdkName));
            }

        } catch (ClaimJwtException | RequiredTypeException e) {
            throw new VerificationTokenException(e.getMessage());
        } catch (SignatureException e) {
            throw new SignatureAlgorithmException(e.getMessage());
        } catch (MalformedJwtException | CompressionException | UnsupportedJwtException e) {
            throw new ContentTokenException(e.getMessage());
        }
    }

    @Override
    public AccountType getAccountType() {
        String role = (String) tokenClaims.get(ROLE_FIELD_NAME);
        AccountType result = AccountType.valueOf(role);
        return result;
    }

    @Override
    public long getUserId() throws ContentTokenException {
        long result = 0;
        Object o = "null";
        ;
        try {
            o = tokenClaims.get(USER_ID_FIELD_NAME);
            if (o == null) {
                throw new ContentTokenException("The token must contains " + USER_ID_FIELD_NAME + " field");
            }
            Number userId = (Number) o;
            result = userId.longValue();
        } catch (ClassCastException | NumberFormatException e) {
            throw new ContentTokenException("User id have wrong number: " + o.toString());
        }
        return result;
    }

//    @Override
//    public String getEmail() {
//        return (String) tokenClaims.get("email");
//    }

    @Override
    public String getLoginName() {
        return (String) tokenClaims.getSubject();
    }

    @Override
    public String generateToken() {
        builder.signWith(signatureAlgorithm, key);
//        if (compressionCodec != null) {
//            builder.compressWith(compressionCodec);
//        }
        String result = builder.compact();
        return result;
    }

    @Override
    public Map<String, Object> getClaims() {
        return tokenClaims;
    }

    private void convertSignatureAlgorithm() {
        for (SignatureAlgorithm sa : SignatureAlgorithm.values()) {
            String saname = (sa.getJcaName() == null) ? "" : sa.getJcaName();
            if (saname.equalsIgnoreCase(signatureAlgorithmJdkName)) {
                if (!sa.isJdkStandard()) {
                    throw new SignatureException("io.jsonwebtoken: Unsupported signature algorithm:" + signatureAlgorithmJdkName);
                } else {
                    signatureAlgorithm = sa;
                    return;
                }
            }
        }
        throw new SignatureException("io.jsonwebtoken: Unknown signature algorithm:" + signatureAlgorithmJdkName);
    }

}