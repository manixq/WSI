package Main;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
@PreMatching
public class CustomHeaders implements ContainerResponseFilter {
	@Override
	public void filter(ContainerRequestContext containerRequestContext, ContainerResponseContext containerResponseContext) throws IOException {
		containerResponseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
		containerResponseContext.getHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
		containerResponseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");
		containerResponseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
		containerResponseContext.getHeaders().add("Access-Control-Expose-Headers", "Location");

		containerRequestContext.getHeaders().add("Access-Control-Request-Headers", "origin, content-type, accept, authorization");
	}



}
