/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.kafka.connect.log.anlaytics.config;

import java.util.Map;
 
import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import com.google.common.base.Preconditions;

 
public class LogAnalyticsSinkConfig extends AbstractConfig {
 
    public static ConfigDef CONFIG = new ConfigDef();
    private Map<String, String> properties;

    

    public LogAnalyticsSinkConfig(Map<String, String> originals) {
        this(CONFIG, originals);
    }

    public LogAnalyticsSinkConfig(ConfigDef definition, Map<String, String> originals) {
        super(definition, originals);
        this.properties = originals;
    }   
    
   
    /**
     * @param propertyName
     * @param defaultValue
     * @return
     */
    public String getPropertyValue(final String propertyName, final String defaultValue) {
        String propertyValue = getPropertyValue(propertyName);
        return propertyValue != null ? propertyValue : defaultValue;
    }
    
    /**
     * @param propertyName
     * @return
     */
    public String getPropertyValue(final String propertyName) {
        Preconditions.checkNotNull(propertyName);
        return this.properties.get(propertyName);
    }
}
