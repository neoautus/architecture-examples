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
import org.lucidj.client10.Client10;
import org.lucidj.client20.Client20;

import java.util.logging.Logger;

@Component (immediate = true, publicFactory = false)
@Instantiate
public class Main
{
    private final static Logger log = Logger.getLogger (Main.class.getName());

    @Requires
    private Client10 client10;

    @Requires
    private Client20 client20;

    @Validate
    private void validate ()
    {
        // You'll get logged into stage/data/log/karaf.log the message below:
        // VALIDATE! client10='Client 1.0 gets 'Hello from SimpleLib 1.0' from SimpleLib' client20='Client 2.0 gets 'Hello from SimpleLib 2.0' from SimpleLib'
        //
        // Even thou Client10 and Client20 references the same class SimpleLib,
        // each one get wired into the library bundle with the correct version.
        //
        log.info ("VALIDATE! client10='" + client10.getHelloFromLib() +
                          "' client20='" + client20.getHelloFromLib() + "'");
    }

    @Invalidate
    private void invalidate ()
    {
        log.info ("Bye bye!");
    }
}

// EOF
