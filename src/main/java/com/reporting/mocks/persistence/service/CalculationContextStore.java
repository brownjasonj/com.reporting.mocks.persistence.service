package com.reporting.mocks.persistence.service;

import com.reporting.mocks.interfaces.persistence.ICalculationContextStore;
import com.reporting.mocks.model.CalculationContext;
import com.reporting.mocks.model.PricingGroup;
import com.reporting.mocks.model.id.CalculationContextId;

import java.util.Collection;
import java.util.UUID;

public class CalculationContextStore implements ICalculationContextStore {
    protected PricingGroup pricingGroup;
    protected CalculationContextRepository calculationContextRepository;
    protected CalculationContextId currentCalculationContextId;
    protected CalculationContextId previousCaclulationContextId;

    public CalculationContextStore(PricingGroup pricingGroup, CalculationContextRepository calculationContextRepository) {
        this.pricingGroup = pricingGroup;
        this.calculationContextRepository = calculationContextRepository;
        this.currentCalculationContextId = null;
        this.previousCaclulationContextId = null;
    }

    @Override
    public CalculationContext create() {
        return new CalculationContext(this.pricingGroup);
    }

    @Override
    public CalculationContext createCopy(CalculationContext calculationContextToCopy) {
        return new CalculationContext(calculationContextToCopy);
    }

    @Override
    public CalculationContext setCurrentContext(CalculationContext cc) {
        if (this.currentCalculationContextId == null) {
            this.previousCaclulationContextId = cc.getCalculationContextId();
        }
        else {
            this.previousCaclulationContextId = this.currentCalculationContextId;
        }
        this.currentCalculationContextId = cc.getCalculationContextId();
        return this.calculationContextRepository.save(cc);
    }

    @Override
    public CalculationContext getCurrentContext() {
        if (this.currentCalculationContextId == null)
            return null;
        else
            return this.get(this.currentCalculationContextId.getId());
    }

    @Override
    public CalculationContext getPreviousContext() {
        if (this.previousCaclulationContextId == null)
            return null;
        else
            return this.get(this.previousCaclulationContextId.getId());
    }

    @Override
    public CalculationContext getCalculationContextById(CalculationContextId calculationContextId) {
        return this.calculationContextRepository.findCalculationContextByCalculationContextId(calculationContextId);
    }

    @Override
    public CalculationContext get(UUID id) {
        return this.calculationContextRepository.findCalculationContextByCalculationContextId(new CalculationContextId(this.pricingGroup.getName(), id));
    }

    @Override
    public PricingGroup getPricingGroup() {
        return this.pricingGroup;
    }

    @Override
    public Collection<CalculationContext> getAll() {
        return this.calculationContextRepository.findCalculationContextByPricingGroup(this.pricingGroup);
    }
}
