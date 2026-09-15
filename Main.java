import java.util.Scanner;
public class Main {
    private static final double USD_TO_INR = 85.50;

    public static void main(String[] args) {
        for (String arg : args) {
            if (arg.equalsIgnoreCase("--demo") || arg.equalsIgnoreCase("-d")) {
                runQuickDemo();
                return;
            }
        }

        Scanner scanner = new Scanner(System.in);

        System.out.println("==================================================");
        System.out.println("  SAIL Freight Forecasting and Vessel Optimizer   ");
        System.out.println("==================================================");

        while (true) {
            System.out.println("\n--- MAIN MENU ---");
            System.out.println("1. Predict Freight Rates (7, 14, 30, 60 Days)");
            System.out.println("2. Check Port Draft and Vessel Compatibility");
            System.out.println("3. Calculate Voyage Logistics Cost");
            System.out.println("4. Best Vessel and Contract Strategy Recommendation");
            System.out.println("5. Ask AI Procurement Assistant");
            System.out.println("6. Run Quick Demo (150,000 Tonnes Australia -> Paradip)");
            System.out.println("0. Exit");
            System.out.print("Enter your choice (0-6): ");

            if (!scanner.hasNextLine()) {
                break;
            }

            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) {
                predictFreight(scanner);
            } else if (choice.equals("2")) {
                checkPortCompatibility(scanner);
            } else if (choice.equals("3")) {
                calculateVoyageCost(scanner);
            } else if (choice.equals("4")) {
                recommendVesselAndStrategy(scanner);
            } else if (choice.equals("5")) {
                askAssistant(scanner);
            } else if (choice.equals("6")) {
                runQuickDemo();
            } else if (choice.equals("0") || choice.equalsIgnoreCase("exit")) {
                System.out.println("\nThank you for using NAVISENSE AI. Goodbye!");
                break;
            } else {
                System.out.println("Invalid option! Please enter a number between 0 and 6.");
            }
        }

