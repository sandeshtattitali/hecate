/*
 * Copyright (c) 2012-2014 Savoir Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.savoirtech.hecate.cql3.handler;

import com.datastax.driver.core.DataType;
import com.savoirtech.hecate.cql3.persistence.Dehydrator;
import com.savoirtech.hecate.cql3.persistence.Evaporator;
import com.savoirtech.hecate.cql3.persistence.Hydrator;
import com.savoirtech.hecate.cql3.util.Callback;

public interface ColumnHandler<C, F> {
//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    Object convertElement(Object parameterValue);

    F convertIdentifier(C columnValue);

    DataType getColumnType();

    void getDeletionIdentifiers(C columnValue, Evaporator context);

    C getInsertValue(F facetValue, Dehydrator dehydrator);

    void injectFacetValue(Callback<F> target, C columnValue, Hydrator hydrator);

    boolean isCascading();
}