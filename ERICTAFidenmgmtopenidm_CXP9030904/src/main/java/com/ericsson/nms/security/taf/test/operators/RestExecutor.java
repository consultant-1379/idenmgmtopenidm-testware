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
package com.ericsson.nms.security.taf.test.operators;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.tools.http.*;
import com.ericsson.cifwk.taf.tools.http.constants.ContentType;
import com.ericsson.nms.host.HostConfigurator;
import com.ericsson.nms.launcher.LauncherOperator;
import com.ericsson.nms.security.taf.test.helpers.OperatorParams;

public abstract class RestExecutor {

	protected static Logger logger = Logger.getLogger(RestExecutor.class);

	protected HttpTool httpTool;

	@Inject
	public LauncherOperator launcherOperator;

	public RestExecutor() {
	}

	protected HttpResponse executeGetRestCall(final String uri,
			final String userName) {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("X-Usernames", userName);
		return executeGetRestCall(uri, headers);
	}

	protected HttpResponse executeGetRestCall(final String uri) {
		return executeGetRestCall(uri, new HashMap<String, String>());
	}

	protected HttpResponse executeGetRestCall(final String uri,
			final Map<String, String> headers) {
		final RequestBuilder request = createRequest();
		setHeaders(request, headers);

		final HttpResponse response = request.get(uri);
		return response;
	}

	protected HttpResponse executePostRestCall(final String uri,
			final String jsonString) {
		return executePostRestCall(uri, jsonString,
				new HashMap<String, String>());
	}

	protected HttpResponse executePostRestCall(final String uri,
			final String jsonString, final Map<String, String> headers) {
		final RequestBuilder request = createRequest();
		setContentType(request);
		setHeaders(request, headers);

		final HttpResponse response = request.body(jsonString).post(uri);
		return response;
	}

	protected HttpResponse executeDeleteRestCall(final String uri) {
		return executeDeleteRestCall(uri, new HashMap<String, String>());
	}

	protected HttpResponse executeDeleteRestCall(final String uri,
			final Map<String, String> headers) {
		logger.info("DeleteRestCall URI : " + getHttpTool().getBaseUri() + uri);

		final RequestBuilder request = createRequest();
		setContentType(request);
		request.header("If-Match", "\"*\"");
		setHeaders(request, headers);

		final HttpResponse response = request.delete(uri);
		return response;
	}

	protected HttpResponse executePutRestCall(final String uri,
			final String jsonString) {
		return executePutRestCall(uri, jsonString,
				new HashMap<String, String>());
	}

	protected HttpResponse executePutRestCall(final String uri,
			final String jsonString, final Map<String, String> headers) {
		final RequestBuilder request = createRequest();
		setContentType(request);
		setHeaders(request, headers);

		final HttpResponse response = request.body(jsonString).put(uri);
		return response;
	}

	protected HttpTool getHttpTool() {
		if (httpTool != null) {
			return httpTool;
		} else {
			Host host = HostConfigurator.getApache();
			return HttpToolBuilder.newBuilder(host).useHttpsIfProvided(true)
					.trustSslCertificates(true).followRedirect(false).build();
		}
	}

	private void setContentType(final RequestBuilder request) {
		request.contentType(ContentType.APPLICATION_JSON);
	}

	private void setCommonRequestOptions(final RequestBuilder request) {
		request.header("Accept", "application/json")
				.header("X-Requested-With", "XMLHttpRequest")
				.timeout(OperatorParams.TIMEOUT);
	}

	private RequestBuilder createRequest() {
		RequestBuilder request = getHttpTool().request();
		setCommonRequestOptions(request);
		return request;
	}

	private void setHeaders(final RequestBuilder builder,
			final Map<String, String> headers) {
		if (headers != null) {
			for (Map.Entry<String, String> header : headers.entrySet()) {
				builder.header(header.getKey(), header.getValue());
			}
		}
	}

}