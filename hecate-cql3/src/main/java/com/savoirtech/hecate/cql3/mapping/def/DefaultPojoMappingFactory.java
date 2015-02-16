/*
 * Copyright (c) 2012-2015 Savoir Technologies, Inc.
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

package com.savoirtech.hecate.cql3.mapping.def;

import com.datastax.driver.core.Session;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.savoirtech.hecate.cql3.handler.ColumnHandlerFactory;
import com.savoirtech.hecate.cql3.handler.def.DefaultColumnHandlerFactory;
import com.savoirtech.hecate.cql3.mapping.FacetMapping;
import com.savoirtech.hecate.cql3.mapping.PojoMapping;
import com.savoirtech.hecate.cql3.mapping.PojoMappingFactory;
import com.savoirtech.hecate.cql3.meta.FacetMetadata;
import com.savoirtech.hecate.cql3.meta.PojoMetadata;
import com.savoirtech.hecate.cql3.meta.PojoMetadataFactory;
import com.savoirtech.hecate.cql3.meta.def.DefaultPojoMetadataFactory;
import com.savoirtech.hecate.cql3.schema.CreateVerifier;
import com.savoirtech.hecate.cql3.schema.SchemaVerifier;
import com.savoirtech.hecate.cql3.util.PojoCacheKey;

public class DefaultPojoMappingFactory implements PojoMappingFactory {
//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    private final Session session;
    private final PojoMetadataFactory pojoMetadataFactory;
    private final ColumnHandlerFactory columnHandlerFactory;
    private final SchemaVerifier schemaVerifier;
    private final LoadingCache<PojoCacheKey, PojoMapping> pojoMappings;

//----------------------------------------------------------------------------------------------------------------------
// Constructors
//----------------------------------------------------------------------------------------------------------------------

    public DefaultPojoMappingFactory(Session session) {
        this.session = session;
        this.pojoMetadataFactory = new DefaultPojoMetadataFactory();
        this.columnHandlerFactory = new DefaultColumnHandlerFactory(pojoMetadataFactory);
        this.schemaVerifier = new CreateVerifier();
        this.pojoMappings = CacheBuilder.newBuilder().build(new PojoMappingCacheLoader());
    }

    public DefaultPojoMappingFactory(Session session, PojoMetadataFactory pojoMetadataFactory, ColumnHandlerFactory columnHandlerFactory, SchemaVerifier schemaVerifier) {
        this.session = session;
        this.pojoMetadataFactory = pojoMetadataFactory;
        this.columnHandlerFactory = columnHandlerFactory;
        this.schemaVerifier = schemaVerifier;
        this.pojoMappings = CacheBuilder.newBuilder().build(new PojoMappingCacheLoader());
    }

//----------------------------------------------------------------------------------------------------------------------
// PojoMappingFactory Implementation
//----------------------------------------------------------------------------------------------------------------------

    @Override
    public PojoMapping getPojoMapping(PojoCacheKey key) {
        return pojoMappings.getUnchecked(key);
    }

//----------------------------------------------------------------------------------------------------------------------
// Inner Classes
//----------------------------------------------------------------------------------------------------------------------

    private class PojoMappingCacheLoader extends CacheLoader<PojoCacheKey, PojoMapping> {
        @Override
        @SuppressWarnings("unchecked")
        public PojoMapping load(PojoCacheKey key) throws Exception {
            final PojoMetadata pojoMetadata = pojoMetadataFactory.getPojoMetadata(key.getPojoType());
            final PojoMapping pojoMapping = new PojoMapping(pojoMetadata, key.getTableName());
            for (FacetMetadata facet : pojoMetadata.getFacets().values()) {
                pojoMapping.addFacet(new FacetMapping(facet, columnHandlerFactory.getColumnHandler(facet)));
            }
            schemaVerifier.verifySchema(session, pojoMapping);
            return pojoMapping;
        }
    }
}
