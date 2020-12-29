package com.reporting.mocks.persistence.service.repository;

import java.util.HashMap;

public class AssociationTransactionQuery {
    protected HashMap<String, String> association;
    protected HashMap<String, String> transaction;

    public AssociationTransactionQuery() {
        this.association = new HashMap<>();
        this.transaction = new HashMap<>();
    }

    public void addTransactionConstraint(String key, String value) {
        this.transaction.put(key, value);
    }

    public void addAssociationConstraint(String key, String value) {
        this.association.put(key, value);
    }
}
