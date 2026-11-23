package com.example.lmchecker;

import com.example.lmchecker.dto.BatchIngestResultDto;
import com.example.lmchecker.dto.ProductCreateRequest;
import com.example.lmchecker.entity.Product;
import com.example.lmchecker.ingestion.BatchIngestionService;
import com.example.lmchecker.ingestion.MockProductDataSource;
import com.example.lmchecker.repository.ProductRepository;
import com.example.lmchecker.storage.FileStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
class IngestionTests {

    @Autowired
    private BatchIngestionService batchIngestionService;

    @Autowired
    private MockProductDataSource mockProductDataSource;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Test
    void testMockProductDataSource() {
        var product = mockProductDataSource.fetchProductFromUrl("https://retailbharat.in/p/swarna-atta-5kg");
        assertNotNull(product);
        assertEquals("Swarna Shakti Foods", product.brandName());
        assertEquals(320.00, product.declaredMrp());

        var batch = mockProductDataSource.fetchSimulatedCrawlBatch(3);
        assertEquals(3, batch.size());
    }

    @Test
    void testFileStorageService() {
        byte[] bytes = "Sample Image Content".getBytes(StandardCharsets.UTF_8);
        String path = fileStorageService.storeOriginal(bytes, "test_label.png");
        assertNotNull(path);
        assertTrue(path.contains("original/"));
        assertTrue(fileStorageService.exists(path));
    }

    @Test
    void testManualProductIngestion() {
        long initialCount = productRepository.count();

        ProductCreateRequest request = new ProductCreateRequest();
        request.setName("Fresh Test Organic Mustard Oil 1L");
        request.setBrandName("PureDrops Organic Dairy");
        request.setSellerName("RetailBharat Supermart LLP");
        request.setCategoryName("Edible Oils & Ghee");
        request.setDeclaredMrp(215.00);
        request.setListingPrice(199.00);
        request.setCountryOfOrigin("India");
        request.setDescription("100% cold pressed mustard oil.");

        Product created = batchIngestionService.ingestManual(request, "storage/uploads/mock_label_atta_5kg.png");
        assertNotNull(created.getId());
        assertEquals(initialCount + 1, productRepository.count());
    }

    @Test
    void testCsvBatchIngestion() {
        String csvData = "name,brand,seller,category,declared_mrp,listing_price,country_of_origin,product_url\n" +
                         "Organic Roasted Almonds 200g,Vedika Herbal Care,Apex Global Traders,Packaged Food & Staples,250.00,220.00,India,https://shop.in/p1\n" +
                         "Cold Pressed Coconut Oil 500ml,PureDrops Organic Dairy,Western Imports & Marketing,Edible Oils & Ghee,180.00,165.00,India,https://shop.in/p2\n";

        MockMultipartFile csvFile = new MockMultipartFile(
            "file", "products.csv", "text/csv", csvData.getBytes(StandardCharsets.UTF_8)
        );

        long beforeCount = productRepository.count();
        BatchIngestResultDto result = batchIngestionService.ingestCsv(csvFile);

        assertEquals(2, result.getTotalProcessed());
        assertEquals(2, result.getSuccessCount());
        assertEquals(0, result.getFailureCount());
        assertEquals(beforeCount + 2, productRepository.count());
    }

    @Test
    void testSimulatedCrawlBatchIngestion() {
        long beforeCount = productRepository.count();
        BatchIngestResultDto result = batchIngestionService.ingestSimulatedCrawl(2);

        assertEquals(2, result.getTotalProcessed());
        assertEquals(2, result.getSuccessCount());
        assertEquals(beforeCount + 2, productRepository.count());
    }
}
