package com.example.datasyncapp;

import com.example.datasyncapp.services.ProviderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class DataSyncAppApplicationTests {

    /**
     * This can be done (@Autowired : DI) because we've used @SpringBootTest which load the entire application context.
     * It can't be done @RunWith(MockitoJUnitRunner.class) way.
     */
    @Autowired
    ProviderService providerService;

    @Autowired
    ApplicationContext context;

    @Test
    void contextLoads() {
        assertTrue(context != null);
    }

    @Test
    void testTheLogic() {
        // when(providerService.getProcessedProviders(any())).thenReturn(null);
    }

}
