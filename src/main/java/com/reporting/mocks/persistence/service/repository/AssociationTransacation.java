package com.reporting.mocks.persistence.service.repository;

import com.reporting.mocks.model.trade.Trade;

import java.util.ArrayList;
import java.util.List;

public class AssociationTransacation {
    public Trade transaction;
    public List<Association> associations;

    public AssociationTransacation(Trade transaction) {
        this.transaction = transaction;
        this.associations = new ArrayList<>();
    }

    public void addAssociation(Association association) {
        associations.add(association);
    }
}
