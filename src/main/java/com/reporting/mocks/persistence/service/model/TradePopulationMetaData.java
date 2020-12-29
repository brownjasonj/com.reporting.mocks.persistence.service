package com.reporting.mocks.persistence.service.model;

import com.reporting.mocks.model.DataMarkerType;
import com.reporting.mocks.model.PricingGroup;
import com.reporting.mocks.model.id.TradePopulationId;
import com.reporting.mocks.model.trade.TradeType;

import java.time.Instant;
import java.util.HashMap;

public class TradePopulationMetaData {
    private TradePopulationId tradePopulationId;
    private PricingGroup pricingGroup;
    private DataMarkerType dataMarkerType;
    private Instant asOf;
    private HashMap<TradeType, Integer> tradeCountByTradeType;
    private int tradeCount;

    public TradePopulationMetaData(TradePopulationId tradePopulationId,
                                   PricingGroup pricingGroup,
                                   DataMarkerType dataMarkerType,
                                   Instant asOf,
                                   HashMap<TradeType, Integer> tradeCountByTradeType,
                                   int tradeCount) {
        this.tradePopulationId = tradePopulationId;
        this.pricingGroup = pricingGroup;
        this.dataMarkerType = dataMarkerType;
        this.asOf = asOf;
        this.tradeCountByTradeType = tradeCountByTradeType;
        this.tradeCount = tradeCount;
    }

    public TradePopulationId getTradePopulationId() {
        return tradePopulationId;
    }

    public PricingGroup getPricingGroup() {
        return pricingGroup;
    }

    public DataMarkerType getDataMarkerType() {
        return dataMarkerType;
    }

    public Instant getAsOf() {
        return asOf;
    }

    public HashMap<TradeType, Integer> getTradeCountByTradeType() {
        return tradeCountByTradeType;
    }

    public int getTradeCount() {
        return tradeCount;
    }
}
