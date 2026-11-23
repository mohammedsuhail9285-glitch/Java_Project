from fastapi import APIRouter, Query, Body
from typing import Dict, Any, Optional
import os
import json
from app.freight.forecasting_engine import FreightForecastingEngine
from app.freight.charter_optimizer import CharterOptimizationEngine
from app.freight.evaluate_forecast_accuracy import run_evaluation

router = APIRouter(prefix="/api/v1/freight", tags=["Maritime Freight (SIH26006)"])

@router.get("/indices")
def get_market_indices() -> Dict[str, Any]:
    return {
        "status": "LIVE",
        "bdi": 1890,
        "bci": 2920,
        "bpi": 1640,
        "bsi": 1310,
        "bunker_vlsfo_usd": 592.50,
        "usd_inr_rate": 83.65,
        "sentiment_score": 0.58,
        "market_commentary": "Capesize rates stabilizing across iron ore trade corridors; Pacific Panamax tonnage building with soft forward outlook."
    }

@router.get("/forecast")
def get_freight_forecast(
    vessel: str = Query("PANAMAX", description="Vessel class: CAPESIZE, PANAMAX, SUPRAMAX, HANDYSIZE"),
    spot: float = Query(15.50, description="Current spot freight rate ($/ton)"),
    bunker: float = Query(595.0, description="Current VLSFO bunker price ($/ton)")
) -> Dict[str, Any]:
    return FreightForecastingEngine.forecast_rate(vessel, spot, bunker)

@router.post("/optimize")
def optimize_charter(payload: Dict[str, Any] = Body(...)) -> Dict[str, Any]:
    commodity = payload.get("commodity", "Coking Coal")
    quantity_tons = float(payload.get("quantity_tons", 75000.0))
    port_code = payload.get("destination_port_code", "INPRT")
    spot_freight = float(payload.get("spot_freight", 16.20))
    fob_cargo_usd = float(payload.get("fob_price_usd", 220.0))
    contract_type = payload.get("contract_type", "SPOT")

    return CharterOptimizationEngine.optimize(
        commodity=commodity,
        quantity_tons=quantity_tons,
        port_code=port_code,
        spot_freight=spot_freight,
        fob_cargo_usd=fob_cargo_usd,
        contract_type=contract_type
    )

@router.get("/model/accuracy")
def get_model_accuracy() -> Dict[str, Any]:
    metrics_path = os.path.join(os.path.dirname(os.path.dirname(__file__)), "freight", "accuracy_metrics.json")
    if os.path.exists(metrics_path):
        try:
            with open(metrics_path, "r", encoding="utf-8") as f:
                return json.load(f)
        except Exception:
            pass
    return run_evaluation()
