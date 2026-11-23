import os
import sys
import json
import re

# Add ai-service to sys.path so we can import the production OCR and NLP engines directly
AI_SERVICE_DIR = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", "ai-service"))
if AI_SERVICE_DIR not in sys.path:
    sys.path.insert(0, AI_SERVICE_DIR)

from app.ocr.tesseract_engine import TesseractOCREngine
from app.nlp.field_extractor import FieldExtractor

TEST_DATA_DIR = os.path.dirname(os.path.abspath(__file__))
SYNTHETIC_DIR = os.path.join(TEST_DATA_DIR, "synthetic")
REAL_DIR = os.path.join(TEST_DATA_DIR, "real")

def evaluate_dataset(dataset_name, dataset_dir, expected_file, evaluate_variants=False):
    if not os.path.exists(dataset_dir) or not os.path.exists(expected_file):
        return None

    with open(expected_file, "r", encoding="utf-8") as f:
        ground_truth = json.load(f)

    if not ground_truth:
        return {"images_count": 0, "status": "EMPTY"}

    base_truth = dict(ground_truth)
    test_files = []

    if evaluate_variants:
        all_files = sorted([f for f in os.listdir(dataset_dir) if f.lower().endswith((".png", ".jpg", ".jpeg", ".webp"))])
        for f in all_files:
            base_key = f
            variant_type = "Clean Baseline"
            for suffix, label in [("_blur", "Gaussian Blur"), ("_rotate", "8° Skew Rotation"), ("_noise", "Sensor Noise"), ("_contrast", "Low Contrast")]:
                if suffix in f:
                    base_key = f.replace(suffix, "")
                    variant_type = label
                    break
            if base_key in base_truth:
                test_files.append((f, base_truth[base_key], variant_type))
    else:
        for f, exp in ground_truth.items():
            test_files.append((f, exp, "Real Label"))

    field_metrics = {
        "mrp": {"correct": 0, "total": 0},
        "net_qty": {"correct": 0, "total": 0},
        "manufacturer": {"correct": 0, "total": 0},
        "mfg_date": {"correct": 0, "total": 0},
    }

    variant_metrics = {}
    failures = []
    results_table = []

    for image_name, expected, variant_type in test_files:
        if variant_type not in variant_metrics:
            variant_metrics[variant_type] = {"correct": 0, "total": 0}

        image_path = os.path.join(dataset_dir, image_name)
        if not os.path.exists(image_path):
            continue

        with open(image_path, "rb") as f:
            img_bytes = f.read()

        # Run Real Tesseract OCR
        ocr_result = TesseractOCREngine.extract_text(img_bytes, filename=image_name, lang="eng")
        raw_text = getattr(ocr_result, "raw_text", "")
        ocr_lines = getattr(ocr_result, "lines", [])

        # Run NLP Field Extraction
        nlp_result = FieldExtractor.extract_fields(raw_text, ocr_lines=ocr_lines)
        detected_fields = {f.field_type: f.raw_value for f in nlp_result.fields}

        # 1. MRP Check
        exp_mrp = expected.get("mrp")
        det_mrp = detected_fields.get("MRP")
        mrp_correct = False
        field_metrics["mrp"]["total"] += 1
        if exp_mrp is None:
            if det_mrp is None or det_mrp == "":
                mrp_correct = True
        else:
            if det_mrp:
                try:
                    exp_num = float(re.sub(r"[^\d.]", "", exp_mrp))
                    det_num = float(re.sub(r"[^\d.]", "", det_mrp))
                    if abs(exp_num - det_num) < 0.01:
                        mrp_correct = True
                except (ValueError, TypeError):
                    if exp_mrp in det_mrp:
                        mrp_correct = True
        if mrp_correct:
            field_metrics["mrp"]["correct"] += 1
            variant_metrics[variant_type]["correct"] += 1
        else:
            failures.append((image_name, variant_type, "MRP", str(exp_mrp), str(det_mrp)))
        variant_metrics[variant_type]["total"] += 1

        # 2. Net Quantity Check
        exp_qty = expected.get("net_qty")
        det_qty = detected_fields.get("NET_QTY")
        qty_correct = False
        field_metrics["net_qty"]["total"] += 1
        if exp_qty and det_qty:
            exp_norm = re.sub(r"\s+", "", exp_qty.lower().replace("gm", "g").replace("gms", "g"))
            det_norm = re.sub(r"\s+", "", det_qty.lower().replace("gm", "g").replace("gms", "g"))
            if exp_norm == det_norm or exp_qty.split()[0] in det_qty:
                qty_correct = True
        if qty_correct:
            field_metrics["net_qty"]["correct"] += 1
            variant_metrics[variant_type]["correct"] += 1
        else:
            failures.append((image_name, variant_type, "Net Qty", str(exp_qty), str(det_qty)))
        variant_metrics[variant_type]["total"] += 1

        # 3. Manufacturer Check
        exp_mfg = expected.get("manufacturer")
        det_mfg = detected_fields.get("MANUFACTURER")
        mfg_correct = False
        field_metrics["manufacturer"]["total"] += 1
        if exp_mfg and det_mfg:
            keywords = [w.lower() for w in exp_mfg.split() if len(w) > 3]
            det_lower = det_mfg.lower()
            if any(k in det_lower for k in keywords):
                mfg_correct = True
        if mfg_correct:
            field_metrics["manufacturer"]["correct"] += 1
            variant_metrics[variant_type]["correct"] += 1
        else:
            failures.append((image_name, variant_type, "Manufacturer", str(exp_mfg), str(det_mfg)))
        variant_metrics[variant_type]["total"] += 1

        # 4. Mfg Date Check
        exp_date = expected.get("mfg_date")
        det_date = detected_fields.get("MFG_DATE")
        date_correct = False
        field_metrics["mfg_date"]["total"] += 1
        if exp_date and det_date and exp_date in det_date:
            date_correct = True
        if date_correct:
            field_metrics["mfg_date"]["correct"] += 1
            variant_metrics[variant_type]["correct"] += 1
        else:
            failures.append((image_name, variant_type, "Mfg Date", str(exp_date), str(det_date)))
        variant_metrics[variant_type]["total"] += 1

        results_table.append({
            "image": image_name,
            "variant": variant_type,
            "mrp": (exp_mrp, det_mrp, mrp_correct),
            "qty": (exp_qty, det_qty, qty_correct),
            "mfg": (exp_mfg, det_mfg, mfg_correct),
            "date": (exp_date, det_date, date_correct)
        })

    return {
        "images_count": len(test_files),
        "results_table": results_table,
        "field_metrics": field_metrics,
        "variant_metrics": variant_metrics,
        "failures": failures,
        "status": "COMPLETED"
    }

