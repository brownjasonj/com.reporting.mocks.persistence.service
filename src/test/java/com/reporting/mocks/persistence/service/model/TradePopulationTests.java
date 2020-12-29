package com.reporting.mocks.persistence.service.model;

import com.google.gson.Gson;
import com.reporting.mocks.model.DataMarkerType;
import com.reporting.mocks.model.PricingGroup;
import com.reporting.mocks.model.trade.Trade;
import com.reporting.mocks.model.trade.TradeType;
import com.reporting.mocks.model.trade.TradeTypes.Payment;
import com.reporting.mocks.persistence.service.TradeGenerators;
import com.reporting.mocks.persistence.service.repository.TradeAssociationRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

public class TradePopulationTests {
    public TradeAssociationRepository tradeAssociationRepository;
    public TradePopulationLive tradePopulationLive;
    public TradePopulation tradePopulation;
    public PricingGroup pricingGroup;
    public Gson gson;

    String pricingGroupName = "fx";
    List<String> tradingBooks;

    private String getRandomBook() {
        Optional<String> optionBookName = this.tradingBooks.stream()
                .skip((int) (this.tradingBooks.size() * Math.random()))
                .findFirst();
        return optionBookName.get();
    }

    @BeforeEach
    public void eachTestSetup() {
        this.gson = new Gson();
        this.tradeAssociationRepository = new TradeAssociationRepository("http://localhost:8080");
        this.tradingBooks = new ArrayList<>(Arrays.asList(UUID.randomUUID().toString(), UUID.randomUUID().toString()));
        this.pricingGroup = new PricingGroup(pricingGroupName, tradingBooks);
        this.tradePopulationLive = new TradePopulationLive(this.tradeAssociationRepository, this.pricingGroup);
    }

    @Test
    public void singleTrade() throws InterruptedException {
        Payment payment1 = TradeGenerators.createPayment(getRandomBook());

        // add a trade to the live trade population
        this.tradePopulationLive.add(payment1);

        // check that the live trade population has a single trade and is the one added
        Assertions.assertEquals(1, this.tradePopulationLive.getTradeCount());
        Assertions.assertEquals(payment1.getTcn(), this.tradePopulationLive.oneAtRandom().getTcn());
        Assertions.assertEquals(payment1.getTcn(), this.tradePopulationLive.getTrade(payment1.getTcn()).getTcn());

        List<Trade> trades = this.tradePopulationLive.getByTradeType(TradeType.Payment);
        Assertions.assertEquals(1, trades.size());
        Assertions.assertEquals(payment1.getTcn(), trades.get(0).getTcn());

        Thread.sleep(200);

        // create a snapshot of the trade population
        TradePopulation newTradePopulation = this.tradePopulationLive.createTradePopulation(DataMarkerType.IND);

        // check that the trade population has a single trade, which is the trade from the live population
        Assertions.assertEquals(1, newTradePopulation.getTradeCount());
        Assertions.assertEquals(payment1.getTcn(), newTradePopulation.getTrade(payment1.getTcn()).getTcn());

        List<Trade> trades2 = newTradePopulation.getByTradeType(TradeType.Payment);
        Assertions.assertEquals(1, trades2.size());
        Assertions.assertEquals(payment1.getTcn(), trades2.get(0).getTcn());


        // now delete the trade from the live population
        this.tradePopulationLive.delete(payment1.getTcn());

        // there should be no trades in the live population
        Assertions.assertEquals(0, this.tradePopulationLive.getTradeCount());
        Assertions.assertEquals(0, this.tradePopulationLive.getTrades().size());

        // but the snapshot trade population should still have 1 trade
        Assertions.assertEquals(1, newTradePopulation.getTradeCount());
        Assertions.assertEquals(payment1.getTcn(), newTradePopulation.getTrade(payment1.getTcn()).getTcn());

        List<Trade> trades3 = newTradePopulation.getByTradeType(TradeType.Payment);
        Assertions.assertEquals(1, trades3.size());
        Assertions.assertEquals(payment1.getTcn(), trades3.get(0).getTcn());

    }

    @Test
    void testLargeTradePopulation() throws InterruptedException {
        int populationCount = 100;

        for(int i = 0; i < populationCount; i++)  {
            this.tradePopulationLive.add(TradeGenerators.createPayment(getRandomBook()));
        }

        Assertions.assertEquals(populationCount, this.tradePopulationLive.getTradeCount());

        // create a snapshot
        Thread.sleep(200);

        // create a snapshot of the trade population
        TradePopulation newTradePopulation = this.tradePopulationLive.createTradePopulation(DataMarkerType.IND);

        Assertions.assertEquals(populationCount, newTradePopulation.getTrades().size());
    }
}
