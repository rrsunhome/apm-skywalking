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

import org.apache.skywalking.apm.agent.core.boot.AgentPackageNotFoundException;
import org.apache.skywalking.apm.agent.core.boot.DefaultImplementor;
import org.apache.skywalking.apm.agent.core.conf.ConfigNotFoundException;
import org.apache.skywalking.apm.agent.core.context.ContextManagerExtendService;
import org.apache.skywalking.apm.agent.core.logging.api.ILog;
import org.apache.skywalking.apm.agent.core.logging.api.LogManager;
import org.apache.skywalking.apm.plugin.exception.ignore.conf.IgnoreExceptionConfigInitializer;


/**
 * qijia.wang
 */
@DefaultImplementor
public class ExceptionIgnoreExtendService extends ContextManagerExtendService {

    private static final ILog LOGGER = LogManager.getLogger(ExceptionIgnoreExtendService.class);

    @Override
    public void boot() {
        try {
            LOGGER.info("exception ignore config loaded");
            System.out.println("exception ignore config loaded");
            IgnoreExceptionConfigInitializer.initialize();
        } catch (ConfigNotFoundException e) {
            LOGGER.error("exception ignore config init error", e);
        } catch (AgentPackageNotFoundException e) {
            LOGGER.error("exception ignore config init error", e);
        }
    }


}
