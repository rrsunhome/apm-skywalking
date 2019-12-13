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


import org.apache.skywalking.apm.plugin.exception.ignore.conf.IgnoreExceptionConfig;
import org.apache.skywalking.apm.plugin.exception.ignore.matcher.DefaultExceptionIgnoreMatcher;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ExceptionPathMatcherTest {

    @Before
    public void before() {
        IgnoreExceptionConfig.Exception.IGNORE_BUSINESS_EXCEPTION_PACKAGES = "java.lang.RuntimeException";

    }

    @Test
    public void testExceptionMatcher() {
        boolean match = DefaultExceptionIgnoreMatcher.getInstance().match("java.lang.RuntimeException");
        Assert.assertEquals(true, match);

    }
}
