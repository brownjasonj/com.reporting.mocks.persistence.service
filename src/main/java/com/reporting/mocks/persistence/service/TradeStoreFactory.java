package com.reporting.mocks.persistence.service;


import com.reporting.mocks.interfaces.persistence.IPersistenceStoreFactory;
import com.reporting.mocks.interfaces.persistence.ITradeStore;
import com.reporting.mocks.model.PricingGroup;
import com.reporting.mocks.persistence.service.model.TradePopulationMetaData;
import com.reporting.mocks.persistence.service.repository.TradeAssociationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
@Scope
public class TradeStoreFactory implements IPersistenceStoreFactory<ITradeStore> {
    @Autowired
    protected TradePopulationMetaDataRepository tradePopulationMetaDataRepository;
    protected TradeAssociationRepository tradeAssociationRepository;

    protected ConcurrentHashMap<String, ITradeStore> tradeStores;

    public TradeStoreFactory() {
        this.tradeAssociationRepository = new TradeAssociationRepository("http://localhost:8080");
    }

    @Autowired
    protected TradeStoreFactory(
            TradePopulationMetaDataRepository tradePopulationMetaDataRepository
    ) {
        this.tradeStores = new ConcurrentHashMap<>();
        this.tradeAssociationRepository = new TradeAssociationRepository("http://localhost:8080");
        this.tradePopulationMetaDataRepository = tradePopulationMetaDataRepository;
    }

    @Override
    public ITradeStore create(PricingGroup pricingGroupName) {
        ITradeStore store = new TradeStore(pricingGroupName,
                this.tradeAssociationRepository,
                this.tradePopulationMetaDataRepository
                );
        this.tradeStores.put(store.getPricingGroup().getName(), store);
        return store;
    }

    @Override
    public ITradeStore get(PricingGroup pricingGroup) {
        if (this.tradeStores.containsKey(pricingGroup.getName()))
            return this.tradeStores.get(pricingGroup.getName());
        else
            return null;
    }

    @Override
    public ITradeStore delete(PricingGroup pricingGroup) {
        if (this.tradeStores.containsKey(pricingGroup.getName()))
            return this.tradeStores.remove(pricingGroup.getName());
        else
            return null;
    }
}
