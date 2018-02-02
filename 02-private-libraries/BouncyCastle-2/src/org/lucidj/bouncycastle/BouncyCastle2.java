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

package org.lucidj.bouncycastle;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.lucidj.securitylib.SecurityLib;

@Component (immediate = true, publicFactory = false)
@Instantiate
@Provides
public class BouncyCastle2 implements SecurityLib
{
    private BouncyCastleProvider bcprovider;

    public BouncyCastle2 ()
    {
        bcprovider = new BouncyCastleProvider ();
    }

    @Override
    public String getInfo()
    {
        return (bcprovider.getInfo ());
    }

    @Override
    public double getVersion ()
    {
        return (bcprovider.getVersion ());
    }
}

// EOF
