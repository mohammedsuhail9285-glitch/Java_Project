package com.example.lmchecker.config;

import com.example.lmchecker.entity.Role;
import com.example.lmchecker.entity.User;
import com.example.lmchecker.repository.ProductRepository;
import com.example.lmchecker.repository.RuleRepository;
import com.example.lmchecker.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final RuleRepository ruleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, ProductRepository productRepository, RuleRepository ruleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.ruleRepository = ruleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        log.info("Initializing default users and sample data...");
        initUsers();
        initMockLabelImages();
        log.info("System initialization complete. Products loaded: {}, Rules loaded: {}",
            productRepository.count(), ruleRepository.count());
    }

    private void initUsers() {
        createOrUpdateUser("admin", "admin@consumer.gov.in", "Admin@123", "System Administrator", Role.ADMIN);
        createOrUpdateUser("regulator", "regulator@consumer.gov.in", "Regulator@123", "Senior Metrology Officer", Role.REGULATOR);
        createOrUpdateUser("reviewer", "reviewer@consumer.gov.in", "Reviewer@123", "Compliance Review Inspector", Role.REVIEWER);
        createOrUpdateUser("analyst", "analyst@consumer.gov.in", "Analyst@123", "Policy & Market Analyst", Role.ANALYST);
    }

    private void createOrUpdateUser(String username, String email, String rawPassword, String fullName, Role role) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            user = User.builder()
                .username(username)
                .email(email)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .fullName(fullName)
                .role(role)
                .enabled(true)
                .build();
            userRepository.save(user);
            log.info("Created user '{}' with role {}", username, role);
        } else {
            // Guarantee fresh password hash matches expected credentials
            user.setPasswordHash(passwordEncoder.encode(rawPassword));
            user.setEmail(email);
            user.setFullName(fullName);
            user.setRole(role);
            user.setEnabled(true);
            userRepository.save(user);
        }
    }

    /**
     * Generate sample package label images for local development if not already present.
     */
    private void initMockLabelImages() {
        File dir = new File("./storage/uploads");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        createMockLabel(new File(dir, "mock_label_atta_5kg.png"),
            "SWARNA SHAKTI WHOLE WHEAT ATTA",
            "Net Qty: 5 kg | MRP Rs. 320.00 (Incl. of all taxes)",
            "Mfg By: Swarna Shakti Consumer Ltd, Pune - 411001",
            "Country of Origin: India | Care: 1800-200-1122");

        createMockLabel(new File(dir, "mock_label_facewash_150ml.png"),
            "VEDIKA PURIFYING NEEM FACE WASH",
            "Net Vol: 150 ml. | MRP Rs. 185.00 (Incl. Taxes)",
            "Mfg By: Vedika Ayurveda & Naturals, Haridwar - 249401",
            "Customer Care: care@vedikaherbal.in (No phone found)");

        createMockLabel(new File(dir, "mock_label_sheetmask_25g.png"),
            "APEX GLOW HYDRATING SHEET MASK",
            "Net Qty: 25 g | MRP Rs. 499.00 (Incl. of all taxes)",
            "Mfg: Apex Global Formulations, Guangzhou",
            "Batch: AG-2026-91 | NO IMPORTER ADDRESS FOUND");

        createMockLabel(new File(dir, "mock_label_protein_1kg.png"),
            "NUTRIBOOST PLANT PROTEIN POWDER",
            "Net Wt: 1 kg | MRP Rs. 2199.00 (Incl. of all taxes)",
            "Mfg By: NutriBoost Labs, Andheri, Mumbai - 400053",
            "Customer Care: support@nutriboost.in | Country of Origin: India");

        createMockLabel(new File(dir, "mock_label_charger_65w.png"),
            "VOLTMAX 65W GaN FAST CHARGER",
            "Net Qty: 1 Unit | MRP Rs. 1899.00 (Incl. of all taxes)",
            "Imported & Packed by: Apex Global Traders, Okhla, New Delhi",
            "Country of Origin: China | Care: support@apexglobalkart.com");
    }

    private void createMockLabel(File file, String line1, String line2, String line3, String line4) {
        if (file.exists()) {
            return;
        }
        try {
            int width = 700;
            int height = 450;
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();

            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // Background
            g.setColor(new Color(248, 250, 252));
            g.fillRect(0, 0, width, height);

            // Border & Header panel
            g.setColor(new Color(10, 37, 64)); // Deep DCA Navy
            g.fillRect(0, 0, width, 55);

            g.setColor(Color.WHITE);
            g.setFont(new Font("SansSerif", Font.BOLD, 18));
            g.drawString("LEGAL METROLOGY DECLARATION PANEL (RULE 6)", 25, 35);

            // Outer bounding frame
            g.setColor(new Color(203, 213, 225));
            g.setStroke(new BasicStroke(2));
            g.drawRect(15, 65, width - 30, height - 80);

            // Content text
            g.setColor(new Color(15, 23, 42));
            g.setFont(new Font("SansSerif", Font.BOLD, 19));
            g.drawString(line1, 35, 110);

            g.setFont(new Font("SansSerif", Font.BOLD, 16));
            g.setColor(new Color(2, 132, 199)); // Blue highlight for MRP / Net Qty
            g.drawString(line2, 35, 160);

            g.setFont(new Font("SansSerif", Font.PLAIN, 15));
            g.setColor(new Color(51, 65, 85));
            g.drawString(line3, 35, 210);
            g.drawString(line4, 35, 250);

            // Simulated Barcode area
            g.setColor(new Color(226, 232, 240));
            g.fillRect(35, 300, 220, 80);
            g.setColor(Color.BLACK);
            for (int x = 45; x < 240; x += 5) {
                int barW = (x % 3 == 0) ? 3 : 1;
                g.fillRect(x, 310, barW, 55);
            }
            g.setFont(new Font("Monospaced", Font.PLAIN, 11));
            g.drawString("8 901030 456789", 75, 375);

            // Stamp
            g.setColor(new Color(16, 185, 129));
            g.setFont(new Font("SansSerif", Font.BOLD, 12));
            g.drawString("STANDARD STATUTORY PACKAGING • VERIFIED FORMAT", 280, 345);

            g.dispose();
            ImageIO.write(image, "png", file);
            log.info("Generated synthetic mock label image: {}", file.getAbsolutePath());
        } catch (IOException e) {
            log.warn("Could not generate mock label image: {}", e.getMessage());
        }
    }
}
