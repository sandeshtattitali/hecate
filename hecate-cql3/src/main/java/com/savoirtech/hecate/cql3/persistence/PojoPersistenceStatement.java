package com.savoirtech.hecate.cql3.persistence;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.RegularStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.savoirtech.hecate.cql3.meta.ColumnDescriptor;
import com.savoirtech.hecate.cql3.meta.PojoDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PojoPersistenceStatement<P> {
    //----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------
    private static final Logger LOGGER = LoggerFactory.getLogger(PojoPersistenceStatement.class);

    private final Session session;
    private final PreparedStatement preparedStatement;
    private final PojoDescriptor<P> pojoDescriptor;
//----------------------------------------------------------------------------------------------------------------------
// Constructors
//----------------------------------------------------------------------------------------------------------------------

    protected PojoPersistenceStatement(Session session, RegularStatement statement, PojoDescriptor<P> pojoDescriptor) {
        this.session = session;
        this.preparedStatement = session.prepare(statement);
        this.pojoDescriptor = pojoDescriptor;
    }

    protected static <P> Select.Selection pojoSelect(PojoDescriptor<P> pojoDescriptor) {
        final Select.Selection select = QueryBuilder.select();
        for (ColumnDescriptor columnDescriptor : pojoDescriptor.getColumns()) {
            select.column(columnDescriptor.getColumnName());
        }
        return select;
    }

    protected ResultSet execute(Object... parameters) {
        return execute(Arrays.asList(parameters));
    }

    protected P one(ResultSet resultSet) {
        Row row = resultSet.one();
        return row == null ? null : mapPojoFromRow(row);
    }

    protected ResultSet execute(List<Object> parameters) {
        LOGGER.debug("CQL: {} with parameters {}...", preparedStatement.getQueryString(), parameters);
        return session.execute(preparedStatement.bind(parameters.toArray(new Object[parameters.size()])));
    }

    protected List<ColumnDescriptor> allColumns() {
        return pojoDescriptor.getColumns();
    }

    protected ColumnDescriptor identifierColumn() {
        return pojoDescriptor.getIdentifierColumn();
    }

    protected Object cassandraValue(P pojo, ColumnDescriptor descriptor) {
        return descriptor.getMapping().fieldCassandraValue(pojo);
    }

    protected List<Object> cassandraValues(P pojo, List<ColumnDescriptor> descriptors) {
        final List<Object> values = new ArrayList<>(descriptors.size());
        for (ColumnDescriptor columnDescriptor : descriptors) {
            values.add(columnDescriptor.getMapping().fieldCassandraValue(pojo));
        }
        return values;
    }

    protected P mapPojoFromRow(Row row) {
        P pojo = pojoDescriptor.newInstance();
        int columnIndex = 0;
        for (ColumnDescriptor descriptor : pojoDescriptor.getColumns()) {
            descriptor.getMapping().populateFromRow(pojo, row, columnIndex);
            columnIndex++;
        }
        return pojo;
    }
}
