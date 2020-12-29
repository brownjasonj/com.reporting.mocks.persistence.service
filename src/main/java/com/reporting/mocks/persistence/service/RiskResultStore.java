package com.reporting.mocks.persistence.service;

import com.reporting.mocks.interfaces.persistence.IRiskResultStore;
import com.reporting.mocks.model.RiskResult;
import com.reporting.mocks.model.id.RiskRunId;
import com.reporting.mocks.model.risks.Risk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope
public class RiskResultStore implements IRiskResultStore {
    @Autowired
    RiskResultRepository riskResultRepository;

    @Override
    public List<RiskResult<? extends Risk>> getAll() {
        return this.riskResultRepository.findAll();
    }

    @Override
    public List<RiskResult<? extends Risk>> getAllByRiskRunId(RiskRunId riskRunId) {
        return this.riskResultRepository.getAllByRiskRunId(riskRunId);
    }

    @Override
    public RiskResult<? extends Risk> add(RiskResult<? extends Risk> riskResult) {
        return this.riskResultRepository.save(riskResult);
    }
}
