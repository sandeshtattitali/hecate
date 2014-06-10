package com.savoirtech.hecate.cql3.handler.pojo;

import com.savoirtech.hecate.cql3.convert.ValueConverter;
import com.savoirtech.hecate.cql3.handler.AbstractMapHandler;
import com.savoirtech.hecate.cql3.meta.FacetMetadata;
import com.savoirtech.hecate.cql3.meta.PojoMetadata;
import com.savoirtech.hecate.cql3.persistence.QueryContext;
import com.savoirtech.hecate.cql3.persistence.SaveContext;
import org.apache.commons.collections.MapUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PojoMapHandler extends AbstractMapHandler {
//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    private final ValueConverter keyConverter;
    private final FacetMetadata facetMetadata;
    private final PojoMetadata pojoMetadata;
    private final ValueConverter identifierConverter;

//----------------------------------------------------------------------------------------------------------------------
// Constructors
//----------------------------------------------------------------------------------------------------------------------

    public PojoMapHandler(ValueConverter keyConverter, FacetMetadata facetMetadata, PojoMetadata pojoMetadata, ValueConverter identifierConverter) {
        super(keyConverter.getDataType(), identifierConverter.getDataType());
        this.keyConverter = keyConverter;
        this.facetMetadata = facetMetadata;
        this.pojoMetadata = pojoMetadata;
        this.identifierConverter = identifierConverter;
    }

//----------------------------------------------------------------------------------------------------------------------
// ColumnHandler Implementation
//----------------------------------------------------------------------------------------------------------------------


    @Override
    public Object getWhereClauseValue(Object parameterValue) {
        return null;
    }

//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    @Override
    protected Object toCassandraKey(Object key) {
        return keyConverter.toCassandraValue(key);
    }

    @Override
    protected Object toCassandraValue(Object value, SaveContext context) {
        final Object identifierValue = pojoMetadata.getIdentifierFacet().getFacet().get(value);
        context.enqueue(pojoMetadata.getPojoType(), facetMetadata.getTableName(), value);
        return identifierConverter.toCassandraValue(identifierValue);
    }

    @Override
    protected Object toFacetKey(Object key) {
        return keyConverter.fromCassandraValue(key);
    }

    @Override
    protected void onFacetValueComplete(Map<Object, Object> facetValues, QueryContext context) {
        if (!MapUtils.isEmpty(facetValues)) {
            Set<Object> identifiers = new HashSet<>(facetValues.size());
            for (Object pojo : facetValues.values()) {
                identifiers.add(pojoMetadata.getIdentifierFacet().getFacet().get(pojo));
            }
            context.addPojos(pojoMetadata.getPojoType(), facetMetadata.getTableName(), pojoMetadata.newPojoMap(identifiers));
        }
    }

    @Override
    protected Object toFacetValue(Object value, QueryContext context) {
        return pojoMetadata.newPojo(identifierConverter.fromCassandraValue(value));
    }
}
