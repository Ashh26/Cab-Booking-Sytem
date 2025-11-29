package com.yasif.project.uber.Uber.backend.system.strategies.Impl;

import com.yasif.project.uber.Uber.backend.system.dto.RideRequestDto;
import com.yasif.project.uber.Uber.backend.system.strategies.RideFareCalculationStrategy;

public class RideFareSurgePricingFareCalculationStrategy implements RideFareCalculationStrategy {
    @Override
    public double calculateFare(RideRequestDto rideRequestDto) {
        return 0;
    }
}