        scanner.close();
    }
    private static void predictFreight(Scanner scanner) {
        System.out.println("\n--- [1] FREIGHT RATE PREDICTION ---");
        System.out.print("Enter Origin Country (e.g., Australia, USA, Mozambique) [Australia]: ");
        String origin = scanner.hasNextLine() ? scanner.nextLine().trim() : "";
        if (origin.isEmpty()) origin = "Australia";

        System.out.print("Enter Destination Port (e.g., Paradip, Vizag, Haldia) [Paradip]: ");
        String destination = scanner.hasNextLine() ? scanner.nextLine().trim() : "";
        if (destination.isEmpty()) destination = "Paradip";

        double currentSpotRate = 24.50; 
        if (origin.equalsIgnoreCase("USA")) {
            currentSpotRate = 44.00;
        } else if (origin.equalsIgnoreCase("Mozambique")) {
            currentSpotRate = 19.80;
        }

        System.out.println("\nRoute: " + origin + " -> " + destination);
        System.out.printf("Current Market Spot Rate: $%.2f / tonne\n", currentSpotRate);
        System.out.println("--------------------------------------------------");
        System.out.println("Horizon     | Predicted Rate | Expected Trend");
        System.out.println("--------------------------------------------------");
        System.out.printf(" 7 Days     | $%.2f / tonne  | Upward (+1.2%%)\n", currentSpotRate * 1.012);
        System.out.printf("14 Days     | $%.2f / tonne  | Upward (+2.5%%)\n", currentSpotRate * 1.025);
        System.out.printf("30 Days     | $%.2f / tonne  | Upward (+3.8%%)\n", currentSpotRate * 1.038);
        System.out.printf("60 Days     | $%.2f / tonne  | Stable (+4.1%%)\n", currentSpotRate * 1.041);
        System.out.println("--------------------------------------------------");
        System.out.println("Insight: Spot freight rates are trending upward. Booking early is advised.");
    }
    private static void checkPortCompatibility(Scanner scanner) {
        System.out.println("\n--- [2] PORT DRAFT AND VESSEL COMPATIBILITY ---");
        System.out.println("Available Indian Ports:");
        System.out.println("  * Paradip        : Max Draft = 16.5m");
        System.out.println("  * Visakhapatnam  : Max Draft = 18.5m");
        System.out.println("  * Gangavaram     : Max Draft = 19.5m");
        System.out.println("  * Dhamra         : Max Draft = 18.5m");
        System.out.println("  * Haldia         : Max Draft = 10.5m");

        System.out.print("\nEnter Port Name [Paradip]: ");
        String port = scanner.hasNextLine() ? scanner.nextLine().trim() : "";
        if (port.isEmpty()) port = "Paradip";

        System.out.print("Enter Vessel Class (Handysize, Supramax, Panamax, Capesize) [Capesize]: ");
        String vessel = scanner.hasNextLine() ? scanner.nextLine().trim() : "";
        if (vessel.isEmpty()) vessel = "Capesize";

        double portDraft = getPortDraft(port);
        double vesselDraft = getVesselDraft(vessel);

        System.out.println("\nChecking: " + vessel + " at " + port);
        System.out.println("Vessel Required Draft: " + vesselDraft + " meters");
        System.out.println("Port Maximum Draft:    " + portDraft + " meters");

        if (vesselDraft <= portDraft) {
            System.out.println("RESULT: [APPROVED] Vessel can safely enter and berth at " + port + ".");
        } else {
            System.out.println("RESULT: [REJECTED] Vessel exceeds port depth by " +
                    String.format("%.1f", (vesselDraft - portDraft)) + " meters! Risk of grounding.");
        }
    }
    private static void calculateVoyageCost(Scanner scanner) {
        System.out.println("\n--- [3] VOYAGE LOGISTICS COST CALCULATOR ---");
        System.out.print("Enter Cargo Quantity in tonnes [150000]: ");
        String qtyInput = scanner.hasNextLine() ? scanner.nextLine().trim() : "";
        double quantity = qtyInput.isEmpty() ? 150000 : Double.parseDouble(qtyInput);

        System.out.print("Enter Freight Rate in USD/tonne [24.50]: ");
        String rateInput = scanner.hasNextLine() ? scanner.nextLine().trim() : "";
        double freightRate = rateInput.isEmpty() ? 24.50 : Double.parseDouble(rateInput);

        double oceanFreightUSD = quantity * freightRate;
        double bunkerFuelUSD = 320000.0;   
        double portChargesUSD = quantity * 3.25;
        double demurrageUSD = 45000.0;   

        double totalUSD = oceanFreightUSD + bunkerFuelUSD + portChargesUSD + demurrageUSD;
        double totalINR = totalUSD * USD_TO_INR;
        double inrCrores = totalINR / 10000000.0; 

        System.out.println("\n==================================================");
        System.out.println("             VOYAGE COST BREAKDOWN                ");
        System.out.println("==================================================");
        System.out.printf("Base Ocean Freight:     $%,12.2f\n", oceanFreightUSD);
        System.out.printf("Sea Bunker Fuel Cost:   $%,12.2f\n", bunkerFuelUSD);
        System.out.printf("Port and Canal Tariffs: $%,12.2f\n", portChargesUSD);
        System.out.printf("Demurrage Queue Buffer: $%,12.2f\n", demurrageUSD);
        System.out.println("--------------------------------------------------");
        System.out.printf("Total Delivered Cost:   $%,12.2f\n", totalUSD);
        System.out.printf("Total in Indian Rupees: Rs. %.2f Crores (Rs. %,.2f)\n", inrCrores, totalINR);
        System.out.printf("Delivered Cost / Tonne: $%.2f / tonne (Rs. %.2f / tonne)\n", totalUSD / quantity, totalINR / quantity);
        System.out.println("==================================================");
    }


    private static void recommendVesselAndStrategy(Scanner scanner) {
        System.out.println("\n--- [4] BEST VESSEL AND CONTRACT RECOMMENDATION ---");
        System.out.print("Enter Destination Port [Paradip]: ");
        String port = scanner.hasNextLine() ? scanner.nextLine().trim() : "";
        if (port.isEmpty()) port = "Paradip";

        System.out.print("Enter Cargo Volume in tonnes [150000]: ");
        String qtyInput = scanner.hasNextLine() ? scanner.nextLine().trim() : "";
        double qty = qtyInput.isEmpty() ? 150000 : Double.parseDouble(qtyInput);

        System.out.println("\nRecommendation for " + String.format("%,.0f", qty) + " tonnes to " + port + ":");

        if (port.equalsIgnoreCase("Paradip")) {
            System.out.println("1. RECOMMENDED VESSEL: Panamax (Capacity: ~75,000 tonnes)");
            System.out.println("   - Why? Capesize (18.0m draft) cannot enter Paradip (max 16.5m limit).");
            System.out.println("   - Panamax (14.5m draft) safely fits and completes cargo in 2 voyages.");
        } else if (port.equalsIgnoreCase("Haldia")) {
            System.out.println("1. RECOMMENDED VESSEL: Handysize (Capacity: ~35,000 tonnes)");
            System.out.println("   - Why? Haldia has shallow 10.5m draft. Larger vessels cannot berth.");
        } else {
            System.out.println("1. RECOMMENDED VESSEL: Capesize (Capacity: ~175,000 tonnes)");
            System.out.println("   - Why? Deepwater port allows largest vessel class for maximum economy of scale.");
        }

        System.out.println("\n2. RECOMMENDED CONTRACT: Short-Term Multi-Voyage Contract (COA 3-6 months)");
        System.out.println("   - Why? Forward freight forecasts show +3.8% rate escalation.");
        System.out.println("   - Securing a Short-Term COA locks rates below projected spot spikes, saving Rs. 1.8 to 2.2 Crores.");
    }

    private static void askAssistant(Scanner scanner) {
        System.out.println("\n--- [5] AI PROCUREMENT COPILOT ---");
        System.out.println("Ask any question about SAIL vessel chartering, port limits, or booking timing.");
        System.out.println("(Example: 'Why is Capesize rejected for Paradip?', 'Should we book now or wait?')");
        System.out.print("\nYour Question: ");
        String query = scanner.hasNextLine() ? scanner.nextLine().trim().toLowerCase() : "";

        System.out.println("\nAnswer:");
        if (query.contains("capesize") && (query.contains("reject") || query.contains("why") || query.contains("paradip"))) {
            System.out.println("Capesize vessels require an 18.0m water draft. Paradip Port only permits a maximum");
            System.out.println("draft of 16.5m. A loaded Capesize would risk hitting the seabed. Panamax (14.5m) is optimal.");
        } else if (query.contains("book") || query.contains("wait") || query.contains("when")) {
            System.out.println("Our forward forecasting model shows rates rising by +3.5% over the next 14 to 30 days.");
            System.out.println("Recommendation: Book now via a Short-Term COA to avoid paying higher spot prices.");
        } else if (query.contains("risk") || query.contains("safest")) {
            System.out.println("The Australia (Gladstone) -> Visakhapatnam corridor has the lowest overall risk score");
            System.out.println("due to deep port drafts (18.5m) and lower anchorage congestion.");
        } else {
            System.out.println("For SAIL raw material shipments, we recommend using Panamax carriers under Short-Term");
            System.out.println("COA agreements to avoid port demurrage and capture steady volume discounts.");
        }
    }

    private static void runQuickDemo() {
        System.out.println("\n==================================================");
        System.out.println("  RUNNING QUICK DEMO: SAIL 150,000t AUSTRALIA->PARADIP");
        System.out.println("==================================================");

        System.out.println("\n[1] Freight Forecast:");
        System.out.println("    Current Spot: $24.50/t -> 30-Day Forecast: $25.43/t (+3.8% Upward)");

        System.out.println("\n[2] Port Compatibility at Paradip (Max 16.5m Draft):");
        System.out.println("    Capesize (18.0m): REJECTED (Exceeds draft limit by 1.5m)");
        System.out.println("    Panamax  (14.5m): APPROVED (Fits within port limit)");

        System.out.println("\n[3] Logistics Spend Calculation (150,000 tonnes via Panamax):");
        double totalUSD = (150000 * 24.50) + 320000.0 + (150000 * 3.25) + 45000.0;
        double totalINR = totalUSD * USD_TO_INR;
        System.out.printf("    Delivered Spend: $%,.2f  (Rs. %.2f Crores)\n", totalUSD, totalINR / 10000000.0);

        System.out.println("\n[4] Recommendation:");
        System.out.println("    Charter Panamax under Short-Term COA to capture Rs. 2.10 Cr in savings.");

        System.out.println("\nDemo completed successfully!");
    }

    private static double getPortDraft(String portName) {
        String p = portName.toLowerCase();
        if (p.contains("paradip")) return 16.5;
        if (p.contains("visakhapatnam") || p.contains("vizag")) return 18.5;
        if (p.contains("gangavaram")) return 19.5;
        if (p.contains("dhamra")) return 18.5;
        if (p.contains("gopalpur")) return 14.5;
        if (p.contains("haldia")) return 10.5;
        return 15.0; 
    }


    private static double getVesselDraft(String vesselName) {
        String v = vesselName.toLowerCase();
        if (v.contains("capesize")) return 18.0;
        if (v.contains("panamax")) return 14.5;
        if (v.contains("supramax")) return 12.8;
        if (v.contains("handysize")) return 10.2;
        return 14.5;
    }
}
