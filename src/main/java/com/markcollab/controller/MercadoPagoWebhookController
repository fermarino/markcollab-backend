package com.markcollab.controller;

import com.markcollab.model.Project;
import com.markcollab.service.ProjectService;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/mercadopago")
public class MercadoPagoWebhookController {

    @Autowired
    private ProjectService projectService;

    private final PaymentClient paymentClient;

    public MercadoPagoWebhookController() {
        this.paymentClient = new PaymentClient();
    }

    /**
     * POST /api/mercadopago/webhook
     * Endpoint que o Mercado Pago chamará para notificar sobre eventos de pagamento.
     * Agora com logs detalhados para depurar o erro 500.
     */
    @PostMapping("/webhook")
    public ResponseEntity<String> receiveWebhook(@RequestBody Map<String, Object> requestBody) {
        System.out.println("✅ DEBUG WEBHOOK: Requisição POST de webhook do Mercado Pago RECEBIDA no controller.");
        System.out.println("DEBUG WEBHOOK: Payload RAW recebido: " + requestBody); // Log do payload completo

        // Sempre retorne 200 OK para o Mercado Pago, mesmo em caso de erro interno,
        // para evitar retentativas infinitas. O log de erro é para você.
        HttpStatus responseStatus = HttpStatus.OK;
        String responseMessage = "Webhook processado com sucesso.";

        try {
            // 1. Validar a origem da notificação (MUITO IMPORTANTE para produção!)
            // Recomendo fortemente implementar a validação do cabeçalho 'x-signature' aqui.
            // Exemplo: https://www.mercadopago.com.br/developers/pt/docs/your-integrations/notifications/webhooks#_validar_a_origem_da_notificao

            String topic = (String) requestBody.get("type"); // Pode ser 'type' ou 'topic' dependendo da versão/tipo de evento
            Map<String, Object> data = (Map<String, Object>) requestBody.get("data"); // Pode ser Map<String, String> ou Map<String, Object>

            if (data == null || !data.containsKey("id")) {
                System.err.println("DEBUG WEBHOOK: Payload 'data' ou 'data.id' ausente. Ignorando webhook.");
                responseMessage = "Payload inválido.";
                return new ResponseEntity<>(responseMessage, responseStatus);
            }

            String paymentIdStr = String.valueOf(data.get("id"));
            Long paymentId = null;
            try {
                paymentId = Long.valueOf(paymentIdStr);
            } catch (NumberFormatException e) {
                System.err.println("DEBUG WEBHOOK: Erro ao converter paymentId para Long: " + paymentIdStr);
                responseMessage = "ID de pagamento inválido.";
                return new ResponseEntity<>(responseMessage, responseStatus);
            }

            if (!("payment".equals(topic) || "collection".equals(topic) || "merchant_order".equals(topic))) {
                System.out.println("DEBUG WEBHOOK: Tópico de webhook ignorado: " + topic + ", ID: " + paymentId);
                responseMessage = "Tópico não relevante.";
                return new ResponseEntity<>(responseMessage, responseStatus);
            }

            System.out.println("DEBUG WEBHOOK: Processando tópico: " + topic + ", Payment ID: " + paymentId);

            // 2. Obter os detalhes completos do pagamento usando a API do Mercado Pago
            // Isso é mais seguro e garante que você tem o status mais recente.
            Payment payment = paymentClient.get(paymentId);

            if (payment == null) {
                System.err.println("DEBUG WEBHOOK: Pagamento não encontrado na API do Mercado Pago para o ID: " + paymentId);
                responseMessage = "Pagamento não encontrado.";
                // Embora o recurso não exista, ainda retornamos 200 OK para o MP para evitar retries.
                return new ResponseEntity<>(responseMessage, responseStatus);
            }

            String paymentStatus = payment.getStatus();
            String externalReference = payment.getExternalReference(); // Deve ser o ID do seu projeto

            System.out.println("DEBUG WEBHOOK: Status do pagamento obtido do MP: " + paymentStatus + ", Referência Externa (ID do Projeto): " + externalReference);

            // 3. Verificar o status do pagamento e atualizar o projeto
            if ("approved".equalsIgnoreCase(paymentStatus) && externalReference != null && !externalReference.isEmpty()) {
                Long projectId = null;
                try {
                    projectId = Long.valueOf(externalReference);
                } catch (NumberFormatException e) {
                    System.err.println("DEBUG WEBHOOK: Erro ao converter externalReference para Long: " + externalReference);
                    responseMessage = "Referência externa inválida.";
                    return new ResponseEntity<>(responseMessage, responseStatus);
                }

                Project projectToUpdate = projectService.findProjectById(projectId);

                if (projectToUpdate != null) {
                    // Validação adicional: Se o projeto já está "Em andamento" ou "Concluído", não atualize novamente.
                    if (!"Em andamento".equals(projectToUpdate.getStatus()) && !"Concluído".equals(projectToUpdate.getStatus())) {
                        String employerCpf = projectToUpdate.getProjectEmployer().getCpf();
                        // Chame o método de serviço para atualizar o status
                        projectService.updateProjectStatus(projectId, "Em andamento", employerCpf);
                        System.out.println("DEBUG WEBHOOK: Projeto " + projectId + " ATUALIZADO para 'Em andamento'.");
                        responseMessage = "Projeto atualizado com sucesso.";
                    } else {
                        System.out.println("DEBUG WEBHOOK: Projeto " + projectId + " já está 'Em andamento' ou 'Concluído'. Nenhuma atualização necessária.");
                        responseMessage = "Projeto já atualizado.";
                    }
                } else {
                    System.err.println("DEBUG WEBHOOK: Erro: Projeto não encontrado com ID: " + projectId + " para atualização via webhook.");
                    responseMessage = "Projeto não encontrado para atualização.";
                }
            } else {
                System.out.println("DEBUG WEBHOOK: Pagamento não aprovado ou referência externa ausente/vazia. Status: " + paymentStatus);
                responseMessage = "Pagamento não aprovado ou dados incompletos.";
            }

        } catch (MPApiException e) {
            System.err.println("DEBUG WEBHOOK: Erro na API do Mercado Pago ao buscar pagamento: " + (e.getApiResponse() != null ? new String(e.getApiResponse().getContent()) : e.getMessage()));
            responseMessage = "Erro API Mercado Pago.";
            // responseStatus já é OK para o MP, o importante é logar o erro para você.
        } catch (MPException e) {
            System.err.println("DEBUG WEBHOOK: Erro SDK do Mercado Pago no webhook: " + e.getMessage());
            responseMessage = "Erro SDK Mercado Pago.";
        } catch (Exception e) {
            System.err.println("DEBUG WEBHOOK: Erro INESPERADO ao processar webhook: " + e.getMessage());
            e.printStackTrace(); // Imprimir stack trace completo para depuração
            responseMessage = "Erro interno inesperado.";
        }
        return new ResponseEntity<>(responseMessage, responseStatus);
    }
}
