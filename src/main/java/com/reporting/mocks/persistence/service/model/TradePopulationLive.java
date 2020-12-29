package com.reporting.mocks.persistence.service.model;

import com.reporting.mocks.interfaces.persistence.ITradePopulation;
import com.reporting.mocks.interfaces.persistence.ITradePopulationLive;
import com.reporting.mocks.model.DataMarkerType;
import com.reporting.mocks.model.PricingGroup;
import com.reporting.mocks.model.id.TradePopulationId;
import com.reporting.mocks.model.trade.Tcn;
import com.reporting.mocks.model.trade.Trade;
import com.reporting.mocks.model.trade.TradeType;
import com.reporting.mocks.persistence.service.repository.TradeAssociationRepository;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TradePopulationLive implements ITradePopulation, ITradePopulationLive  {
    protected TradeAssociationRepository tradeAssociationRepository;
    protected TradePopulationId tradePopulationId;
    protected PricingGroup pricingGroup;
    protected DataMarkerType dataMarkerType;
    protected Set<Tcn> listOfTcns;
    protected ConcurrentHashMap<Tcn,Trade> trades;     // set of current trades in the trade population
    protected ConcurrentHashMap<TradeType, List<Tcn>> tradeTypeTrades;

    public TradePopulationLive(TradeAssociationRepository tradeAssociationRepository, PricingGroup pricingGroup) {
        this.tradeAssociationRepository = tradeAssociationRepository;
        this.pricingGroup = pricingGroup;
        this.dataMarkerType = DataMarkerType.LIVE;
        this.tradePopulationId = new TradePopulationId();
        this.trades = new ConcurrentHashMap<>();
        this.tradeTypeTrades = new ConcurrentHashMap<>();
        this.listOfTcns = ConcurrentHashMap.newKeySet();
    }

    public synchronized TradePopulation createTradePopulation(DataMarkerType dataMarkerType) {
        HashMap<TradeType, Integer> tradeCountByTradeType = new HashMap<>();
        for(TradeType tradeType: tradeTypeTrades.keySet()) {
            tradeCountByTradeType.put(tradeType, tradeTypeTrades.get(tradeType).size());
        }

        return new TradePopulation(
                this.tradeAssociationRepository,
                new TradePopulationMetaData(
                    new TradePopulationId(this.pricingGroup.getName()),
                    this.pricingGroup,
                    dataMarkerType,
                    Instant.now(),
                    tradeCountByTradeType,
                    this.listOfTcns.size())
        );
    }

    public synchronized TradePopulationReactive createTradePopulationReactive(DataMarkerType dataMarkerType) {
        HashMap<TradeType, Integer> tradeCountByTradeType = new HashMap<>();
        for(TradeType tradeType: tradeTypeTrades.keySet()) {
            tradeCountByTradeType.put(tradeType, tradeTypeTrades.get(tradeType).size());
        }

        return new TradePopulationReactive(
                this.tradeAssociationRepository,
                new TradePopulationMetaData(
                        new TradePopulationId(this.pricingGroup.getName()),
                        this.pricingGroup,
                        dataMarkerType,
                        Instant.now(),
                        tradeCountByTradeType,
                        this.listOfTcns.size())
        );
    }

    /*
        ITradePopulation implementations
     */
    @Override
    public TradePopulationId getId() {
        return this.tradePopulationId;
    }

    @Override
    public String getPricingGroupName() {
        return this.pricingGroup.getName();
    }

    @Override
    public DataMarkerType getType() {
        return this.dataMarkerType;
    }

    @Override
    public Date getAsOf() {
        return new Date();
    }

    @Override
    public synchronized Collection<Trade> getTrades() {
        return this.trades.values();
    }

    @Override
    public synchronized List<Trade> getByTradeType(TradeType tradeType) {
        if (this.tradeTypeTrades.containsKey(tradeType)) {
            return this.tradeTypeTrades.get(tradeType).stream().map(tcn -> this.trades.get(tcn)).collect(Collectors.toList());
        }
        else {
            return new ArrayList<>();
        }
    }

    @Override
    public synchronized List<TradeType> getTradeTypes() {
        return new ArrayList<>(this.tradeTypeTrades.keySet());
    }

    @Override
    public synchronized int getTradeCount() {
        return this.trades.values().size();
    }

    @Override
    public synchronized Trade getTrade(Tcn tcn) {
        if (this.trades.containsKey(tcn))
            return this.trades.get(tcn);
        else
            return null;
    }

    /*
        ITradePopulationLive implementation
     */
    @Override
    public synchronized Trade add(Trade trade) {
        tradeAssociationRepository.save(trade, Instant.now());
        if (!this.listOfTcns.contains(trade.getTcn())) {
            List<Tcn> tcnsPerType;
            if (!this.tradeTypeTrades.containsKey(trade.getTradeType())) {
                tcnsPerType = new ArrayList<>();
                this.tradeTypeTrades.put(trade.getTradeType(), tcnsPerType);
            }
            else {
                tcnsPerType = this.tradeTypeTrades.get(trade.getTradeType());
            }
            tcnsPerType.add(trade.getTcn());
            this.listOfTcns.add(trade.getTcn());
            this.trades.put(trade.getTcn(), trade);
        }
        return trade;
    }

    @Override
    public synchronized Trade oneAtRandom() {
        if (this.listOfTcns.isEmpty()) {
            return null;
        }
        else {
            Optional<Tcn> optionalTrade = this.listOfTcns.stream()
                    .skip((int) (this.listOfTcns.size() * Math.random()))
                    .findFirst();
            return this.getTrade(optionalTrade.get());
        }
    }

    @Override
    public synchronized Trade delete(Tcn tcn) {
        if (this.listOfTcns.contains(tcn)) {
            this.tradeAssociationRepository.delete(tcn, Instant.now());
            Trade trade = this.getTrade(tcn);
            if (this.tradeTypeTrades.containsKey(trade.getTradeType())) {
                this.tradeTypeTrades.get(trade.getTradeType()).remove(tcn);
            }
            this.listOfTcns.remove(tcn);
            this.trades.remove(tcn);
            return trade;
        }
        else {
            return null;
        }
    }
}
