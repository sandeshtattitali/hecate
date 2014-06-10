package com.savoirtech.hecate.cql3.handler.scalar;

import com.savoirtech.hecate.cql3.convert.ValueConverter;
import com.savoirtech.hecate.cql3.persistence.QueryContext;
import com.savoirtech.hecate.cql3.persistence.SaveContext;

public class ScalarMapHandler extends com.savoirtech.hecate.cql3.handler.AbstractMapHandler {
//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    private final ValueConverter keyConverter;
    private final ValueConverter valueConverter;

//----------------------------------------------------------------------------------------------------------------------
// Constructors
//----------------------------------------------------------------------------------------------------------------------

    public ScalarMapHandler(ValueConverter keyConverter, ValueConverter valueConverter) {
        super(keyConverter.getDataType(), valueConverter.getDataType());
        this.keyConverter = keyConverter;
        this.valueConverter = valueConverter;
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
        return valueConverter.toCassandraValue(value);
    }

    @Override
    protected Object toFacetKey(Object key) {
        return keyConverter.fromCassandraValue(key);
    }

    @Override
    protected Object toFacetValue(Object value, QueryContext context) {
        return valueConverter.fromCassandraValue(value);
    }
}