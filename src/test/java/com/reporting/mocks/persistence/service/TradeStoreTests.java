package com.reporting.mocks.persistence.service;

import com.google.gson.Gson;
import com.reporting.mocks.interfaces.persistence.ITradePopulation;
import com.reporting.mocks.interfaces.persistence.ITradePopulationLive;
import com.reporting.mocks.model.DataMarkerType;
import com.reporting.mocks.model.PricingGroup;
import com.reporting.mocks.model.trade.Trade;
import com.reporting.mocks.model.trade.TradeTypes.Payment;
import com.reporting.mocks.persistence.service.repository.TradeAssociationRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.*;

@DataMongoTest
public class TradeStoreTests {
    @Autowired
    protected TradePopulationMetaDataRepository tradePopulationMetaDataRepository;
    protected TradeAssociationRepository tradeAssociationRepository;
    protected TradeStore tradeStore;

    public PricingGroup pricingGroup;
    public Gson gson;

    String pricingGroupName = "fx";
    List<String> tradingBooks;

    @Autowired
    public TradeStoreTests(
            TradePopulationMetaDataRepository tradePopulationMetaDataRepository) {
        this.tradePopulationMetaDataRepository = tradePopulationMetaDataRepository;
        this.tradingBooks = new ArrayList<>(Arrays.asList(UUID.randomUUID().toString(), UUID.randomUUID().toString()));
        this.pricingGroup = new PricingGroup(pricingGroupName, tradingBooks);
        this.tradeAssociationRepository = new TradeAssociationRepository("http://localhost:8080");
        this.tradeStore = new TradeStore(
                this.pricingGroup,
                this.tradeAssociationRepository,
                this.tradePopulationMetaDataRepository
        );
    }

    private String getRandomBook() {
        Optional<String> optionBookName = this.tradingBooks.stream()
                .skip((int) (this.tradingBooks.size() * Math.random()))
                .findFirst();
        return optionBookName.get();
    }

    @BeforeEach
    public void eachTestSetup() {
    }

    @Test
    public void saveAndGetTradePopulation() {
        ITradePopulationLive tradePopulationLive = this.tradeStore.getTradePopulationLive();

        Payment payment1 = TradeGenerators.createPayment(getRandomBook());
        tradePopulationLive.add(payment1);

        ITradePopulation tradePopulation = this.tradeStore.createSnapShot(DataMarkerType.IND);

        ITradePopulation tradePopulationRecovered = this.tradeStore.getTradePopulationById(tradePopulation.getId());

        Assertions.assertNotNull(tradePopulationRecovered);
        Assertions.assertEquals(tradePopulation.getId().getId(), tradePopulationRecovered.getId().getId());

        Assertions.assertEquals(tradePopulation.getTradeCount(), tradePopulationRecovered.getTradeCount());
        Assertions.assertEquals(tradePopulation.getAsOf(), tradePopulationRecovered.getAsOf());

        Trade trade = tradePopulation.getTrade(payment1.getTcn());
        Trade tradeRecovered = tradePopulationRecovered.getTrade(payment1.getTcn());

        Assertions.assertNotNull(trade);
        Assertions.assertNotNull(tradeRecovered);
    }
}
