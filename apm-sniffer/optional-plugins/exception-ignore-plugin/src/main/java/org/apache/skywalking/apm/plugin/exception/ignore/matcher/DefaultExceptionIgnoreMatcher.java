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

package org.apache.skywalking.apm.plugin.exception.ignore.matcher;

import org.apache.skywalking.apm.agent.core.boot.AgentPackageNotFoundException;
import org.apache.skywalking.apm.agent.core.boot.BootService;
import org.apache.skywalking.apm.agent.core.boot.ServiceManager;
import org.apache.skywalking.apm.agent.core.conf.ConfigNotFoundException;
import org.apache.skywalking.apm.agent.core.logging.api.ILog;
import org.apache.skywalking.apm.agent.core.logging.api.LogManager;
import org.apache.skywalking.apm.plugin.exception.ignore.ExceptionIgnoreExtendService;
import org.apache.skywalking.apm.plugin.exception.ignore.conf.IgnoreExceptionConfig;
import org.apache.skywalking.apm.plugin.exception.ignore.conf.IgnoreExceptionConfigInitializer;
import org.apache.skywalking.apm.util.StringUtil;

import java.util.Arrays;

/**
 * qijia.wang
 */
public class DefaultExceptionIgnoreMatcher implements ExceptionIgnoreMatcher {

    private static final ILog LOGGER = LogManager.getLogger(DefaultExceptionIgnoreMatcher.class);

    private static final String DEFAULT_EXCEPTION_SPLIT = ",";

    public static ExceptionIgnoreMatcher getInstance() {
        return ExceptionIgnoreHolder.INSTANCE;
    }

    private static class ExceptionIgnoreHolder {
        private static ExceptionIgnoreMatcher INSTANCE = new DefaultExceptionIgnoreMatcher();
    }

    @Override
    public boolean match(String exceptionClassName) {

        String ignoreBusinessExceptionPackages = IgnoreExceptionConfig.Exception.IGNORE_BUSINESS_EXCEPTION_PACKAGES;
        if (StringUtil.isEmpty(ignoreBusinessExceptionPackages)) {
            try {
                IgnoreExceptionConfigInitializer.initialize();
                ignoreBusinessExceptionPackages = IgnoreExceptionConfig.Exception.IGNORE_BUSINESS_EXCEPTION_PACKAGES;
            } catch (Exception e) {
                LOGGER.error("exception config load failed");
            }
        }

        LOGGER.debug("ignore_business_exception_packages:{}", ignoreBusinessExceptionPackages);
        if (StringUtil.isEmpty(ignoreBusinessExceptionPackages)) {
            return false;
        }
        if (Arrays.asList(ignoreBusinessExceptionPackages.split(DEFAULT_EXCEPTION_SPLIT)).contains(exceptionClassName)) {
            LOGGER.debug("business exceptionClassName ignore:{}", exceptionClassName);
            return true;
        }

        return false;
    }


    @Override
    public boolean match(Throwable throwable) {
        return this.match(throwable.getClass().getName());
    }
}
