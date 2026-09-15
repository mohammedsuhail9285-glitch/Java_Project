package com.sih.navisense.dto;

public class EvaluationMetricsDto {
    private double mae;  // Mean Absolute Error
    private double rmse; // Root Mean Squared Error
    private double mape; // Mean Absolute Percentage Error (%)
    private int sampleCount;
    private String modelName;

    public EvaluationMetricsDto() {}

    public EvaluationMetricsDto(double mae, double rmse, double mape, int sampleCount, String modelName) {
        this.mae = Math.round(mae * 100.0) / 100.0;
        this.rmse = Math.round(rmse * 100.0) / 100.0;
        this.mape = Math.round(mape * 100.0) / 100.0;
        this.sampleCount = sampleCount;
        this.modelName = modelName;
    }

    public double getMae() { return mae; }
    public void setMae(double mae) { this.mae = mae; }

    public double getRmse() { return rmse; }
    public void setRmse(double rmse) { this.rmse = rmse; }

    public double getMape() { return mape; }
    public void setMape(double mape) { this.mape = mape; }

    public int getSampleCount() { return sampleCount; }
    public void setSampleCount(int sampleCount) { this.sampleCount = sampleCount; }

    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }
}
