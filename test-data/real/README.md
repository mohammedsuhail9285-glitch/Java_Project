# Real-World Packaged Commodity Testing Dataset

This folder is reserved for evaluating RegTech Sentinel against real-world smartphone photos of retail packaging labels.

## How to Test Real Labels:
1. Capture or save photos of physical packaging labels from retail grocery, cosmetics, or electronics (e.g. `real_biscuit.jpg`, `real_oil.png`).
2. Place the image files in this directory: `test-data/real/`.
3. Add an entry for each image in `test-data/real/expected.json` with ground truth statutory values:
   ```json
   {
     "real_biscuit.jpg": {
       "mrp": "30.00",
       "net_qty": "200 g",
       "manufacturer": "Britannia Industries Ltd",
       "mfg_date": "05/2026"
     }
   }
   ```
4. Run `powershell -ExecutionPolicy Bypass -File .\run_accuracy_eval.ps1`.
   The evaluation engine automatically detects your real images and reports separate accuracy metrics for synthetic vs real-world datasets.
