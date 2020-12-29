package com.reporting.mocks.persistence.service;

import com.reporting.mocks.model.RiskResult;
import com.reporting.mocks.model.id.RiskRunId;
import com.reporting.mocks.model.risks.Risk;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RiskResultRepository extends MongoRepository<RiskResult<? extends Risk>,RiskRunId> {
    List<RiskResult<? extends Risk>> getAllByRiskRunId(RiskRunId riskRunId);
}
