package ru.otus.bank.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import ru.otus.bank.dao.AgreementDao;
import ru.otus.bank.entity.Agreement;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AgreementServiceImplTest {

    private AgreementDao agreementDao = mock(AgreementDao.class);

    AgreementServiceImpl agreementServiceImpl;

    @BeforeEach
    public void init() {
        agreementServiceImpl = new AgreementServiceImpl(agreementDao);
    }

    @Test
    public void testFindByName() {
        String name = "test";
        Agreement agreement = new Agreement();
        agreement.setId(10L);
        agreement.setName(name);

        when(agreementDao.findByName(name)).thenReturn(
                Optional.of(agreement));

        Optional<Agreement> result = agreementServiceImpl.findByName(name);

        assertTrue(result.isPresent());
        assertEquals(10, agreement.getId());
    }

    @Test
    public void testFindByNameWithCaptor() {
        String name = "test";
        Agreement agreement = new Agreement();
        agreement.setId(10L);
        agreement.setName(name);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        when(agreementDao.findByName(captor.capture())).thenReturn(
                Optional.of(agreement));

        Optional<Agreement> result = agreementServiceImpl.findByName(name);

        assertEquals("test", captor.getValue());
        assertTrue(result.isPresent());
        assertEquals(10, agreement.getId());
    }
    @Test
    public void addAgreementTest(){
        String agreementName = "name";
        Agreement agreement = new Agreement();
        agreement.setName(agreementName);

        ArgumentMatcher<Agreement> matcher =
                argument->
                        argument != null
                        && argument.getName().equals(agreementName);

        when(agreementDao.save(argThat(matcher))).thenReturn(agreement);

        assertEquals(agreement, agreementServiceImpl.addAgreement(agreementName));

        verify(agreementDao, times(1)).save(argThat(matcher));
    }


}
