package org.ops4j.pax.web.service.tomcat.internal;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.servlet.Servlet;

import org.ops4j.pax.web.service.spi.Configuration;
import org.ops4j.pax.web.service.spi.LifeCycle;
import org.ops4j.pax.web.service.spi.ServerController;
import org.ops4j.pax.web.service.spi.ServerEvent;
import org.ops4j.pax.web.service.spi.ServerListener;
import org.ops4j.pax.web.service.spi.model.ContainerInitializerModel;
import org.ops4j.pax.web.service.spi.model.ContextModel;
import org.ops4j.pax.web.service.spi.model.ErrorPageModel;
import org.ops4j.pax.web.service.spi.model.EventListenerModel;
import org.ops4j.pax.web.service.spi.model.FilterModel;
import org.ops4j.pax.web.service.spi.model.SecurityConstraintMappingModel;
import org.ops4j.pax.web.service.spi.model.ServletModel;
import org.osgi.service.http.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Romain Gilles
 */
class TomcatServerController implements ServerController {

	private static final Logger LOG = LoggerFactory
			.getLogger(TomcatServerController.class);
	private ServerState serverState;

	private final Set<ServerListener> listeners = newThreadSafeSet();

	private Set<ServerListener> newThreadSafeSet() {
		// return new ConcurrentSkipListSet<ServerListener>();
		return new CopyOnWriteArraySet<ServerListener>();
	}

	private TomcatServerController(ServerState initialState) {
		this.serverState = initialState;
	}

	@Override
	public void start() {
		LOG.debug("start server");
		serverState = serverState.start();
		fireStateChange(ServerEvent.STARTED);
	}

	@Override
	public void stop() {
		LOG.debug("stop server");
		serverState = serverState.stop();
		fireStateChange(ServerEvent.STOPPED);
	}

	@Override
	public boolean isStarted() {
		return serverState.isStarted();
	}

	@Override
	public boolean isConfigured() {
		return serverState.isConfigured();
	}

	@Override
	public void configure(Configuration configuration) {
		LOG.debug("set configuration");
		serverState = serverState.configure(configuration);
		fireStateChange(ServerEvent.CONFIGURED);
		this.start();
	}

	@Override
	public Configuration getConfiguration() {
		return serverState.getConfiguration();
	}

	@Override
	public void addListener(ServerListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(ServerListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void removeContext(HttpContext httpContext) {
		serverState.removeContext(httpContext);
	}

	@Override
	public void addServlet(ServletModel model) {
		serverState.addServlet(model);
	}

	@Override
	public void removeServlet(ServletModel model) {
		serverState.removeServlet(model);
	}

	@Override
	public void addEventListener(EventListenerModel eventListenerModel) {
		serverState.addEventListener(eventListenerModel);
	}

	@Override
	public void removeEventListener(EventListenerModel eventListenerModel) {
		serverState.removeEventListener(eventListenerModel);
	}

	@Override
	public void addFilter(FilterModel filterModel) {
		serverState.addFilter(filterModel);
	}

	@Override
	public void removeFilter(FilterModel filterModel) {
		serverState.removeFilter(filterModel);
	}

	@Override
	public void addErrorPage(ErrorPageModel model) {
		serverState.addErrorPage(model);
	}

	@Override
	public void removeErrorPage(ErrorPageModel model) {
		serverState.removeErrorPage(model);
	}

	@Override
	public LifeCycle getContext(ContextModel model) {
		return serverState.getContext(model);
	}

	@Override
	public Integer getHttpPort() {
		return serverState.getHttpPort();
	}

	@Override
	public Integer getHttpSecurePort() {
		return serverState.getHttpSecurePort();
	}

	@Override
	public Servlet createResourceServlet(ContextModel contextModel,
			String alias, String name) {
		return serverState.createResourceServlet(contextModel, alias, name);
	}

	@Override
	public void addSecurityConstraintMapping(
			SecurityConstraintMappingModel secMapModel) {
		serverState.addSecurityConstraintMapping(secMapModel);
	}

	@Override
	public void addContainerInitializerModel(ContainerInitializerModel model) {
		serverState.addContainerInitializerModel(model);
	}

	private void fireStateChange(ServerEvent event) {
		for (ServerListener listener : listeners) {
			listener.stateChanged(event);
		}
	}

	static ServerController newInstance(ServerState serverState) {
		return new TomcatServerController(serverState);
	}
}
