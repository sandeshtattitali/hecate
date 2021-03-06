/*
 * Copyright (c) 2012-2015 Savoir Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.savoirtech.hecate.pojo.convert;

import com.datastax.driver.core.DataType;

public interface Converter {
//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    Converter NULL_CONVERTER = new NullConverter();

//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    DataType getDataType();

    Class<?> getValueType();

    Object toColumnValue(Object value);

    Object toFacetValue(Object value);

//----------------------------------------------------------------------------------------------------------------------
// Inner Classes
//----------------------------------------------------------------------------------------------------------------------

    class NullConverter implements Converter {
        @Override
        public Object toFacetValue(Object value) {
            return null;
        }

        @Override
        public DataType getDataType() {
            return null;
        }

        @Override
        public Object toColumnValue(Object value) {
            return null;
        }

        @Override
        public Class<?> getValueType() {
            return null;
        }
    }
}
