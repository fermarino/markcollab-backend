package com.markcollab.config;

import com.mercadopago.MercadoPagoConfig; // <-- Este é o MercadoPagoConfig da SDK!
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MercadoPagoSdkConfiguration { // <-- SUA CLASSE RENOMEADA

    @Value("${mercadopago.access_token}")
    private String accessToken;

    @PostConstruct
    public void configureMercadoPago() {
        // Define o Access Token para a SDK do Mercado Pago
        MercadoPagoConfig.setAccessToken(accessToken); // <-- AQUI VOCÊ CHAMA A CLASSE DA SDK
        System.out.println("✅ Mercado Pago SDK configurado com Access Token.");
    }
}
