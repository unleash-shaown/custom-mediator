package com.bits.esb;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;

public class HashGenerator extends AbstractMediator {
	private static final Log LOGGER = LogFactory.getLog(HashGenerator.class);
	private static final String HASH_ALGO = "MD5";
	private static final String STRING_TO_BE_HASHED = "INPUT_TEXT";
	private static final String HASHED_VALUE = "HASHED_VALUE";


	public boolean mediate(MessageContext context) { 
		// TODO Implement your mediation logic here
		String inputText = (String) context.getProperty(STRING_TO_BE_HASHED);
		try {
			String hashedValue = String.format("%032x", new BigInteger(1,
					MessageDigest.getInstance(HASH_ALGO).digest(inputText.getBytes(StandardCharsets.UTF_8))));
			context.setProperty(HASHED_VALUE, hashedValue);
		} catch (NoSuchAlgorithmException e) {
			LOGGER.fatal(e);
		}
		return true;
	}
}
