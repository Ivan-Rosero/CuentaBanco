package org.ivanmros.test.springboot.app.services;

import org.ivanmros.test.springboot.app.models.Cuenta;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

//@Service
public interface CuentaService {
    Cuenta findById(Long id);
    int revisarTotalTransferencias(Long bancoId);

    BigDecimal revisarSaldo(Long cuentaId);

    void transferir(Long numCuentaOrigen, Long numCuentaDestino, BigDecimal monto, Long bancoId);
}
