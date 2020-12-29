package com.reporting.mocks.persistence.service.model;

import com.reporting.mocks.interfaces.persistence.ITradePopulationReactive;
import com.reporting.mocks.model.DataMarkerType;
import com.reporting.mocks.model.PricingGroup;
import com.reporting.mocks.model.id.TradePopulationId;
import com.reporting.mocks.model.trade.Tcn;
import com.reporting.mocks.model.trade.Trade;
import com.reporting.mocks.model.trade.TradeType;
import com.reporting.mocks.persistence.service.repository.TradeAssociationRepository;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class TradePopulationReactive implements ITradePopulationReactive {
    private TradeAssociationRepository tradeAssociationRepository;
    private TradePopulationMetaData tradePopulationMetaData;

    public TradePopulationReactive(TradeAssociationRepository tradeAssociationRepository,
                                   TradePopulationMetaData tradePopulationMetaData) {
        this.tradeAssociationRepository = tradeAssociationRepository;
        this.tradePopulationMetaData = tradePopulationMetaData;
    }

    public TradePopulationMetaData getTradePopulationMetaData() {
        return this.tradePopulationMetaData;
    }

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
    public int getTradeCount() {
        return 0;
    }

    @Override
    public int getTradeCountByTradeType(TradeType tradeType) {
        if (this.tradePopulationMetaData.getTradeCountByTradeType().containsKey(tradeType))
            return this.tradePopulationMetaData.getTradeCountByTradeType().get(tradeType);
        else
            return 0;
    }

    @Override
    public Flux<Trade> getTrades() {
        return Flux.fromStream(this.tradePopulationMetaData.getPricingGroup().getTradingBooks().stream().flatMap(s ->
                this.tradeAssociationRepository.getTradesByTradingBook(s, this.tradePopulationMetaData.getAsOf()).stream()));
    }

    @Override
    public Flux<Trade> getTradesByType(TradeType tradeType) {
        return Flux.fromStream(this.tradePopulationMetaData.getPricingGroup().getTradingBooks().stream().flatMap(tradingBookName ->
                this.tradeAssociationRepository.getByTradeType(tradingBookName, tradeType, this.tradePopulationMetaData.getAsOf()).stream()));
    }

    @Override
    public List<TradeType> getTradeTypes() {
        return new ArrayList<>(this.tradePopulationMetaData.getTradeCountByTradeType().keySet());
    }

    @Override
    public Trade getTrade(Tcn tcn) {
        return this.tradeAssociationRepository.getTradeByTcn(tcn, this.tradePopulationMetaData.getAsOf());
    }
}
