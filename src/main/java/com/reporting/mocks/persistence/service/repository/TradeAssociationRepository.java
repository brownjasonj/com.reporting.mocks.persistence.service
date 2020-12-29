package com.reporting.mocks.persistence.service.repository;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.reporting.mocks.model.trade.Tcn;
import com.reporting.mocks.model.trade.Trade;
import com.reporting.mocks.model.trade.TradeType;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class TradeAssociationRepository {
    private final String serviceUrl;
    private HttpClient httpClient;
    private final String transactionUrlPath = "/transaction";
    private final String associationUrlPath = "/association";
    private final String associationTransactionUrlPath = "/association-transaction";
    private final String transactionUrl;
    private final String associationUrl;
    private final String associationTransactionUrl;
    private final String tradingBookTag = "trading-book";
    private Gson gson;


    public TradeAssociationRepository(String serviceUrl) {
        this.serviceUrl = serviceUrl;
        this.httpClient = HttpClient.newHttpClient();
        this.transactionUrl = serviceUrl + this.transactionUrlPath;
        this.associationUrl = serviceUrl + this.associationUrlPath;
        this.associationTransactionUrl = serviceUrl + this.associationTransactionUrlPath;

        this.gson = new Gson();
    }

    /*
        save does the following steps
        (1) covert trade to a json object, then add keys 'id' and 'valid_from' to the object
        (2) create an association object that has
                a type of 'trading-book'
                a references type of 'transaction'
                the value set to the id of the trade
                a valid-from the same time as the trade
         (3) PUT the modified trade json object from (1)
         (4) PUT the association object
     */
    public Trade save(Trade trade, Instant asOf) {
        try {
            Trade returnTrade = null;
            String tradingBookName = trade.getBook();

            String createAssociationTransactionURL = associationTransactionUrl
                    + "?valid-from=" + asOf.toString();

            Association association = new Association(tradingBookTag, tradingBookName);
            AssociationTransacation associationTransacation = new AssociationTransacation(trade);
            associationTransacation.addAssociation(association);


            HttpRequest associationTransactionRequest = HttpRequest.newBuilder()
                    .uri(URI.create(createAssociationTransactionURL))
                    .timeout(Duration.ofMinutes(1))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(gson.toJson(associationTransacation)))
                    .build();

            HttpResponse<String> transactionResponse =
                    this.httpClient.send(associationTransactionRequest, HttpResponse.BodyHandlers.ofString());
//            System.out.println(transactionResponse.statusCode());
//            System.out.println(transactionResponse.body());

            if (transactionResponse.statusCode() == 200) {
                returnTrade = gson.fromJson(transactionResponse.body(), Trade.class);
            }

            return returnTrade;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
        dalete does the following

        (1) deletes the transaction
        (2) deletes all associations to the transactions
     */
    public Boolean delete(Tcn tcn, Instant asOf) {
        Boolean deleteSuccessful = false;

        String deleteTransactionUrl = this.transactionUrl + "?"
                + "id=" + tcn.getId().toString();

        HttpRequest transactionRequest = HttpRequest.newBuilder()
                .uri(URI.create(deleteTransactionUrl))
                .timeout(Duration.ofMinutes(1))
                .header("Content-Type", "application/json")
                .DELETE()
                .build();


        String associationRequestUrl = this.associationUrl + "?"
                + "type="+tradingBookTag
                + "&reftype=transaction"
                + "&refvalue=" + tcn.getId().toString();

        HttpRequest associationRequest = HttpRequest.newBuilder()
                .uri(URI.create(associationRequestUrl))
                .timeout(Duration.ofMinutes(1))
                .header("Content-Type", "application/json")
                .DELETE()
                .build();

        HttpResponse<String> associationResponse =
                null;
        try {
            associationResponse = this.httpClient.send(associationRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }

//        System.out.println(associationResponse.statusCode());
//        System.out.println(associationResponse.body());


        HttpResponse<String> transactionResponse =
                null;
        try {
            transactionResponse = this.httpClient.send(transactionRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }

//        System.out.println(transactionResponse.statusCode());
//        System.out.println(transactionResponse.body());

        return ((transactionResponse.statusCode() == 200)
                && (associationResponse.statusCode() == 200));
    }

    public Trade getTradeByTcn(Tcn tcn, Instant asOf) {
        String transactionUri = this.transactionUrl + "?"
                + "valid-from=" + asOf.toString()
                + "&id=" + tcn.getId().toString();

        HttpRequest transactionRequest = HttpRequest.newBuilder()
                .uri(URI.create(transactionUri))
                .timeout(Duration.ofMinutes(1))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> transactionResponse =
                null;
        try {
            transactionResponse = this.httpClient.send(transactionRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        System.out.println(transactionResponse.statusCode());
//        System.out.println(transactionResponse.body());

        if (transactionResponse.body().isEmpty()) {
            return null;
        }
        else {
            Type listType = new TypeToken<ArrayList<Trade>>(){}.getType();
            List<Trade> trades = gson.fromJson(transactionResponse.body(), listType);
            // The service end-point always returns a list, but in this case of requesting a single
            // Tcn the list should have length 1, if not then return null as something went wrong
            if (trades.size() == 1)
                return trades.get(0);
            else
                return null;
        }
    }

    public List<Trade> getTradesByTradingBook(String tradingBook, Instant asOf) {
        String associationTransactionUri = this.associationTransactionUrl + "?"
                + "valid-from=" + asOf.toString();

        AssociationTransactionQuery query = new AssociationTransactionQuery();
        query.addAssociationConstraint("type", this.tradingBookTag);
        query.addAssociationConstraint("value", tradingBook);

        HttpRequest associationTransactionRequest = HttpRequest.newBuilder()
                .uri(URI.create(associationTransactionUri))
                .timeout(Duration.ofMinutes(1))
                .header("Content-Type", "application/json")
                .method("GET", HttpRequest.BodyPublishers.ofString(gson.toJson(query)))
                .build();

        HttpResponse<String> transactionResponse =
                null;
        try {
            transactionResponse = this.httpClient.send(associationTransactionRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        System.out.println(transactionResponse.statusCode());
//        System.out.println(transactionResponse.body());

        Type listType = new TypeToken<ArrayList<Trade>>(){}.getType();
        return gson.fromJson(transactionResponse.body(), listType);
    }

    /*
        Gets trades by trade type in a given trading book

     */
    public List<Trade> getByTradeType(String tradingBook, TradeType tradetype, Instant asOf) {
        String associationTransactionUri = this.associationTransactionUrl + "?"
                + "valid-from=" + asOf.toString();

        AssociationTransactionQuery query = new AssociationTransactionQuery();
        query.addTransactionConstraint("tradeType", tradetype.name());

        query.addAssociationConstraint("type", this.tradingBookTag);
        query.addAssociationConstraint("value", tradingBook);

        HttpRequest associationTransactionRequest = HttpRequest.newBuilder()
                .uri(URI.create(associationTransactionUri))
                .timeout(Duration.ofMinutes(1))
                .header("Content-Type", "application/json")
                .method("GET", HttpRequest.BodyPublishers.ofString(gson.toJson(query)))
                .build();

        HttpResponse<String> transactionResponse =
                null;
        try {
            transactionResponse = this.httpClient.send(associationTransactionRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        System.out.println(transactionResponse.statusCode());
//        System.out.println(transactionResponse.body());

        Type listType = new TypeToken<ArrayList<Trade>>(){}.getType();
        return gson.fromJson(transactionResponse.body(), listType);
    }
}
