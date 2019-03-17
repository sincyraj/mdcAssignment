package org.vaadin.spring.mdc.service;

import java.util.List;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import org.vaadin.spring.mdc.model.User;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Service
public class UserServiceImpl implements UserService {


	Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@Override
	public List<User> getAllUsers() {


		RestTemplate restTemplate = new RestTemplate();
		try {
			ResponseEntity<List<User>> response = restTemplate.exchange(
					"https://jsonplaceholder.typicode.com/todos",
					HttpMethod.GET,
					null,
					new ParameterizedTypeReference<List<User>>() {
					});
			List<User> users = response.getBody();

		/*for (User user : users) {
			logger.info(user.toString());
		}*/
			return users;
		}catch (HttpStatusCodeException e) {
			System.out.println(" Response Status Code "+e.getStatusCode().value());
			throw e;
		}

	}
	
	
}


