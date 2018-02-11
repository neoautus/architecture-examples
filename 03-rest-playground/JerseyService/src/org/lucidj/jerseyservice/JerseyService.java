/*
 * Copyright 2018 NEOautus Ltd. (http://neoautus.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.lucidj.jerseyservice;

import org.apache.felix.ipojo.annotations.*;
import org.glassfish.jersey.servlet.ServletContainer;
import org.osgi.service.http.HttpService;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component (immediate = true, publicFactory = false)
@Instantiate
public class JerseyService
{
    private final static Logger log = Logger.getLogger (JerseyService.class.getName());

    private final static String JERSEY_SERVLET_ALIAS = "/services";
    private ServletContainer jersey_servlet_container;

    @Requires
    private HttpService httpService;

    @Validate
    private void validate ()
    {
        try
        {
            log.info ("Starting Jersey Service on " + JERSEY_SERVLET_ALIAS);
            Dictionary<String, String> init_params = new Hashtable<>();
            init_params.put ("jersey.config.server.provider.packages", "org.lucidj.jerseyservice");
            jersey_servlet_container = new ServletContainer ();
            httpService.registerServlet (JERSEY_SERVLET_ALIAS, jersey_servlet_container, init_params, null);
        }
        catch (Exception e)
        {
            log.log (Level.SEVERE, "Registering Jersey servlet failed", e);
        }
    }

    @Invalidate
    private void invalidate ()
    {
        log.info ("Stopping Jersey Service on " + JERSEY_SERVLET_ALIAS);
        httpService.unregister (JERSEY_SERVLET_ALIAS);
    }
}

// EOF
