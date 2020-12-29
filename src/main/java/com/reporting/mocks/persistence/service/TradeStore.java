package com.reporting.mocks.persistence.service;

import com.reporting.mocks.interfaces.persistence.ITradePopulation;
import com.reporting.mocks.interfaces.persistence.ITradePopulationLive;
import com.reporting.mocks.interfaces.persistence.ITradePopulationReactive;
import com.reporting.mocks.interfaces.persistence.ITradeStore;
import com.reporting.mocks.model.DataMarkerType;
import com.reporting.mocks.model.PricingGroup;
import com.reporting.mocks.model.id.TradePopulationId;
import com.reporting.mocks.persistence.service.model.*;
import com.reporting.mocks.persistence.service.repository.TradeAssociationRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class TradeStore implements ITradeStore {
    protected PricingGroup pricingGroup;
    protected TradePopulationLive tradePopulationLive;
    protected TradeAssociationRepository tradeAssociationRepository;
    protected TradePopulationMetaDataRepository tradePopulationMetaDataRepository;

    public TradeStore(PricingGroup pricingGroup,
                      TradeAssociationRepository tradeAssociationRepository,
                      TradePopulationMetaDataRepository tradePopulationMetaDataRepository
    ) {
        this.pricingGroup = pricingGroup;
        this.tradeAssociationRepository = tradeAssociationRepository;
        this.tradePopulationMetaDataRepository = tradePopulationMetaDataRepository;
        this.tradePopulationLive = new TradePopulationLive(tradeAssociationRepository, pricingGroup);
    }

    @Override
    public PricingGroup getPricingGroup() {
        return this.pricingGroup;
    }

    @Override
    public ITradePopulation createSnapShot(DataMarkerType type) {
        TradePopulation tradePopulation = this.tradePopulationLive.createTradePopulation(type);
        this.tradePopulationMetaDataRepository.save(tradePopulation.getTradePopulationMetaData());
        return tradePopulation;
    }



    public ITradePopulationLive getTradePopulationLive() {
        return tradePopulationLive;
    }

    @Override
    public ITradePopulation getTradePopulationById(TradePopulationId id) {
        TradePopulationMetaData tradePopulationMetaData = this.tradePopulationMetaDataRepository.getTradePopulationMetaDataByTradePopulationId(id);
        return new TradePopulation(tradeAssociationRepository, tradePopulationMetaData);
    }

    @Override
    public Collection<ITradePopulation> getAllTradePopulation() {
        return this.tradePopulationMetaDataRepository.findAll().stream().map(tradePopulationMetaData -> new TradePopulation(this.tradeAssociationRepository, tradePopulationMetaData)).collect(Collectors.toList());
    }


    @Override
    public List<TradePopulationId> getTradePopulationsIds() {
        return this.tradePopulationMetaDataRepository.findAll().stream().map(tradePopulationMetaData -> tradePopulationMetaData.getTradePopulationId()).collect(Collectors.toList());
    }

    @Override
    public ITradePopulationReactive createReactiveSnapShot(DataMarkerType type) {
        TradePopulationReactive tradePopulationReactive = this.tradePopulationLive.createTradePopulationReactive(type);
        this.tradePopulationMetaDataRepository.save(tradePopulationReactive.getTradePopulationMetaData());
        return tradePopulationReactive;
    }

    @Override
    public ITradePopulationReactive getTradePopulationReactiveById(TradePopulationId id) {
        TradePopulationMetaData tradePopulationMetaData = this.tradePopulationMetaDataRepository.getTradePopulationMetaDataByTradePopulationId(id);
        return new TradePopulationReactive(tradeAssociationRepository, tradePopulationMetaData);
    }
}
