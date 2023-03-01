package org.ivanmros.test.springboot.app;

import org.ivanmros.test.springboot.app.models.Cuenta;
import org.ivanmros.test.springboot.app.repositories.BancoRepository;
import org.ivanmros.test.springboot.app.repositories.CuentaRepository;
import org.ivanmros.test.springboot.app.services.CuentaServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = {CuentaServiceImpl.class})
public class IntegracionJpaTest {

    @InjectMocks
    CuentaServiceImpl cuentaServiceImpl;
    @Autowired
    CuentaRepository cuentaRepository;
    @Autowired
    BancoRepository bancoRepository;

    @BeforeAll
    void init(){
        cuentaServiceImpl = new CuentaServiceImpl(cuentaRepository, bancoRepository);
    }

    @Test
    void testFindById() {
        Optional<Cuenta> cuenta = cuentaRepository.findById(1L);
        assertTrue(cuenta.isPresent());
        assertEquals("Ivan", cuenta.orElseThrow().getPersona());
    }

    @Test
    void testFinByPersonaThrowException() {
        Optional<Cuenta> cuenta = cuentaRepository.findByPersona("Mauricio");
        assertThrows(NoSuchElementException.class, cuenta::orElseThrow);
        assertFalse(cuenta.isPresent());

    }

    @Test
    void testFindAll() {
        List<Cuenta> cuentas = cuentaRepository.findAll();
        assertFalse(cuentas.isEmpty());
        assertEquals(2, cuentas.size());

    }

    @Test
    void testSave() {
        //Given
        Cuenta cuentaIvan = new Cuenta(null, "Ivan", new BigDecimal("3000"));
        cuentaRepository.save(cuentaIvan);

        //When
        Cuenta cuenta = cuentaRepository.findByPersona("Ivan").orElseThrow();

        //Then
        assertEquals("Ivan", cuenta.getPersona());
        assertEquals("3000", cuenta.getSaldo().toPlainString());
        assertEquals(3, cuenta.getId());
    }

    @Test
    void testUpdate() {
        //Given
        Cuenta cuentaIvan = new Cuenta(null, "Ivan", new BigDecimal("3000"));

        //When
        Cuenta cuenta = cuentaRepository.save(cuentaIvan);

        //Then
        assertEquals("Ivan", cuenta.getPersona());
        assertEquals("3000", cuenta.getSaldo().toPlainString());
        //assertEquals(3, cuenta.getId());


        //When
        cuenta.setSaldo(new BigDecimal("3800"));
        Cuenta cuentaActualizada = cuentaRepository.save(cuenta);

        //Then
        assertEquals("Ivan", cuentaActualizada.getPersona());
        assertEquals("3800", cuentaActualizada.getSaldo().toPlainString());
    }

    @Test
    void testDelete() {
        Cuenta cuenta = cuentaRepository.findById(2L).orElseThrow();
        assertEquals("Ivan", cuenta.getPersona());

        cuentaRepository.delete(cuenta);

        assertThrows(NoSuchElementException.class, () -> {
            cuentaRepository.findByPersona("Ivan").orElseThrow();
        });
        assertEquals(1, cuentaRepository.findAll().size());
    }
}
