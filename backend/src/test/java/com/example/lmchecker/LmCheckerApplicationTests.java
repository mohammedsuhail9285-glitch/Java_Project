package com.example.lmchecker;

import com.example.lmchecker.entity.Role;
import com.example.lmchecker.entity.User;
import com.example.lmchecker.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
class LmCheckerApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RuleRepository ruleRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private ViolationRepository violationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void contextLoadsAndSeedDataVerified() {
        // 1. Verify Seed Users and Authentication Credentials
        User admin = userRepository.findByUsername("admin").orElse(null);
        assertNotNull(admin, "Admin user must be seeded");
        assertEquals(Role.ADMIN, admin.getRole());
        assertTrue(passwordEncoder.matches("Admin@123", admin.getPasswordHash()));

        User regulator = userRepository.findByUsername("regulator").orElse(null);
        assertNotNull(regulator, "Regulator user must be seeded");
        assertEquals(Role.REGULATOR, regulator.getRole());
        assertTrue(passwordEncoder.matches("Regulator@123", regulator.getPasswordHash()));

        User reviewer = userRepository.findByUsername("reviewer").orElse(null);
        assertNotNull(reviewer, "Reviewer user must be seeded");
        assertEquals(Role.REVIEWER, reviewer.getRole());
        assertTrue(passwordEncoder.matches("Reviewer@123", reviewer.getPasswordHash()));

        User analyst = userRepository.findByUsername("analyst").orElse(null);
        assertNotNull(analyst, "Analyst user must be seeded");
        assertEquals(Role.ANALYST, analyst.getRole());
        assertTrue(passwordEncoder.matches("Analyst@123", analyst.getPasswordHash()));

        // 2. Verify Statutory Legal Metrology Rules (LM001 - LM010)
        long ruleCount = ruleRepository.count();
        assertTrue(ruleCount >= 10, "At least 10 seeded rules (LM001-LM010) must be present. Found: " + ruleCount);
        assertTrue(ruleRepository.findByRuleId("LM001").isPresent());
        assertTrue(ruleRepository.findByRuleId("LM002").isPresent());
        assertTrue(ruleRepository.findByRuleId("LM003").isPresent());
        assertTrue(ruleRepository.findByRuleId("LM008").isPresent());

        // 3. Verify Product Catalog
        long productCount = productRepository.count();
        assertTrue(productCount >= 50, "At least 50 synthetic products must be seeded. Found: " + productCount);

        // 4. Verify Categories, Sellers, Brands, Violations
        assertTrue(categoryRepository.count() >= 10);
        assertTrue(sellerRepository.count() >= 8);
        assertTrue(brandRepository.count() >= 12);
        assertTrue(violationRepository.count() >= 5);
    }
}
