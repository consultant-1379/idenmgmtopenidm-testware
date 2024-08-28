package com.ericsson.nms.security.taf.test.utils;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.ericsson.nms.security.ENMUser;
import com.ericsson.nms.security.taf.test.model.ENMUserDTO;

public class JSONtoUserMapper {

	private static Logger logger = Logger.getLogger(JSONtoUserMapper.class);
	private static final String USER_ENABLED = "enabled";

	public static final ENMUser mapStringToUser(String stringifiedUser) {
		logger.debug("Attempting to map user in JSON to EnmUser");

		ENMUser user = new ENMUser();
		ENMUserDTO userDTO = mapJSONtoDTO(stringifiedUser);
		mapToENMUser(user, userDTO);

		logger.debug("Mapped user is: " + user.toString());
		return user;
	}

	private static final ENMUserDTO mapJSONtoDTO(String stringifiedUser) {
		logger.debug("Mapping JSON to UserDTO");
		logger.debug("JSON string: " + stringifiedUser);

		ObjectMapper mapper = new ObjectMapper();

		ENMUserDTO userDTO = null;
		try {
			userDTO = mapper.readValue(stringifiedUser, ENMUserDTO.class);
			logger.debug("DTO user is: " + userDTO.toString());
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return userDTO;
	}

	private static final void mapToENMUser(ENMUser user, ENMUserDTO userDTO) {
		logger.debug("Simple mapping of DTO to ENMUser");

		user.setUsername(userDTO.getUserName());
		user.setFirstName(userDTO.getFirstName());
		user.setLastName(userDTO.getLastName());
		user.setEmail(userDTO.getEmail());
		user.setPassword(userDTO.getPassword());

		if (userDTO.getStatus().equalsIgnoreCase(USER_ENABLED)) {
			user.setEnabled(true);
		} else {
			user.setEnabled(false);
		}
	}
}
