package com.example.lmchecker.dto;

import com.example.lmchecker.entity.FreightMarketData;
import com.example.lmchecker.entity.MaritimePort;
import com.example.lmchecker.entity.ProcurementPlan;

import java.util.List;

public class FreightOverviewDto {

    private FreightMarketData marketData;
    private long totalRoutes;
    private long totalPorts;
    private long activeProcurementPlans;
    private double totalOptimizedSavingsInr;
    private List<MaritimePort> eastCoastPorts;
    private List<ProcurementPlan> recentPlans;

    public FreightOverviewDto() {}

    public FreightMarketData getMarketData() { return marketData; }
    public void setMarketData(FreightMarketData marketData) { this.marketData = marketData; }

    public long getTotalRoutes() { return totalRoutes; }
    public void setTotalRoutes(long totalRoutes) { this.totalRoutes = totalRoutes; }

    public long getTotalPorts() { return totalPorts; }
    public void setTotalPorts(long totalPorts) { this.totalPorts = totalPorts; }

    public long getActiveProcurementPlans() { return activeProcurementPlans; }
    public void setActiveProcurementPlans(long activeProcurementPlans) { this.activeProcurementPlans = activeProcurementPlans; }

    public double getTotalOptimizedSavingsInr() { return totalOptimizedSavingsInr; }
    public void setTotalOptimizedSavingsInr(double totalOptimizedSavingsInr) { this.totalOptimizedSavingsInr = totalOptimizedSavingsInr; }

    public List<MaritimePort> getEastCoastPorts() { return eastCoastPorts; }
    public void setEastCoastPorts(List<MaritimePort> eastCoastPorts) { this.eastCoastPorts = eastCoastPorts; }

    public List<ProcurementPlan> getRecentPlans() { return recentPlans; }
    public void setRecentPlans(List<ProcurementPlan> recentPlans) { this.recentPlans = recentPlans; }
}

