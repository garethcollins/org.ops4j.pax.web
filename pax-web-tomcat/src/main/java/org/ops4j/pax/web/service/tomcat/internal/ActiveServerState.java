/*
 * Copyright 2012 Romain Gilles
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.ops4j.pax.web.service.tomcat.internal;

import org.ops4j.pax.web.service.spi.Configuration;
import org.ops4j.pax.web.service.spi.model.ErrorPageModel;
import org.ops4j.pax.web.service.spi.model.ServletModel;
import org.osgi.service.http.HttpContext;

import static org.ops4j.pax.web.service.tomcat.internal.ServerState.States.ACTIVE;

/**
 * @author Romaim Gilles
 */
class ActiveServerState extends AbstractServerState implements ServerState
{


    private final ServerState initializedState;
    private final ServerWrapper serverWrapper;

    ActiveServerState(ServerStateFactory serverStateFactory, ServerState initializedState, ServerWrapper serverWrapper)
    {
        super( serverStateFactory );
        this.initializedState = initializedState;
        this.serverWrapper = serverWrapper;
    }

    static ServerState getInstance(ServerStateFactory serverStateFactory, ServerState initializedState, ServerWrapper server)
    {
        return new ActiveServerState( serverStateFactory, initializedState, server );
    }

    @Override
    public ServerState start()
    {
        return throwIllegalState();
    }

    @Override
    public ServerState stop()
    {
        serverWrapper.stop();
        return initializedState;
    }

    @Override
    public boolean isStarted()
    {
        return true;
    }

    @Override
    public boolean isConfigured()
    {
        return true;
    }

    @Override
    public ServerState configure(Configuration configuration)
    {
        return stop().configure( configuration ).start();
    }

    @Override
    public States getState()
    {
        return ACTIVE;
    }

    @Override
    public Configuration getConfiguration()
    {
        return initializedState.getConfiguration();
    }

    @Override
    public void addServlet(ServletModel model)
    {
        serverWrapper.addServlet( model );
    }

    @Override
    public void removeServlet(ServletModel model)
    {
        serverWrapper.removeServlet( model );
    }

    @Override
    public void removeContext(HttpContext httpContext)
    {
        serverWrapper.removeContext( httpContext );
    }

    @Override
    public void addErrorPage(ErrorPageModel model)
    {
        serverWrapper.addErrorPage( model );
    }

    @Override
    public void removeErrorPage(ErrorPageModel model)
    {
        serverWrapper.removeErrorPage( model );
    }
}