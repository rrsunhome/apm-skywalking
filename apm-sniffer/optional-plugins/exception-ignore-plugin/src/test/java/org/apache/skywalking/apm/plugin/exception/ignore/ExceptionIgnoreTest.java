/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.skywalking.apm.plugin.exception.ignore;

import org.apache.skywalking.apm.agent.core.boot.ServiceManager;
import org.apache.skywalking.apm.agent.core.context.ContextManagerExtendService;
import org.apache.skywalking.apm.agent.test.tools.AgentServiceRule;
import org.apache.skywalking.apm.plugin.exception.ignore.conf.IgnoreExceptionConfig;
import org.apache.skywalking.apm.util.ConfigInitializer;
import org.apache.skywalking.apm.util.PropertyPlaceholderHelper;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ExceptionIgnoreTest {

    @Rule
    public final EnvironmentVariables environmentVariables = new EnvironmentVariables().set("SW_AGENT_EXCEPTION_IGNORE_BUSINESS_EXCEPTION_PACKAGES", "java.lang.RuntimeException");

    @Rule
    public AgentServiceRule serviceRule = new AgentServiceRule();

    @Test
    public void testServiceOverrideFromPlugin() {
        ContextManagerExtendService service = ServiceManager.INSTANCE.findService(ExceptionIgnoreExtendService.class);
        Assert.assertEquals(ExceptionIgnoreExtendService.class, service.getClass());
    }


    @Test
    public void testExceptionIgnoreConfigOverridingFromSystemEnv() throws IllegalAccessException {
        Properties properties = new Properties();
        properties.put("exception.ignore_business_exception_packages", "${SW_AGENT_EXCEPTION_IGNORE_BUSINESS_EXCEPTION_PACKAGES:java.lang.RuntimeException}");
        properties.put("exception.ignore_business_exception_packages", PropertyPlaceholderHelper.INSTANCE.replacePlaceholders((String)properties.get("exception.ignore_business_exception_packages"), properties));
        ConfigInitializer.initialize(properties, IgnoreExceptionConfig.class);

        //Assert.assertEquals(IgnoreExceptionConfig.Exception.IGNORE_BUSINESS_EXCEPTION_PACKAGES, "java.lang.RuntimeException");

        assertThat(IgnoreExceptionConfig.Exception.IGNORE_BUSINESS_EXCEPTION_PACKAGES, is("java.lang.RuntimeException"));
    }
}
