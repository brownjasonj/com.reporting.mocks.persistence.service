package com.reporting.mocks.persistence.service;

import com.reporting.mocks.model.trade.TradeTypes.Payment;
import com.reporting.mocks.model.trade.TradeTypes.Spot;
import com.reporting.mocks.model.underlying.Underlying;

import java.time.Instant;
import java.util.Random;

public class TradeGenerators {

    public static Payment createPayment(String tradingBook, Instant asOf) {
        Random rand = new Random();
        return new Payment(
                tradingBook,
                rand.nextDouble(),
                new Underlying("EUR"),
                asOf
        );
    }

    public static Payment createPayment(String tradingBook) {
        Instant asOf = Instant.now();
        return TradeGenerators.createPayment(tradingBook, asOf);
    }

    public static Spot createSpot(String tradingBook, Instant asOf) {
        Random rand = new Random();

        return new Spot(
                tradingBook,
                rand.nextDouble(),
                new Underlying("EUR"),
                new Underlying("USD"),
                asOf,
                rand.nextDouble()
        );
    }

    public static Spot createSpot(String tradingBook) {
        Instant asOf = Instant.now();
        return TradeGenerators.createSpot(tradingBook, asOf);
    }
}
