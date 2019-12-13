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

package org.apache.skywalking.apm.plugin.exception.ignore.conf;

import org.apache.skywalking.apm.agent.core.boot.AgentPackageNotFoundException;
import org.apache.skywalking.apm.agent.core.boot.AgentPackagePath;
import org.apache.skywalking.apm.agent.core.conf.ConfigNotFoundException;
import org.apache.skywalking.apm.agent.core.conf.ConfigReadFailedException;
import org.apache.skywalking.apm.agent.core.logging.api.ILog;
import org.apache.skywalking.apm.agent.core.logging.api.LogManager;
import org.apache.skywalking.apm.util.ConfigInitializer;
import org.apache.skywalking.apm.util.PropertyPlaceholderHelper;
import org.apache.skywalking.apm.util.StringUtil;

import java.io.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * qijia.wang
 */
public class IgnoreExceptionConfigInitializer {
    private static final ILog logger = LogManager.getLogger(IgnoreExceptionConfigInitializer.class);
    private static String SPECIFIED_CONFIG_PATH = "skywalking_ignore_exception_config";
    private static String DEFAULT_CONFIG_FILE_NAME = "/config/apm-exception-ignore-plugin.config";
    private static String ENV_KEY_PREFIX = "skywalking.";


    public static void initialize() throws ConfigNotFoundException, AgentPackageNotFoundException {
        InputStreamReader configFileStream;

        try {
            configFileStream = loadConfig();
            Properties properties = new Properties();
            properties.load(configFileStream);
            for (String key : properties.stringPropertyNames()) {
                String value = (String) properties.get(key);
                //replace the key's value. properties.replace(key,value) in jdk8+
                properties.put(key, PropertyPlaceholderHelper.INSTANCE.replacePlaceholders(value, properties));
            }
            ConfigInitializer.initialize(properties, IgnoreExceptionConfig.class);
        } catch (Exception e) {
            logger.error(e, "Failed to read the config file, skywalking is going to run in default config.");
        }

        try {
            overrideConfigBySystemProp();
        } catch (Exception e) {
            logger.error(e, "Failed to read the system properties.");
        }
        if (StringUtil.isEmpty(IgnoreExceptionConfig.Exception.IGNORE_BUSINESS_EXCEPTION_PACKAGES)) {
            throw new ExceptionInInitializerError("`exception.ignore_business_exception_packages` is missing.");
        }

        logger.info("ex context: {}", IgnoreExceptionConfig.Exception.IGNORE_BUSINESS_EXCEPTION_PACKAGES);
    }

    /**
     * Override the config by system properties. The property key must start with `skywalking`, the result should be as same
     * as in `agent.config`
     * <p>
     * such as: Property key of `agent.service_name` should be `skywalking.agent.service_name`
     */
    private static void overrideConfigBySystemProp() throws IllegalAccessException {
        Properties properties = new Properties();
        Properties systemProperties = System.getProperties();
        Iterator<Map.Entry<Object, Object>> entryIterator = systemProperties.entrySet().iterator();
        while (entryIterator.hasNext()) {
            Map.Entry<Object, Object> prop = entryIterator.next();
            String key = prop.getKey().toString();
            if (key.startsWith(ENV_KEY_PREFIX)) {
                String realKey = key.substring(ENV_KEY_PREFIX.length());
                properties.put(realKey, prop.getValue());
            }
        }

        if (!properties.isEmpty()) {
            ConfigInitializer.initialize(properties, IgnoreExceptionConfig.class);
        }
    }

    /**
     * Load the specified config file or default config file
     *
     * @return the config file {@link InputStream}, or null if not needEnhance.
     */
    private static InputStreamReader loadConfig() throws AgentPackageNotFoundException, ConfigNotFoundException, ConfigReadFailedException {

        String specifiedConfigPath = System.getProperties().getProperty(SPECIFIED_CONFIG_PATH);
        File configFile = StringUtil.isEmpty(specifiedConfigPath) ? new File(AgentPackagePath.getPath(), DEFAULT_CONFIG_FILE_NAME) : new File(specifiedConfigPath);
        if (configFile.exists() && configFile.isFile()) {
            try {
                logger.info("Exception Config file found in {}.", configFile);
                return new InputStreamReader(new FileInputStream(configFile), "UTF-8");
            } catch (FileNotFoundException e) {
                throw new ConfigNotFoundException("Failed to load agent.config", e);
            } catch (UnsupportedEncodingException e) {
                throw new ConfigReadFailedException("Failed to load agent.config", e);
            }
        }
        throw new ConfigNotFoundException("Failed to load agent.config.");
    }
}
