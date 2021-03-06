/*
 * Copyright 2000-2009 JetBrains s.r.o.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.intellij.spring.osgi.model;

import com.intellij.javaee.StandardResourceProvider;
import com.intellij.javaee.ResourceRegistrar;
import com.intellij.spring.osgi.constants.SpringOsgiConstants;

/**
 * @author Dmitry Avdeev
 */
public class SpringOsgiResourceProvider implements StandardResourceProvider {
  public void registerResources(ResourceRegistrar registrar) {
    registrar.addStdResource(SpringOsgiConstants.OSGI_1_1_SCHEMA, "/resources/schemas/spring-osgi-1.1.xsd", getClass());
    registrar.addStdResource(SpringOsgiConstants.OSGI_COMPENDIUM_1_1_SCHEMA, "/resources/schemas/spring-osgi-compendium-1.1.xsd", getClass());
  }
}
