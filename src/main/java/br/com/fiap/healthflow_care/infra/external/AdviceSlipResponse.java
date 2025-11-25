package br.com.fiap.healthflow_care.infra.external;

public record AdviceSlipResponse(AdviceSlipResponse.Slip slip) {

    public record Slip(String slip_id, String advice) {
    }
}
