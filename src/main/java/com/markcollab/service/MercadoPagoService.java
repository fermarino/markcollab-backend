package com.markcollab.service;

import com.markcollab.model.Project;
import com.markcollab.model.Employer;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferencePayerRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class MercadoPagoService {

    private final PreferenceClient preferenceClient;

    @Value("${frontend.base-url}")
    private String frontendBaseUrl;

    @Value("${mercadopago.webhook-url}") // <-- INJETANDO A NOVA PROPRIEDADE
    private String mercadoPagoWebhookUrl; // <-- Nova variável para a URL do webhook

    public MercadoPagoService() {
        this.preferenceClient = new PreferenceClient();
    }

    public String createPaymentPreference(Project project, Employer employer) throws MPException, MPApiException {
        // Crie um item para o pagamento (o projeto)
        PreferenceItemRequest itemRequest =
                PreferenceItemRequest.builder()
                        .id(String.valueOf(project.getProjectId())) // Usando o ID do projeto como ID do item
                        .title(project.getProjectTitle())
                        .description(project.getProjectDescription())
                        .quantity(1)
                        .currencyId("BRL") // Moeda Brasileira
                        .unitPrice(new BigDecimal(project.getProjectPrice()))
                        .build();

        List<PreferenceItemRequest> items = new ArrayList<>();
        items.add(itemRequest);

        // Crie os dados do pagador
        PreferencePayerRequest payerRequest =
                PreferencePayerRequest.builder()
                        .name(employer.getName())
                        .email(employer.getEmail())
                        .build();

        String successUrl = frontendBaseUrl + "/pagamento/sucesso";
        String pendingUrl = frontendBaseUrl + "/pagamento/pendente";
        String failureUrl = frontendBaseUrl + "/pagamento/falha";

        System.out.println("DEBUG MP: Gerando URL de sucesso como: " + successUrl);
        System.out.println("DEBUG MP: Gerando URL do Webhook como: " + mercadoPagoWebhookUrl); // <-- DEBUG DO WEBHHOOK

        PreferenceRequest preferenceRequest =
                PreferenceRequest.builder()
                        .items(items)
                        .payer(payerRequest)
                        .backUrls(com.mercadopago.client.preference.PreferenceBackUrlsRequest.builder()
                                .success(successUrl)
                                .pending(pendingUrl)
                                .failure(failureUrl)
                                .build())
                        .notificationUrl(mercadoPagoWebhookUrl) // <-- AQUI ESTÁ A CORREÇÃO CRÍTICA!
                        .externalReference(String.valueOf(project.getProjectId())) // <-- MUITO IMPORTANTE PARA IDENTIFICAR O PROJETO NO WEBHHOOK
                        .autoReturn("approved")
                        .build();

        // Crie a preferência de pagamento
        Preference preference = preferenceClient.create(preferenceRequest);

        // Retorne a URL de inicialização (init_point)
        return preference.getInitPoint();
    }
}
