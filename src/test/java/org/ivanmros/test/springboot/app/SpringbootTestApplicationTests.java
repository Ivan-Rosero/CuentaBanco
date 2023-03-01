package org.ivanmros.test.springboot.app;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.ivanmros.test.springboot.app.Datos.*;


import jakarta.inject.Inject;
import org.ivanmros.test.springboot.app.exceptions.DineroInsuficienteException;
import org.ivanmros.test.springboot.app.models.Banco;
import org.ivanmros.test.springboot.app.models.Cuenta;
import org.ivanmros.test.springboot.app.repositories.BancoRepository;
import org.ivanmros.test.springboot.app.repositories.CuentaRepository;
import org.ivanmros.test.springboot.app.services.CuentaService;
import org.ivanmros.test.springboot.app.services.CuentaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;


@SpringBootTest(classes = SpringbootTestApplication.class)
@ContextConfiguration(classes = {CuentaServiceImpl.class})
class SpringbootTestApplicationTests {
	@MockBean
	CuentaRepository cuentaRepository;
	@MockBean
	BancoRepository bancoRepository;
//	@InjectMocks
//	CuentaServiceImpl service;

	@Autowired
	CuentaService service;

	@BeforeEach
	void setUp() {
//		cuentaRepository = mock(CuentaRepository.class);
//		bancoRepository = mock(BancoRepository.class);
//		service = new CuentaServiceImpl(cuentaRepository, bancoRepository);
//		Datos.CUENTA_001.setSaldo(new BigDecimal("1000"));
//		Datos.CUENTA_002.setSaldo(new BigDecimal("2000"));
//		Datos.BANCO.setTotalTransferencia(0);
	}

	@Test
	void contextLoads() {
		//Given
		when(cuentaRepository.findById(1L)).thenReturn(Datos.crearCuenta001());
		when(cuentaRepository.findById(2L)).thenReturn(Datos.crearCuenta002());
		when(bancoRepository.findById(1L)).thenReturn(Datos.crearBanco());

		BigDecimal saldoOrigen = service.revisarSaldo(1L);
		System.out.println(saldoOrigen);
		BigDecimal saldoDestino = service.revisarSaldo(2L);
		System.out.println(saldoDestino);
		assertEquals("1000", saldoOrigen.toPlainString());
		assertEquals("2000", saldoDestino.toPlainString());

		service.transferir(1L, 2L, new BigDecimal("100"), 1L);

		saldoOrigen = service.revisarSaldo(1L);
		saldoDestino = service.revisarSaldo(2L);

		assertEquals("900", saldoOrigen.toPlainString());
		assertEquals("2100", saldoDestino.toPlainString());

		//Comprobar que solo se realice una transferencia
		int total = service.revisarTotalTransferencias(1L);
		assertEquals(1, total);

		//Verificar que se invocan las cantidades de veces correctas las actualizaciones y busquedas por Id
		verify(cuentaRepository, times(3)).findById(1L);
		verify(cuentaRepository, times(3)).findById(2L);
		verify(cuentaRepository, times(2)).save(any(Cuenta.class));

		//Verificar que se realiza la busqueda por id y actualización del banco
		verify(bancoRepository, times(2)).findById(1L);
		verify(bancoRepository).save(any(Banco.class));

		verify(cuentaRepository, times(6)).findById(anyLong());
		verify(cuentaRepository, never()).findAll();

	}

	@Test
	void contextLoads2() {
		//Given
		when(cuentaRepository.findById(1L)).thenReturn(Datos.crearCuenta001());
		when(cuentaRepository.findById(2L)).thenReturn(Datos.crearCuenta002());
		when(bancoRepository.findById(1L)).thenReturn(Datos.crearBanco());

		BigDecimal saldoOrigen = service.revisarSaldo(1L);
		System.out.println(saldoOrigen);
		BigDecimal saldoDestino = service.revisarSaldo(2L);
		System.out.println(saldoDestino);
		assertEquals("1000", saldoOrigen.toPlainString());
		assertEquals("2000", saldoDestino.toPlainString());


		assertThrows(DineroInsuficienteException.class, () ->{
			service.transferir(1L, 2L, new BigDecimal("1200"), 1L);

		});

		saldoOrigen = service.revisarSaldo(1L);
		saldoDestino = service.revisarSaldo(2L);

		assertEquals("1000", saldoOrigen.toPlainString());
		assertEquals("2000", saldoDestino.toPlainString());

		//Comprobar que solo se realice una transferencia
		int total = service.revisarTotalTransferencias(1L);
		assertEquals(0, total);

		//Verificar que se invocan las cantidades de veces correctas las actualizaciones y busquedas por Id
		verify(cuentaRepository, times(3)).findById(1L);
		verify(cuentaRepository, times(2)).findById(2L);
		verify(cuentaRepository, never()).save(any(Cuenta.class));

		//Verificar que se realiza la busqueda por id y actualización del banco
		verify(bancoRepository, times(1)).findById(1L);
		verify(bancoRepository, never()).save(any(Banco.class));

		verify(cuentaRepository, times(5)).findById(anyLong());
		verify(cuentaRepository, never()).findAll();

	}

	@Test
	void contextLoads3() {
		when(cuentaRepository.findById(1L)).thenReturn(crearCuenta001());

		Cuenta cuenta1 = service.findById(1L);
		Cuenta cuenta2 = service.findById(1L);

		assertSame(cuenta1, cuenta2);
		verify(cuentaRepository, times(2)).findById(1L);


	}
}
