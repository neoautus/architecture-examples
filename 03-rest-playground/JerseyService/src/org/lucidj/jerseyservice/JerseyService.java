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
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.osgi.service.http.HttpService;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Configurable;
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

    private String get_ApplicationPath_annotation (ResourceConfig resource)
    {
        // We need every resource published with it's @ApplicationPath
        ApplicationPath app_path = resource.getClass ().getAnnotation (ApplicationPath.class);
        return ((app_path == null)? null: app_path.value ());
    }

    private void publish_jersey_resource (ResourceConfig resource)
    {
        String app_path = get_ApplicationPath_annotation (resource);

        if (app_path == null)
        {
            log.log (Level.SEVERE, "Class resource requires @ApplicationPath: " + resource.getClass().getName());
            return;
        }

        // We combine our base path plus @ApplicationPath so we can publish a unique servlet path
        String servlet_path = JERSEY_SERVLET_ALIAS + app_path;

        try
        {
            // === SAFEGUARD ==
            // This does nothing on a unlocked resource, however throws exception when locked.
            // We use it here as a safeguard against locked resources. If we leave it without
            // check, the exception is thrown from _inside_ the Servlet engine and leaves weird
            // side effects.
            resource.addProperties (null);
        }
        catch (IllegalStateException is_readonly)
        {
            // Anyway, you should NOT see this message often
            log.info ("Already published JAX-RS resource: " + servlet_path);
            return;
        }

        try
        {
            // We publish the container using a copy of original resource, keeping
            // it unlocked. This way we can publish it as many times as we want.
            ResourceConfig clone = new ResourceConfig (resource.getClasses ());
            clone.setProperties (resource.getProperties ());
            ServletContainer servlet = new ServletContainer (clone);

            // Each registered container is a servlet with it's own path
            log.info ("Publishing JAX-RS resource: " + servlet_path);
            httpService.registerServlet (servlet_path, servlet, null, null);
        }
        catch (Exception e)
        {
            log.log (Level.SEVERE, "Error publishing JAX-RS resource: " + app_path, e);
        }
    }

    private void remove_jersey_resource (ResourceConfig resource)
    {
        String app_path = get_ApplicationPath_annotation(resource);

        if (app_path != null) // Non-annotated classes surely weren't published by us
        {
            // Unregister the servlet from the HttpService
            String servlet_path = JERSEY_SERVLET_ALIAS + app_path;
            log.info ("Removing JAX-RS resource: " + servlet_path);

            try
            {
                httpService.unregister (servlet_path);
            }
            catch (IllegalArgumentException not_there)
            {
                // The most probable fail is servlet already unregistered due to bundle deactivation
                log.warning ("Unable to unregister servlet: " + not_there.getMessage());
            }
        }
    }

    @Bind (aggregate=true, optional=true, specification = Configurable.class)
    private void bindConfigurable (Configurable config)
    {
        if (config instanceof ResourceConfig)
        {
            publish_jersey_resource ((ResourceConfig)config);
        }
    }

    @Unbind
    private void unbindConfigurable (Configurable config)
    {
        if (config instanceof ResourceConfig)
        {
            remove_jersey_resource ((ResourceConfig)config);
        }
    }

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
