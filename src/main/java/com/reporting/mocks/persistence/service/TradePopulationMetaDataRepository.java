package com.reporting.mocks.persistence.service;

import com.reporting.mocks.model.id.TradePopulationId;
import com.reporting.mocks.persistence.service.model.TradePopulationMetaData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

@Component
public interface TradePopulationMetaDataRepository extends MongoRepository<TradePopulationMetaData, TradePopulationId> {
    TradePopulationMetaData getTradePopulationMetaDataByTradePopulationId(TradePopulationId id);
}
