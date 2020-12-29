package com.reporting.mocks.persistence.service.repository;

import com.google.gson.Gson;
import com.reporting.mocks.model.trade.Tcn;
import com.reporting.mocks.model.trade.Trade;
import com.reporting.mocks.model.trade.TradeType;
import com.reporting.mocks.model.trade.TradeTypes.Payment;
import com.reporting.mocks.model.trade.TradeTypes.Spot;
import com.reporting.mocks.model.underlying.Underlying;
import com.reporting.mocks.persistence.service.TradeGenerators;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.UUID;


public class TradeAssociationRepositoryTests {
    public TradeAssociationRepository tradeAssociationRepository;
    public Gson gson;
    public String tradingBook;

    @BeforeEach
    void setUpTradeAssociationRepository() {
        this.tradeAssociationRepository = new TradeAssociationRepository("http://localhost:8080");
        this.tradingBook = UUID.randomUUID().toString();
        this.gson = new Gson();
    }

    @Test
    void saveTrade() {
        Random rand = new Random();
        Instant asOf = Instant.now();
        Payment payment = TradeGenerators.createPayment(this.tradingBook, asOf);

        Trade trade = this.tradeAssociationRepository.save(payment, asOf);

        Assertions.assertNotNull(trade);
        Assertions.assertEquals(payment.getTcn().getId(), trade.getTcn().getId());
        System.out.println(gson.toJson(trade));
    }

    @Test
    void saveTradeDeleteTrade() {
        Random rand = new Random();
        Instant asOf = Instant.now();
        Payment payment = TradeGenerators.createPayment(this.tradingBook, asOf);

        Trade trade = this.tradeAssociationRepository.save(payment, asOf);

        Assertions.assertNotNull(trade);
        Assertions.assertEquals(payment.getTcn().getId(), trade.getTcn().getId());

        Boolean deleteSuccessful = this.tradeAssociationRepository.delete(payment.getTcn(), Instant.now());

        Assertions.assertTrue(deleteSuccessful);
    }


    @Test
    void getSetOfTrades() throws InterruptedException {
        Instant asOf = Instant.now();
        Payment payment1 = TradeGenerators.createPayment(this.tradingBook, asOf);

        Trade trade1 = this.tradeAssociationRepository.save(payment1, asOf);

        asOf = Instant.now();
        Payment payment2 = TradeGenerators.createPayment(this.tradingBook, asOf);

        Trade trade2 = this.tradeAssociationRepository.save(payment2, asOf);

        Thread.sleep(3000);

        List<Trade> trades = this.tradeAssociationRepository.getTradesByTradingBook(this.tradingBook, Instant.now());

        // Assertions.assertEquals(2, trades.size());
    }

    @Test
    void getSetofTradesByTradeType() throws InterruptedException {
        Instant asOf = Instant.now();
        Payment payment1 = TradeGenerators.createPayment(this.tradingBook, asOf);

        Trade trade1 = this.tradeAssociationRepository.save(payment1, asOf);

        asOf = Instant.now();
        Payment payment2 = TradeGenerators.createPayment(this.tradingBook, asOf);

        Trade trade2 = this.tradeAssociationRepository.save(payment2, asOf);

        /*
        Double underlyingAmount1, Underlying underlying1, Underlying underlying2, Instant settlementDate, Double price
         */
        Spot spot1 = TradeGenerators.createSpot(this.tradingBook, asOf);

        Trade trade3 = this.tradeAssociationRepository.save(spot1, asOf);

        Thread.sleep(200);

        List<Trade> trades1 = this.tradeAssociationRepository.getByTradeType(this.tradingBook, TradeType.Payment, Instant.now());

        Assertions.assertEquals(2, trades1.size());

        List<Trade> trades2 = this.tradeAssociationRepository.getByTradeType(this.tradingBook, TradeType.Spot, Instant.now());
        Assertions.assertEquals(1, trades2.size());
    }

    @Test
    void getTradeByTcn() throws InterruptedException {
        Instant asOf = Instant.now();
        Payment payment1 = TradeGenerators.createPayment(this.tradingBook, asOf);

        Trade trade1 = this.tradeAssociationRepository.save(payment1, asOf);

        Thread.sleep(200);

        Trade trade2 = this.tradeAssociationRepository.getTradeByTcn(trade1.getTcn(), Instant.now());
        Assertions.assertNotNull(trade2);
        Assertions.assertEquals(trade1.getTcn(), trade2.getTcn());
    }
}
