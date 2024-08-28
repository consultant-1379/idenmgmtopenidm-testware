/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.taf.test.helpers;

import java.io.IOException;
import java.math.BigInteger;
import org.apache.commons.io.IOUtils;

public class PasswordDecoder {

	private static final String path = "data/password/";

	public static String getPassword() throws IOException {
		ClassLoader classLoader = PasswordDecoder.class.getClassLoader();

		byte[] encodedPassword = IOUtils.toByteArray(classLoader
				.getResourceAsStream(path + "passwordEncoded"));
		BigInteger parameterN = new BigInteger(IOUtils.toByteArray(classLoader
				.getResourceAsStream(path + "passwordParamN")));
		BigInteger parameterD = new BigInteger(IOUtils.toByteArray(classLoader
				.getResourceAsStream(path + "passwordParamD")));

		return new String((new BigInteger(encodedPassword)).modPow(parameterD,
				parameterN).toByteArray());
	}
}
