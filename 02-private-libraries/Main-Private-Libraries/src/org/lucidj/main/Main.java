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

package org.lucidj.main;

import org.apache.felix.ipojo.annotations.*;
import org.lucidj.securitylib.SecurityLib;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Component (immediate = true, publicFactory = false)
@Instantiate
public class Main
{
    private final static Logger log = Logger.getLogger ("private-libraries");

    private List<SecurityLib> security_libs = new ArrayList<>();

    private void log_current_libs ()
    {
        int size = security_libs.size ();

        log.info ("Currently we have " + size + " registered security libraries");

        for (int i = 0; i < size; i++)
        {
            SecurityLib service = security_libs.get (i);
            log.info ("Library #" + i + ": " + service + " info=" + service.getInfo ());
        }
    }

    @Bind (aggregate=true, optional=true, specification = SecurityLib.class)
    private void bindSecurityLib (SecurityLib security_service)
    {
        log.info ("bindSecurityLib: Adding " + security_service);

        synchronized (security_libs)
        {
            security_libs.add (security_service);
            log_current_libs ();
        }
    }

    @Unbind
    private void unbindSecurityLib (SecurityLib security_service)
    {
        log.info ("unbindSecurityLib: Removing " + security_service);

        synchronized (security_libs)
        {
            security_libs.remove (security_service);
            log_current_libs ();
        }
    }

    @Validate
    private void validate ()
    {
        log.info ("Validate: Hello!");
    }

    @Invalidate
    private void invalidate ()
    {
        log.info ("Invalidate: Bye bye!");
    }
}

// EOF