def print_dataset_report(title, data):
    print("\n" + "=" * 80)
    print(f"  DATASET: {title}")
    print("=" * 80)

    if not data or data.get("status") == "EMPTY":
        print("  Status: Empty dataset. No test images currently loaded.")
        print("  To test: Add sample label images to test-data/real and update expected.json.")
        return

    # 1. Per-Image Table
    print("\n" + "-" * 115)
    print(f"{'Image Name':<32} | {'Variant Type':<18} | {'MRP':<12} | {'Net Qty':<12} | {'Mfg Name':<12} | {'Mfg Date':<12}")
    print("-" * 115)
    for row in data["results_table"]:
        mrp_s = "PASS" if row["mrp"][2] else "FAIL"
        qty_s = "PASS" if row["qty"][2] else "FAIL"
        mfg_s = "PASS" if row["mfg"][2] else "FAIL"
        date_s = "PASS" if row["date"][2] else "FAIL"
        print(f"{row['image']:<32} | {row['variant']:<18} | {mrp_s:<12} | {qty_s:<12} | {mfg_s:<12} | {date_s:<12}")
    print("-" * 115)

    # 2. Variant Performance Table
    if data["variant_metrics"]:
        print("\n" + "-" * 55)
        print(f"{'Condition / Distortion Variant':<30} | {'Field Accuracy':<20}")
        print("-" * 55)
        for var_name, counts in data["variant_metrics"].items():
            c, t = counts["correct"], counts["total"]
            pct = (c / t * 100.0) if t > 0 else 0.0
            print(f"{var_name:<30} | {c}/{t} ({pct:.1f}%)")
        print("-" * 55)

    # 3. Per-Field Summary Table
    print("\n" + "-" * 55)
    print(f"{'Statutory Field':<30} | {'Extraction Accuracy':<20}")
    print("-" * 55)
    total_c, total_t = 0, 0
    for field, counts in data["field_metrics"].items():
        c, t = counts["correct"], counts["total"]
        pct = (c / t * 100.0) if t > 0 else 0.0
        fname = field.replace("_", " ").upper()
        print(f"{fname:<30} | {c}/{t} ({pct:.1f}%)")
        total_c += c
        total_t += t
    overall_pct = (total_c / total_t * 100.0) if total_t > 0 else 0.0
    print("-" * 55)
    print(f"{'OVERALL DATASET ACCURACY':<30} | {total_c}/{total_t} ({overall_pct:.1f}%)")
    print("-" * 55)

    # 4. Discrepancy / Failure Details
    if data["failures"]:
        print("\n--- Failure / Discrepancy Log (Expected vs Detected) ---")
        for img, var, fld, exp, det in data["failures"]:
            print(f"  - [{var}] {img} -> {fld}: Expected '{exp}' | Detected '{det}'")
    else:
        print("\n  - Zero field discrepancies detected across this dataset.")

def main():
    print("=" * 80)
    print("  REGTECH SENTINEL - OCR & STATUTORY EXTRACTION RIGOROUS BENCHMARK")
    print("=" * 80)

    TesseractOCREngine.configure()
    if not TesseractOCREngine.is_available():
        print("[ERROR] Tesseract OCR engine not found or unavailable.")
        sys.exit(1)

    print(f"Active OCR Engine: Tesseract ({TesseractOCREngine.get_tesseract_cmd()})")

    # 1. Synthetic Dataset (Baseline + Distortion Variants)
    syn_expected = os.path.join(SYNTHETIC_DIR, "expected.json")
    syn_data = evaluate_dataset("Synthetic", SYNTHETIC_DIR, syn_expected, evaluate_variants=True)
    print_dataset_report("SYNTHETIC BENCHMARK (Clean Baseline + Blur, 8° Rotation, Noise & Contrast Variants)", syn_data)

    # 2. Real-World Dataset
    real_expected = os.path.join(REAL_DIR, "expected.json")
    real_data = evaluate_dataset("Real-World", REAL_DIR, real_expected, evaluate_variants=False)
    print_dataset_report("REAL-WORLD BENCHMARK (Smartphone Photos of Physical Retail Labels)", real_data)

    print("\n" + "=" * 80)
    print("  BENCHMARK COMPLETE")
    print("=" * 80 + "\n")

if __name__ == "__main__":
    main()
