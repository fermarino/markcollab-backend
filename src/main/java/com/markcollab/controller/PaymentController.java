package com.markcollab.controller;

import com.markcollab.model.Project;
import com.markcollab.model.Employer;
import com.markcollab.service.MercadoPagoService;
import com.markcollab.service.ProjectService;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "https://markcollab-backend.onrender.com")
public class PaymentController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private MercadoPagoService mercadoPagoService;

    /**
     * POST /api/payment/{projectId}/pay/{employerCpf}
     * Inicia o fluxo de pagamento no Mercado Pago e retorna a URL de checkout.
     */
    @PostMapping("/{projectId}/pay/{employerCpf}")
    public ResponseEntity<?> payProject(
            @PathVariable Long projectId,
            @PathVariable String employerCpf) {

        try {
            // Busca o projeto e o empregador
            Project project = projectService.findProjectById(projectId);
            Employer employer = projectService.findEmployerByCpf(employerCpf);

            // Verifica se o empregador é dono do projeto
            if (project.getProjectEmployer() == null || !project.getProjectEmployer().getCpf().equals(employerCpf)) {
                return ResponseEntity.status(403).body("Unauthorized: Only the project owner can make the payment.");
            }

            // Verifica o status do projeto
            if (project.getStatus() == null ||
                    (!project.getStatus().equals("Aberto") && !project.getStatus().equals("Em andamento"))) {
                return ResponseEntity.badRequest().body("Project status does not allow payment initiation.");
            }

            // Cria a preferência de pagamento no Mercado Pago
            String checkoutUrl = mercadoPagoService.createPaymentPreference(project, employer);

            // Retorna a URL do checkout para o frontend
            return ResponseEntity.ok(checkoutUrl);

        } catch (MPApiException e) {
            System.err.println("Mercado Pago API error: " + e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body("Error processing Mercado Pago payment: " + e.getMessage());
        } catch (MPException e) {
            System.err.println("Mercado Pago SDK error: " + e.getMessage());
            return ResponseEntity.status(500).body("Internal error processing Mercado Pago payment.");
        } catch (RuntimeException e) {
            System.err.println("Error fetching project/employer or validation: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Unexpected error while initiating payment.");
        }
    }
}
