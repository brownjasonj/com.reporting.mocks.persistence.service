package com.reporting.mocks.persistence.service;

import com.reporting.mocks.interfaces.persistence.IMarketStore;
import com.reporting.mocks.interfaces.persistence.IPersistenceStoreFactory;
import com.reporting.mocks.model.PricingGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class MarketEnvStoreFactory implements IPersistenceStoreFactory<IMarketStore> {
    @Autowired
    protected MarketEnvRepository marketEnvRepository;
    protected ConcurrentHashMap<String, IMarketStore> stores;

    @Autowired
    public MarketEnvStoreFactory(MarketEnvRepository marketEnvRepository) {
        this.marketEnvRepository = marketEnvRepository;
        this.stores = new ConcurrentHashMap<>();
    }

    @Override
    public IMarketStore get(PricingGroup pricingGroup) {
        if (this.stores.containsKey(pricingGroup.getName()))
            return stores.get(pricingGroup.getName());
        else
            return null;
    }

    @Override
    public IMarketStore delete(PricingGroup pricingGroup) {
        return null;
    }

    @Override
    public IMarketStore create(PricingGroup pricingGroup) {
        IMarketStore store = new MarketEnvStore(pricingGroup, this.marketEnvRepository);
        this.stores.put(pricingGroup.getName(), store);
        return store;
    }
}
