package com.reporting.mocks.persistence.service.model;

import com.reporting.mocks.interfaces.persistence.ITradePopulation;
import com.reporting.mocks.model.DataMarkerType;
import com.reporting.mocks.model.id.TradePopulationId;
import com.reporting.mocks.model.trade.Tcn;
import com.reporting.mocks.model.trade.Trade;
import com.reporting.mocks.model.trade.TradeType;
import com.reporting.mocks.persistence.service.repository.TradeAssociationRepository;

import java.util.*;

public class TradePopulation implements ITradePopulation {
    private TradePopulationMetaData tradePopulationMetaData;
    private TradeAssociationRepository tradeAssociationRepository;



    public TradePopulation(TradeAssociationRepository tradeAssociationRepository,
                           TradePopulationMetaData tradePopulationMetaData) {
        this.tradeAssociationRepository = tradeAssociationRepository;
        this.tradePopulationMetaData = tradePopulationMetaData;
    }

    public TradePopulationMetaData getTradePopulationMetaData() {
        return this.tradePopulationMetaData;
    }

    /*
                ITradePopulation interface implementations
             */
    @Override
    public TradePopulationId getId() {
        return this.tradePopulationMetaData.getTradePopulationId();
    }

    @Override
    public String getPricingGroupName() {
        return this.tradePopulationMetaData.getPricingGroup().getName();
    }

    @Override
    public DataMarkerType getType() {
        return this.tradePopulationMetaData.getDataMarkerType();
    }

    @Override
    public Date getAsOf() {
        return Date.from(this.tradePopulationMetaData.getAsOf());
    }

    @Override
    public Collection<Trade> getTrades() {
        ArrayList<Trade> trades = new ArrayList<>();
        for(String tradingBook : this.tradePopulationMetaData.getPricingGroup().getTradingBooks()) {
            trades.addAll(this.tradeAssociationRepository.getTradesByTradingBook(tradingBook, this.tradePopulationMetaData.getAsOf()));
        }
        return trades;
    }

    @Override
    public List<Trade> getByTradeType(TradeType tradeType) {
        ArrayList<Trade> trades = new ArrayList<>();
        for(String tradingBook : this.tradePopulationMetaData.getPricingGroup().getTradingBooks()) {
            trades.addAll(this.tradeAssociationRepository.getByTradeType(tradingBook, tradeType, this.tradePopulationMetaData.getAsOf()));
        }
        return trades;
    }

    @Override
    public List<TradeType> getTradeTypes() {
        return new ArrayList<>(this.tradePopulationMetaData.getTradeCountByTradeType().keySet());
    }

    @Override
    public int getTradeCount() {
        return this.tradePopulationMetaData.getTradeCount();
    }

    @Override
    public Trade getTrade(Tcn tcn) {
        return this.tradeAssociationRepository.getTradeByTcn(tcn, this.tradePopulationMetaData.getAsOf());
    }
}
