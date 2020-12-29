package com.reporting.mocks.persistence.service.model;

import com.google.gson.Gson;
import com.reporting.mocks.model.PricingGroup;
import com.reporting.mocks.model.trade.Tcn;
import com.reporting.mocks.model.trade.Trade;
import com.reporting.mocks.model.trade.TradeType;
import com.reporting.mocks.model.trade.TradeTypes.Payment;
import com.reporting.mocks.model.trade.TradeTypes.Spot;
import com.reporting.mocks.model.underlying.Underlying;
import com.reporting.mocks.persistence.service.TradeGenerators;
import com.reporting.mocks.persistence.service.repository.TradeAssociationRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.*;

public class TradePopulationLiveTests {
    public TradeAssociationRepository tradeAssociationRepository;
    public TradePopulationLive tradePopulationLive;
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
    public void singleTrade() {
        Payment payment1 = TradeGenerators.createPayment(getRandomBook());

        this.tradePopulationLive.add(payment1);

        Assertions.assertEquals(1, this.tradePopulationLive.getTradeCount());
        Assertions.assertEquals(payment1.getTcn(), this.tradePopulationLive.oneAtRandom().getTcn());
        Assertions.assertEquals(payment1.getTcn(), this.tradePopulationLive.getTrade(payment1.getTcn()).getTcn());

        List<Trade> trades = this.tradePopulationLive.getByTradeType(TradeType.Payment);
        Assertions.assertEquals(1, trades.size());
        Assertions.assertEquals(payment1.getTcn(), trades.get(0).getTcn());

        this.tradePopulationLive.delete(payment1.getTcn());
        Assertions.assertEquals(0, this.tradePopulationLive.getTradeCount());
        Assertions.assertNull(this.tradePopulationLive.oneAtRandom());
        Assertions.assertNull(this.tradePopulationLive.getTrade(payment1.getTcn()));

    }

    @Test
    void deleteTwice() {
        Payment payment1 = TradeGenerators.createPayment(getRandomBook());
        this.tradePopulationLive.add(payment1);
        Assertions.assertEquals(1, this.tradePopulationLive.getTradeCount());
        Assertions.assertEquals(1, this.tradePopulationLive.getByTradeType(TradeType.Payment).size());
        Assertions.assertNotNull(this.tradePopulationLive.delete(payment1.getTcn()));
        Assertions.assertEquals(0, this.tradePopulationLive.getTradeCount());
        Assertions.assertEquals(0, this.tradePopulationLive.getByTradeType(TradeType.Payment).size());
        Assertions.assertNull(this.tradePopulationLive.delete(payment1.getTcn()));
        Assertions.assertEquals(0, this.tradePopulationLive.getTradeCount());
        Assertions.assertEquals(0, this.tradePopulationLive.getByTradeType(TradeType.Payment).size());
    }

    @Test
    void multipleTrades() {
        Payment payment1 = TradeGenerators.createPayment(getRandomBook());
        Payment payment2 = TradeGenerators.createPayment(getRandomBook());
        Payment payment3 = TradeGenerators.createPayment(getRandomBook());
        Spot spot1 = TradeGenerators.createSpot(getRandomBook());
        Spot spot2 = TradeGenerators.createSpot(getRandomBook());
        Spot spot3 = TradeGenerators.createSpot(getRandomBook());

        this.tradePopulationLive.add(payment1);
        this.tradePopulationLive.add(payment2);
        this.tradePopulationLive.add(payment3);
        this.tradePopulationLive.add(spot1);
        this.tradePopulationLive.add(spot2);
        this.tradePopulationLive.add(spot3);

        Assertions.assertEquals(6, this.tradePopulationLive.getTradeCount());
        Assertions.assertEquals(3, this.tradePopulationLive.getByTradeType(TradeType.Payment).size());
        Assertions.assertEquals(3, this.tradePopulationLive.getByTradeType(TradeType.Spot).size());

        this.tradePopulationLive.delete(payment1.getTcn());
        Assertions.assertEquals(5, this.tradePopulationLive.getTradeCount());
        Assertions.assertEquals(2, this.tradePopulationLive.getByTradeType(TradeType.Payment).size());
        Assertions.assertEquals(3, this.tradePopulationLive.getByTradeType(TradeType.Spot).size());

        this.tradePopulationLive.delete(spot1.getTcn());
        Assertions.assertEquals(4, this.tradePopulationLive.getTradeCount());
        Assertions.assertEquals(2, this.tradePopulationLive.getByTradeType(TradeType.Payment).size());
        Assertions.assertEquals(2, this.tradePopulationLive.getByTradeType(TradeType.Spot).size());

    }

}
