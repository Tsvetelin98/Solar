/* ==================================================================
 * JsonObjectMatcher.java - 23/08/2017 1:22:36 PM
 * 
 * Copyright 2017 SolarNetwork.net Dev Team
 * 
 * This program is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU General Public License as 
 * published by the Free Software Foundation; either version 2 of 
 * the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License 
 * along with this program; if not, write to the Free Software 
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 
 * 02111-1307 USA
 * ==================================================================
 */

package net.solarnetwork.central.user.billing.killbill.test;

import static org.springframework.test.util.AssertionErrors.assertEquals;
import java.util.Map;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.test.web.client.RequestMatcher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.solarnetwork.util.JsonUtils;

/**
 * Request matchers for JSON body content.
 * 
 * @author matt
 * @version 1.0
 */
public final class JsonObjectMatchers {

	private final ObjectMapper objectMapper;

	/**
	 * Get a matchers instance using the default ObjectMapper.
	 * 
	 * @return the new instance
	 */
	public static JsonObjectMatchers json() {
		return new JsonObjectMatchers();
	}

	/**
	 * Get a matchers instance using a specific ObjectMapper.
	 * 
	 * @param objectMapper
	 *        the mapper to use
	 * @return the new instance
	 */
	public static JsonObjectMatchers json(ObjectMapper objectMapper) {
		return new JsonObjectMatchers(objectMapper);
	}

	/**
	 * Default constructor.
	 */
	public JsonObjectMatchers() {
		this(Jackson2ObjectMapperBuilder.json().build());
	}

	/**
	 * Construct with an ObjectMapper.
	 * 
	 * @param mapper
	 *        the mapper to use
	 */
	public JsonObjectMatchers(ObjectMapper mapper) {
		super();
		this.objectMapper = mapper;
	}

	/**
	 * Get a matcher for the request body as a JSON object.
	 * 
	 * @param obj
	 *        the object to match; will be treated as a Map with string keys, or
	 *        converted to that
	 * @return the matcher
	 */
	@SuppressWarnings("unchecked")
	public RequestMatcher bodyObject(final Object obj) {
		final Map<String, Object> objMap;
		if ( obj instanceof Map ) {
			objMap = (Map<String, Object>) obj;
		} else {
			try {
				objMap = JsonUtils.getStringMap(objectMapper.writeValueAsString(obj));
			} catch ( JsonProcessingException e ) {
				throw new RuntimeException(e);
			}
		}
		return new RequestMatcher() {

			@Override
			public void match(ClientHttpRequest request) {
				String body = ((MockClientHttpRequest) request).getBodyAsString();
				Map<String, Object> json = JsonUtils.getStringMap(body);
				assertEquals("JSON body object", objMap, json);
			}
		};
	}

}
