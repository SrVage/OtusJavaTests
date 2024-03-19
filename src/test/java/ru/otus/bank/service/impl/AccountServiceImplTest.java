package ru.otus.bank.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.bank.dao.AccountDao;
import ru.otus.bank.entity.Account;
import ru.otus.bank.entity.Agreement;
import ru.otus.bank.service.exception.AccountException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceImplTest {
    @Mock
    AccountDao accountDao;

    @InjectMocks
    AccountServiceImpl accountServiceImpl;

    @Test
    void testTransfer() {
        Account sourceAccount = new Account();
        sourceAccount.setAmount(new BigDecimal(100));

        Account destinationAccount = new Account();
        destinationAccount.setAmount(new BigDecimal(10));

        when(accountDao.findById(eq(1L))).thenReturn(Optional.of(sourceAccount));
        when(accountDao.findById(eq(2L))).thenReturn(Optional.of(destinationAccount));

        accountServiceImpl.makeTransfer(1L, 2L, new BigDecimal(10));

        assertEquals(new BigDecimal(90), sourceAccount.getAmount());
        assertEquals(new BigDecimal(20), destinationAccount.getAmount());
    }

    @Test
    void testSourceNotFound() {
        when(accountDao.findById(any())).thenReturn(Optional.empty());

        AccountException result = assertThrows(AccountException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                accountServiceImpl.makeTransfer(1L, 2L, new BigDecimal(10));
            }
        });
        assertEquals("No source account", result.getLocalizedMessage());
    }


    @Test
    void testTransferWithVerify() {
        Account sourceAccount = new Account();
        sourceAccount.setAmount(new BigDecimal(100));
        sourceAccount.setId(1L);

        Account destinationAccount = new Account();
        destinationAccount.setAmount(new BigDecimal(10));
        destinationAccount.setId(2L);

        when(accountDao.findById(eq(1L))).thenReturn(Optional.of(sourceAccount));
        when(accountDao.findById(eq(2L))).thenReturn(Optional.of(destinationAccount));

        ArgumentMatcher<Account> sourceMatcher =
                argument -> argument.getId().equals(1L) && argument.getAmount().equals(new BigDecimal(90));

        ArgumentMatcher<Account> destinationMatcher =
                argument -> argument.getId().equals(2L) && argument.getAmount().equals(new BigDecimal(20));

        accountServiceImpl.makeTransfer(1L, 2L, new BigDecimal(10));

        verify(accountDao).save(argThat(sourceMatcher));
        verify(accountDao).save(argThat(destinationMatcher));
        }

    @Test
    void getAccountsTest() {
        Account firstAccount = new Account();
        firstAccount.setId(1L);
        firstAccount.setAmount(new BigDecimal(100));
        Account secondAccount = new Account();
        secondAccount.setId(1L);
        secondAccount.setAmount(new BigDecimal(200));
        List<Account> testAccounts = new ArrayList<>();
        testAccounts.add(firstAccount);
        testAccounts.add(secondAccount);
        Agreement agreement = new Agreement();
        agreement.setId(1L);
        when(accountDao.findByAgreementId(eq(1L))).thenReturn(testAccounts);
        List<Account> accounts = accountServiceImpl.getAccounts(agreement);
        assertArrayEquals(testAccounts.toArray(), accounts.toArray());
        verify(accountDao, times(1)).findByAgreementId(eq(1L));
    }

    @Test
    void getAllAccountsTest() {
        Account firstAccount = new Account();
        firstAccount.setId(1L);
        firstAccount.setAmount(new BigDecimal(100));
        Account secondAccount = new Account();
        secondAccount.setId(2L);
        firstAccount.setAmount(new BigDecimal(200));
        List<Account> testAccounts = new ArrayList<>();
        testAccounts.add(firstAccount);
        testAccounts.add(secondAccount);
        when(accountDao.findAll()).thenReturn(testAccounts);
        List<Account> accounts = accountServiceImpl.getAccounts();
        assertArrayEquals(testAccounts.toArray(), accounts.toArray());
        verify(accountDao, times(1)).findAll();
    }

    @Test
    void chargedTest(){
        BigDecimal initialAmount = new BigDecimal(100);
        BigDecimal chargeAmount = new BigDecimal(50);

        Account firstAccount = new Account();
        firstAccount.setId(1L);
        firstAccount.setAmount(initialAmount);

        when(accountDao.findById(eq(1L))).thenReturn(Optional.of(firstAccount));

        assertTrue(accountServiceImpl.charge(1L, chargeAmount));
        assertEquals(initialAmount.subtract(chargeAmount), firstAccount.getAmount());

        verify(accountDao, times(1)).findById(1L);
        verify(accountDao, times(1)).save(firstAccount);
    }

    @Test
    void chargedExceptionTest(){
        BigDecimal chargeAmount = new BigDecimal(50);

        when(accountDao.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(AccountException.class, () -> accountServiceImpl.charge(10L, chargeAmount));

        verify(accountDao, times(1)).findById(anyLong());
        verify(accountDao, never()).save(any());
    }

    @Test
    void addAccountTest(){
        Long agreementId = 5L;
        Agreement agreement = new Agreement();
        agreement.setId(agreementId);
        String accountNumber = "acc_num";
        Integer type = 43;
        BigDecimal amount = new BigDecimal(100);

        Account account = new Account();
        account.setAgreementId(agreement.getId());
        account.setNumber(accountNumber);
        account.setType(type);
        account.setAmount(amount);

        ArgumentMatcher<Account> matcher =
                argument ->
                        argument != null
                        && argument.getNumber().equals(accountNumber)
                        && argument.getType().equals(type)
                        && argument.getAmount().equals(amount);

        when(accountDao.save(argThat(matcher))).thenReturn(account);

        assertEquals(account, accountServiceImpl.addAccount(agreement, accountNumber, type, amount));

        verify(accountDao, times(1)).save(argThat(matcher));
    }
}
