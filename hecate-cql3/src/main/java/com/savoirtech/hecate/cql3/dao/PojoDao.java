package com.savoirtech.hecate.cql3.dao;

public interface PojoDao<K, P> {
//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    P findByKey(K key);
    void save(P pojo);

    void delete(K key);
}
